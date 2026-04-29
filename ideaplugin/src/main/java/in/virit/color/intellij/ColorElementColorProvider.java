package in.virit.color.intellij;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.ElementColorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiNewExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Renders a color swatch in the gutter for expressions using the
 * Viritin Color library, and rewrites them when the user picks a new color.
 *
 * Recognizes:
 * <ul>
 *   <li>{@code new RgbColor(r, g, b)} / {@code new RgbColor(r, g, b, a)}</li>
 *   <li>{@code RgbColor.of("rgb(...)")} / {@code new HexColor("#...")} /
 *       {@code HexColor.of("#...")} / {@code HslColor.of("hsl(...)")}</li>
 *   <li>{@code Color.parseCssColor("...")}</li>
 *   <li>{@code NamedColor.RED} (read-only swatch)</li>
 * </ul>
 */
public class ColorElementColorProvider implements ElementColorProvider {

    /**
     * Tracks element replacements made during a single picker session.
     *
     * The platform's ColorLineMarkerProvider captures the original PsiElement in a closure
     * and calls setColorTo() with that same reference on every picker drag. When we replace
     * the element wholesale (e.g. NamedColor.X → new RgbColor(...) or RGB 3→4-arg upgrade),
     * the original PsiElement is invalidated; subsequent calls would otherwise crash on
     * resolve() or fall silently. We map original → live replacement so the second drag
     * onward edits the new element in place.
     */
    private final Map<PsiElement, SmartPsiElementPointer<? extends PsiElement>> redirects =
            Collections.synchronizedMap(new WeakHashMap<>());

    @Override
    public @Nullable Color getColorFrom(@NotNull PsiElement element) {
        if (element instanceof PsiNewExpression newExpr) {
            return fromNewExpression(newExpr);
        }
        if (element instanceof PsiMethodCallExpression call) {
            return fromMethodCall(call);
        }
        if (element instanceof PsiReferenceExpression ref) {
            return fromNamedColorReference(ref);
        }
        return null;
    }

    @Override
    public void setColorTo(@NotNull PsiElement element, @NotNull Color color) {
        PsiElement live = currentElement(element);
        if (live == null) return;
        Project project = live.getProject();
        WriteCommandAction.runWriteCommandAction(project, "Change Color", null, () -> {
            PsiElement target = currentElement(element);
            if (target == null) return;
            applyColor(project, element, target, color);
        }, live.getContainingFile());
    }

    private @Nullable PsiElement currentElement(PsiElement original) {
        SmartPsiElementPointer<? extends PsiElement> p = redirects.get(original);
        if (p != null) {
            PsiElement el = p.getElement();
            if (el != null && el.isValid()) return el;
        }
        return original.isValid() ? original : null;
    }

    private void recordRedirect(Project project, PsiElement original, PsiElement replacement) {
        redirects.put(original, SmartPointerManager.getInstance(project).createSmartPsiElementPointer(replacement));
    }

    // ----- read --------------------------------------------------------------

    private @Nullable Color fromNewExpression(PsiNewExpression expr) {
        PsiJavaCodeReferenceElement ref = expr.getClassReference();
        if (ref == null) return null;
        String simple = ref.getReferenceName();
        if (!"RgbColor".equals(simple) && !"HexColor".equals(simple) && !"HslColor".equals(simple)) {
            return null;
        }
        String fqn = qualifiedName(ref);
        PsiExpressionList args = expr.getArgumentList();
        if (args == null) return null;
        PsiExpression[] a = args.getExpressions();

        if (ColorLib.RGB.equals(fqn)) return rgbFromIntArgs(a);
        if (ColorLib.HSL.equals(fqn)) return hslFromIntArgs(a);
        if (ColorLib.HEX.equals(fqn)) return a.length == 1 ? HexUtil.parseHex(stringValue(a[0])) : null;
        return null;
    }

    private @Nullable Color fromMethodCall(PsiMethodCallExpression call) {
        PsiReferenceExpression methodExpr = call.getMethodExpression();
        String name = methodExpr.getReferenceName();
        if (!"of".equals(name) && !"parseCssColor".equals(name)) return null;
        PsiExpression qualifier = methodExpr.getQualifierExpression();
        if (!(qualifier instanceof PsiReferenceExpression qref)) return null;
        String qName = qref.getReferenceName();
        if (!"RgbColor".equals(qName) && !"HexColor".equals(qName)
                && !"HslColor".equals(qName) && !"Color".equals(qName)) return null;

        PsiMethod method = call.resolveMethod();
        if (method == null) return null;
        PsiClass cls = method.getContainingClass();
        if (cls == null) return null;
        String fqn = cls.getQualifiedName();
        PsiExpression[] a = call.getArgumentList().getExpressions();
        if (a.length != 1) return null;
        String s = stringValue(a[0]);
        if (s == null) return null;

        if (ColorLib.HEX.equals(fqn)) return HexUtil.parseHex(s);
        if (ColorLib.RGB.equals(fqn)) return CssParse.rgb(s);
        if (ColorLib.HSL.equals(fqn)) return CssParse.hsl(s);
        if (ColorLib.COLOR.equals(fqn)) return CssParse.any(s);
        return null;
    }

    private @Nullable Color fromNamedColorReference(PsiReferenceExpression ref) {
        // Cheap pre-check: skip refs whose qualifier text doesn't look like NamedColor.
        PsiExpression q = ref.getQualifierExpression();
        if (q == null) return null;
        String qText = q.getText();
        if (qText == null) return null;
        if (!qText.equals("NamedColor") && !qText.endsWith(".NamedColor")) return null;

        // Verify the resolved target is our NamedColor enum (and not some unrelated class with the same name).
        PsiElement target = ref.resolve();
        if (!(target instanceof PsiEnumConstant ec)) return null;
        PsiClass cls = ec.getContainingClass();
        if (cls == null || !ColorLib.NAMED.equals(cls.getQualifiedName())) return null;

        // Compiled enum constants (resolved against the JAR) return null from getArgumentList(),
        // so look up by enum constant name in the static table.
        return NamedColorTable.colorFor(ec.getName());
    }

    // ----- write -------------------------------------------------------------

    private void applyColor(Project project, PsiElement original, PsiElement element, Color color) {
        PsiElementFactory f = JavaPsiFacade.getElementFactory(project);

        if (element instanceof PsiNewExpression newExpr) {
            String fqn = qualifiedName(newExpr.getClassReference());
            PsiExpression[] a = argsOf(newExpr);
            if (ColorLib.RGB.equals(fqn) && (a.length == 3 || a.length == 4)) {
                // Picker introduced alpha into a 3-arg constructor → upgrade to 4-arg form.
                if (a.length == 3 && color.getAlpha() != 255) {
                    PsiExpression replacement = f.createExpressionFromText(
                            "new RgbColor(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ", " + alphaStr(color) + ")",
                            newExpr);
                    PsiElement inserted = newExpr.replace(replacement);
                    recordRedirect(project, original, inserted);
                    return;
                }
                replace(a[0], f.createExpressionFromText(String.valueOf(color.getRed()), a[0]));
                replace(a[1], f.createExpressionFromText(String.valueOf(color.getGreen()), a[1]));
                replace(a[2], f.createExpressionFromText(String.valueOf(color.getBlue()), a[2]));
                if (a.length == 4) {
                    replace(a[3], f.createExpressionFromText(alphaStr(color), a[3]));
                }
                return;
            }
            if (ColorLib.HEX.equals(fqn) && a.length == 1) {
                replace(a[0], f.createExpressionFromText("\"" + HexUtil.toHex(color) + "\"", a[0]));
                return;
            }
            if (ColorLib.HSL.equals(fqn) && (a.length == 3 || a.length == 4)) {
                int[] hsl = toHsl(color);
                if (a.length == 3 && color.getAlpha() != 255) {
                    PsiExpression replacement = f.createExpressionFromText(
                            "new HslColor(" + hsl[0] + ", " + hsl[1] + ", " + hsl[2] + ", " + alphaStr(color) + ")",
                            newExpr);
                    PsiElement inserted = newExpr.replace(replacement);
                    recordRedirect(project, original, inserted);
                    return;
                }
                replace(a[0], f.createExpressionFromText(String.valueOf(hsl[0]), a[0]));
                replace(a[1], f.createExpressionFromText(String.valueOf(hsl[1]), a[1]));
                replace(a[2], f.createExpressionFromText(String.valueOf(hsl[2]), a[2]));
                if (a.length == 4) {
                    replace(a[3], f.createExpressionFromText(alphaStr(color), a[3]));
                }
                return;
            }
        }

        if (element instanceof PsiMethodCallExpression call) {
            PsiMethod m = call.resolveMethod();
            if (m == null) return;
            PsiClass cls = m.getContainingClass();
            if (cls == null) return;
            String fqn = cls.getQualifiedName();
            PsiExpression[] a = call.getArgumentList().getExpressions();
            if (a.length != 1) return;

            String newLiteral;
            if (ColorLib.HEX.equals(fqn) || ColorLib.COLOR.equals(fqn)) {
                newLiteral = HexUtil.toHex(color);
            } else if (ColorLib.RGB.equals(fqn)) {
                newLiteral = formatRgb(color);
            } else if (ColorLib.HSL.equals(fqn)) {
                newLiteral = formatHsl(color);
            } else {
                return;
            }
            replace(a[0], f.createExpressionFromText("\"" + newLiteral + "\"", a[0]));
            return;
        }

        if (element instanceof PsiReferenceExpression ref) {
            // Replace NamedColor.X with new RgbColor(...) — picker rarely lands on a named
            // color exactly, and RgbColor is the most direct representation of the picked value.
            //
            // Use the simple class name (not FQN) in the synthetic expression: putting an FQN
            // here trips JavaTreeCopyHandler.decodeInformation during replace() — it tries to
            // resolve the new ref while the tree is still in a DummyHolder. Instead, ensure
            // the import is present and emit "new RgbColor(...)".
            ensureImported(project, ref, ColorLib.RGB);
            String args;
            if (color.getAlpha() == 255) {
                args = color.getRed() + ", " + color.getGreen() + ", " + color.getBlue();
            } else {
                args = color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ", " + alphaStr(color);
            }
            PsiExpression replacement = f.createExpressionFromText("new RgbColor(" + args + ")", ref);
            PsiElement inserted = ref.replace(replacement);
            recordRedirect(project, original, inserted);
        }
    }

    private static void ensureImported(Project project, PsiElement context, String classFqn) {
        PsiFile file = context.getContainingFile();
        if (!(file instanceof PsiJavaFile javaFile)) return;
        PsiClass cls = JavaPsiFacade.getInstance(project).findClass(classFqn, context.getResolveScope());
        if (cls != null) {
            javaFile.importClass(cls); // no-op if already imported or in same package
        }
    }

    // ----- helpers -----------------------------------------------------------

    private static @Nullable Color rgbFromIntArgs(PsiExpression[] a) {
        if (a.length < 3) return null;
        Integer r = intValue(a[0]);
        Integer g = intValue(a[1]);
        Integer b = intValue(a[2]);
        if (r == null || g == null || b == null) return null;
        if (a.length == 3) return safeRgb(r, g, b, 255);
        if (a.length == 4) {
            Double alpha = doubleValue(a[3]);
            int alphaInt = alpha == null ? 255 : (int) Math.round(alpha * 255);
            return safeRgb(r, g, b, alphaInt);
        }
        return null;
    }

    private static @Nullable Color hslFromIntArgs(PsiExpression[] a) {
        if (a.length < 3) return null;
        Integer h = intValue(a[0]);
        Integer s = intValue(a[1]);
        Integer l = intValue(a[2]);
        if (h == null || s == null || l == null) return null;
        double alpha = 1.0;
        if (a.length == 4) {
            Double da = doubleValue(a[3]);
            if (da != null) alpha = da;
        }
        return CssParse.hslToColor(h, s, l, alpha);
    }

    private static Color safeRgb(int r, int g, int b, int a) {
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255 || a < 0 || a > 255) return null;
        return new Color(r, g, b, a);
    }

    private static String alphaStr(Color c) {
        return String.format(java.util.Locale.ROOT, "%.2f", c.getAlpha() / 255.0);
    }

    private static @Nullable String stringValue(PsiExpression e) {
        if (e instanceof PsiLiteralExpression lit && lit.getValue() instanceof String s) return s;
        return null;
    }

    private static @Nullable Integer intValue(PsiExpression e) {
        if (e instanceof PsiLiteralExpression lit && lit.getValue() instanceof Integer i) return i;
        return null;
    }

    private static @Nullable Double doubleValue(PsiExpression e) {
        if (e instanceof PsiLiteralExpression lit) {
            Object v = lit.getValue();
            if (v instanceof Double d) return d;
            if (v instanceof Float f) return f.doubleValue();
            if (v instanceof Integer i) return i.doubleValue();
        }
        return null;
    }

    private static String qualifiedName(@Nullable PsiJavaCodeReferenceElement ref) {
        if (ref == null) return "";
        PsiElement resolved = ref.resolve();
        if (resolved instanceof PsiClass cls && cls.getQualifiedName() != null) {
            return cls.getQualifiedName();
        }
        return "";
    }

    private static PsiExpression[] argsOf(PsiNewExpression e) {
        PsiExpressionList list = e.getArgumentList();
        return list == null ? PsiExpression.EMPTY_ARRAY : list.getExpressions();
    }

    private static void replace(PsiElement old, PsiElement neu) {
        old.replace(neu);
    }

    private static String formatRgb(Color c) {
        if (c.getAlpha() == 255) {
            return "rgb(" + c.getRed() + " " + c.getGreen() + " " + c.getBlue() + ")";
        }
        return String.format(java.util.Locale.US, "rgb(%d %d %d / %.2f)",
                c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha() / 255.0);
    }

    private static String formatHsl(Color c) {
        int[] hsl = toHsl(c);
        if (c.getAlpha() == 255) {
            return "hsl(" + hsl[0] + " " + hsl[1] + " " + hsl[2] + ")";
        }
        return String.format(java.util.Locale.US, "hsl(%d %d %d / %.2f)",
                hsl[0], hsl[1], hsl[2], c.getAlpha() / 255.0);
    }

    private static int[] toHsl(Color c) {
        double r = c.getRed() / 255.0, g = c.getGreen() / 255.0, b = c.getBlue() / 255.0;
        double max = Math.max(r, Math.max(g, b)), min = Math.min(r, Math.min(g, b));
        double d = max - min;
        double h = 0;
        if (d != 0) {
            if (max == r) h = 60 * (((g - b) / d) % 6);
            else if (max == g) h = 60 * (((b - r) / d) + 2);
            else h = 60 * (((r - g) / d) + 4);
        }
        if (h < 0) h += 360;
        double l = (max + min) / 2;
        double s = d == 0 ? 0 : d / (1 - Math.abs(2 * l - 1));
        return new int[]{(int) Math.round(h), (int) Math.round(s * 100), (int) Math.round(l * 100)};
    }
}
