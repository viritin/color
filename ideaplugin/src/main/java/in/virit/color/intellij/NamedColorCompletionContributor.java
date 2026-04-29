package in.virit.color.intellij;

import com.intellij.codeInsight.ExpectedTypeInfo;
import com.intellij.codeInsight.ExpectedTypesProvider;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementDecorator;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.codeInsight.lookup.LookupElementRenderer;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.Map;

/**
 * Contributes named-color completion entries with swatch icons.
 *
 * <p>Two contexts are handled:
 * <ul>
 *   <li>No qualifier in a {@code Color}-expected slot (e.g. {@code Color x = █}) — we
 *       contribute lookup elements that insert {@code NamedColor.X}.</li>
 *   <li>Qualified by {@code NamedColor.} — we let IntelliJ's built-in enum completion
 *       run and decorate each result with the matching color swatch.</li>
 * </ul>
 */
public class NamedColorCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters,
                                       @NotNull CompletionResultSet result) {
        PsiElement pos = parameters.getPosition();
        PsiReferenceExpression ref = PsiTreeUtil.getParentOfType(pos, PsiReferenceExpression.class);

        if (ref != null && isNamedColorQualifier(ref.getQualifierExpression())) {
            // Decorate IntelliJ's built-in enum completions for NamedColor.X.
            result.runRemainingContributors(parameters, completionResult -> {
                LookupElement le = completionResult.getLookupElement();
                Color c = NamedColorTable.colorFor(le.getLookupString());
                if (c != null) {
                    result.passResult(completionResult.withLookupElement(decorate(le, c)));
                } else {
                    result.passResult(completionResult);
                }
            });
            return;
        }

        if (ref != null && ref.getQualifierExpression() == null && expectsColor(ref)) {
            addPrefixedNamedColors(parameters, result);
        }

        super.fillCompletionVariants(parameters, result);
    }

    private static void addPrefixedNamedColors(CompletionParameters params, CompletionResultSet result) {
        PsiElement pos = params.getPosition();
        // Verify NamedColor is on the classpath so the inserted "NamedColor.X" reference resolves.
        PsiClass namedColorCls = JavaPsiFacade.getInstance(pos.getProject())
                .findClass(ColorLib.NAMED, GlobalSearchScope.allScope(pos.getProject()));
        if (namedColorCls == null || !namedColorCls.isEnum()) return;

        for (Map.Entry<String, String> e : NamedColorTable.entries().entrySet()) {
            String name = e.getKey();
            Color c = HexUtil.parseHex(e.getValue());
            if (c == null) continue;

            String insertText = "NamedColor." + name;
            LookupElement le = LookupElementBuilder.create(namedColorCls, insertText)
                    .withPresentableText(name.toLowerCase())
                    .withTypeText(e.getValue(), true)
                    .withIcon(new ColorIcon(c))
                    // Force the canonical text on insert. Without this, case-insensitive
                    // matching causes the IDE to preserve the user's typed-prefix case
                    // (e.g. typing "red" inserts "namedcolor.red" instead of "NamedColor.RED").
                    .withInsertHandler((context, item) ->
                            context.getDocument().replaceString(
                                    context.getStartOffset(), context.getTailOffset(), insertText))
                    .withCaseSensitivity(false);
            result.addElement(le);
        }
    }

    private static LookupElement decorate(LookupElement original, Color color) {
        return LookupElementDecorator.withRenderer(original, new LookupElementRenderer<>() {
            @Override
            public void renderElement(LookupElementDecorator<LookupElement> element,
                                      LookupElementPresentation presentation) {
                element.getDelegate().renderElement(presentation);
                presentation.setIcon(new ColorIcon(color));
                presentation.setTypeText(HexUtil.toHex(color));
            }
        });
    }

    private static boolean isNamedColorQualifier(PsiExpression q) {
        if (!(q instanceof PsiReferenceExpression qref)) return false;
        String text = qref.getText();
        if (!"NamedColor".equals(text) && !text.endsWith(".NamedColor")) return false;
        PsiElement target = qref.resolve();
        if (!(target instanceof PsiClass cls)) return false;
        return ColorLib.NAMED.equals(cls.getQualifiedName());
    }

    private static boolean expectsColor(PsiReferenceExpression ref) {
        ExpectedTypeInfo[] infos = ExpectedTypesProvider.getExpectedTypes(ref, true);
        for (ExpectedTypeInfo info : infos) {
            if (isColorType(info.getType())) return true;
        }
        return false;
    }

    private static boolean isColorType(PsiType t) {
        if (!(t instanceof PsiClassType ct)) return false;
        PsiClass cls = ct.resolve();
        if (cls == null) return false;
        String fqn = cls.getQualifiedName();
        if (ColorLib.COLOR.equals(fqn) || ColorLib.NAMED.equals(fqn)) return true;
        for (PsiClass sup : cls.getSupers()) {
            if (ColorLib.COLOR.equals(sup.getQualifiedName())) return true;
        }
        return false;
    }
}
