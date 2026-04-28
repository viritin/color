package in.virit.color.intellij;

import com.intellij.codeInsight.ExpectedTypeInfo;
import com.intellij.codeInsight.ExpectedTypesProvider;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.Map;

/**
 * Contributes named-color completion entries (with color swatch icons)
 * wherever a {@code Color} / {@code NamedColor} is expected.
 */
public class NamedColorCompletionContributor extends CompletionContributor {

    public NamedColorCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withParent(PsiReferenceExpression.class),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters params,
                                                  @NotNull ProcessingContext ctx,
                                                  @NotNull CompletionResultSet result) {
                        addNamedColors(params, result);
                    }
                });
    }

    private static void addNamedColors(CompletionParameters params, CompletionResultSet result) {
        PsiElement pos = params.getPosition();
        PsiReferenceExpression ref = PsiTreeUtil.getParentOfType(pos, PsiReferenceExpression.class);
        if (ref == null || ref.getQualifierExpression() != null) return;
        if (!expectsColor(ref)) return;

        // Verify NamedColor is on the classpath so the inserted "NamedColor.X" reference resolves.
        PsiClass namedColorCls = JavaPsiFacade.getInstance(pos.getProject())
                .findClass(ColorLib.NAMED, GlobalSearchScope.allScope(pos.getProject()));
        if (namedColorCls == null || !namedColorCls.isEnum()) return;

        for (Map.Entry<String, String> e : NamedColorTable.entries().entrySet()) {
            String name = e.getKey();
            Color c = HexUtil.parseHex(e.getValue());
            if (c == null) continue;

            LookupElement le = LookupElementBuilder.create(namedColorCls, "NamedColor." + name)
                    .withPresentableText(name.toLowerCase())
                    .withTypeText(e.getValue(), true)
                    .withIcon(new ColorIcon(c))
                    .withCaseSensitivity(false);
            result.addElement(le);
        }
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
        // also accept any subtype of Color (RgbColor, HexColor, HslColor)
        for (PsiClass sup : cls.getSupers()) {
            if (ColorLib.COLOR.equals(sup.getQualifiedName())) return true;
        }
        return false;
    }

}
