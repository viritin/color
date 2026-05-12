package in.virit.color.netbeans;

import com.junichi11.netbeans.modules.color.codes.preview.api.OffsetRange;
import com.junichi11.netbeans.modules.color.codes.preview.spi.ColorCodeGeneratorItem;
import com.junichi11.netbeans.modules.color.codes.preview.spi.ColorCodesPreviewOptionsPanel;
import com.junichi11.netbeans.modules.color.codes.preview.spi.ColorCodesProvider;
import com.junichi11.netbeans.modules.color.codes.preview.spi.ColorValue;

import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.util.lookup.ServiceProvider;

import javax.swing.text.Document;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Recognizes Viritin Color expressions in Java source and emits color swatches
 * that the upstream "Color Codes Preview" plugin renders in the editor sidebar.
 *
 * Patterns (line-based):
 *   new RgbColor(r, g, b[, a])
 *   new HexColor("#xxx")
 *   new HslColor(h, s, l[, a])
 *   RgbColor.of("rgb(...)")
 *   HexColor.of("#xxx")
 *   HslColor.of("hsl(...)")
 *   Color.parseCssColor("...")
 *   NamedColor.NAME
 */
@ServiceProvider(service = ColorCodesProvider.class, position = 2000)
public final class ViritinColorCodesProvider implements ColorCodesProvider {

    private static final String MIME_TYPE_JAVA = "text/x-java";

    private static final String NUM = "\\s*(\\d+)\\s*";
    private static final String DBL = "\\s*([0-9]*\\.?[0-9]+)\\s*";
    private static final String STR = "\\s*\"([^\"]*)\"\\s*";

    private static final Pattern P_NEW_RGB_4 = Pattern.compile(
            "\\bnew\\s+RgbColor\\s*\\(" + NUM + "," + NUM + "," + NUM + "," + DBL + "\\)");
    private static final Pattern P_NEW_RGB_3 = Pattern.compile(
            "\\bnew\\s+RgbColor\\s*\\(" + NUM + "," + NUM + "," + NUM + "\\)");
    private static final Pattern P_RGB_OF = Pattern.compile(
            "\\bRgbColor\\.of\\s*\\(" + STR + "\\)");

    private static final Pattern P_NEW_HEX = Pattern.compile(
            "\\bnew\\s+HexColor\\s*\\(" + STR + "\\)");
    private static final Pattern P_HEX_OF = Pattern.compile(
            "\\bHexColor\\.of\\s*\\(" + STR + "\\)");

    private static final Pattern P_NEW_HSL_4 = Pattern.compile(
            "\\bnew\\s+HslColor\\s*\\(" + NUM + "," + NUM + "," + NUM + "," + DBL + "\\)");
    private static final Pattern P_NEW_HSL_3 = Pattern.compile(
            "\\bnew\\s+HslColor\\s*\\(" + NUM + "," + NUM + "," + NUM + "\\)");
    private static final Pattern P_HSL_OF = Pattern.compile(
            "\\bHslColor\\.of\\s*\\(" + STR + "\\)");

    private static final Pattern P_PARSE_CSS = Pattern.compile(
            "\\bColor\\.parseCssColor\\s*\\(" + STR + "\\)");

    private static final Pattern P_NAMED = Pattern.compile(
            "\\bNamedColor\\.([A-Z_]+)\\b");

    // ── CSS Color 4 types ────────────────────────────────────────────────────
    // All take double args (HwbColor / LabColor / LchColor / OklabColor / OklchColor).
    private static final String CSS4_TYPES = "(?:Hwb|Lab|Lch|Oklab|Oklch)Color";
    private static final Pattern P_NEW_CSS4_4 = Pattern.compile(
            "\\bnew\\s+" + CSS4_TYPES + "\\s*\\(" + DBL + "," + DBL + "," + DBL + "," + DBL + "\\)");
    private static final Pattern P_NEW_CSS4_3 = Pattern.compile(
            "\\bnew\\s+" + CSS4_TYPES + "\\s*\\(" + DBL + "," + DBL + "," + DBL + "\\)");
    private static final Pattern P_CSS4_OF = Pattern.compile(
            "\\b" + CSS4_TYPES + "\\.of\\s*\\(" + STR + "\\)");

    // ColorFunction(ColorSpace.X, c1, c2, c3[, alpha]) and ColorFunction.of("color(...)")
    private static final String SPACE = "\\s*ColorSpace\\.([A-Z_0-9]+)\\s*";
    private static final Pattern P_NEW_COLOR_FN_5 = Pattern.compile(
            "\\bnew\\s+ColorFunction\\s*\\(" + SPACE + "," + DBL + "," + DBL + "," + DBL + "," + DBL + "\\)");
    private static final Pattern P_NEW_COLOR_FN_4 = Pattern.compile(
            "\\bnew\\s+ColorFunction\\s*\\(" + SPACE + "," + DBL + "," + DBL + "," + DBL + "\\)");
    private static final Pattern P_COLOR_FN_OF = Pattern.compile(
            "\\bColorFunction\\.of\\s*\\(" + STR + "\\)");

    @Override
    public String getId() {
        return "viritin-color";
    }

    @Override
    public String getDisplayName() {
        return "Viritin Color";
    }

    @Override
    public String getDescription() {
        return "Preview colors from the in.virit:color library "
                + "(RgbColor, HexColor, HslColor, NamedColor, Color.parseCssColor).";
    }

    @Override
    public boolean isProviderEnabled(Document document) {
        return MIME_TYPE_JAVA.equals(NbEditorUtilities.getMimeType(document));
    }

    @Override
    public List<ColorValue> getColorValues(Document document, String line, int lineNumber,
                                           Map<String, List<ColorValue>> variableColorValues) {
        if (!hasAnyMarker(line)) {
            return Collections.emptyList();
        }
        List<ColorValue> out = new ArrayList<>();

        scanRgb4(line, lineNumber, out);
        scanRgb3(line, lineNumber, out);
        scanRgbOf(line, lineNumber, out);

        scanNewHex(line, lineNumber, out);
        scanHexOf(line, lineNumber, out);

        scanHsl4(line, lineNumber, out);
        scanHsl3(line, lineNumber, out);
        scanHslOf(line, lineNumber, out);

        scanParseCss(line, lineNumber, out);
        scanNamed(line, lineNumber, out);

        scanCss4New4(line, lineNumber, out);
        scanCss4New3(line, lineNumber, out);
        scanCss4Of(line, lineNumber, out);
        scanColorFunction5(line, lineNumber, out);
        scanColorFunction4(line, lineNumber, out);
        scanColorFunctionOf(line, lineNumber, out);

        return out;
    }

    @Override
    public int getStartIndex(Document document, int currentIndex) {
        return currentIndex;
    }

    @Override
    public ColorCodesPreviewOptionsPanel getOptionsPanel() {
        return ColorCodesPreviewOptionsPanel.createEmptyPanel();
    }

    @Override
    public boolean canGenerateColorCode() {
        return false;
    }

    @Override
    public List<ColorCodeGeneratorItem> getColorCodeGeneratorItems(String mimeType) {
        return Collections.emptyList();
    }

    // --- scanning helpers ----------------------------------------------------

    private static boolean hasAnyMarker(String line) {
        return line.contains("RgbColor")
                || line.contains("HexColor")
                || line.contains("HslColor")
                || line.contains("NamedColor")
                || line.contains("parseCssColor")
                || line.contains("HwbColor")
                || line.contains("LabColor")
                || line.contains("LchColor")
                || line.contains("OklabColor")
                || line.contains("OklchColor")
                || line.contains("ColorFunction");
    }

    private static void scanRgb4(String line, int ln, List<ColorValue> out) {
        Matcher m = P_NEW_RGB_4.matcher(line);
        while (m.find()) {
            try {
                int r = Integer.parseInt(m.group(1));
                int g = Integer.parseInt(m.group(2));
                int b = Integer.parseInt(m.group(3));
                double a = Double.parseDouble(m.group(4));
                Color c = safeRgba(r, g, b, a);
                if (c != null) out.add(value(line, m, ln, c, ViritinColorValue.Format.NEW_RGB_4));
            } catch (NumberFormatException ignored) {}
        }
    }

    private static void scanRgb3(String line, int ln, List<ColorValue> out) {
        Matcher m = P_NEW_RGB_3.matcher(line);
        while (m.find()) {
            // Skip overlap with the 4-arg form already captured.
            if (overlapsExisting(out, m)) continue;
            try {
                int r = Integer.parseInt(m.group(1));
                int g = Integer.parseInt(m.group(2));
                int b = Integer.parseInt(m.group(3));
                Color c = safeRgba(r, g, b, 1.0);
                if (c != null) out.add(value(line, m, ln, c, ViritinColorValue.Format.NEW_RGB_3));
            } catch (NumberFormatException ignored) {}
        }
    }

    private static void scanRgbOf(String line, int ln, List<ColorValue> out) {
        Matcher m = P_RGB_OF.matcher(line);
        while (m.find()) {
            Color c = CssParse.rgb(m.group(1));
            if (c != null) out.add(value(line, m, ln, c, ViritinColorValue.Format.RGB_OF));
        }
    }

    private static void scanNewHex(String line, int ln, List<ColorValue> out) {
        Matcher m = P_NEW_HEX.matcher(line);
        while (m.find()) {
            Color c = HexUtil.parseHex(m.group(1));
            if (c != null) out.add(value(line, m, ln, c, ViritinColorValue.Format.NEW_HEX));
        }
    }

    private static void scanHexOf(String line, int ln, List<ColorValue> out) {
        Matcher m = P_HEX_OF.matcher(line);
        while (m.find()) {
            Color c = HexUtil.parseHex(m.group(1));
            if (c != null) out.add(value(line, m, ln, c, ViritinColorValue.Format.HEX_OF));
        }
    }

    private static void scanHsl4(String line, int ln, List<ColorValue> out) {
        Matcher m = P_NEW_HSL_4.matcher(line);
        while (m.find()) {
            try {
                int h = Integer.parseInt(m.group(1));
                int s = Integer.parseInt(m.group(2));
                int l = Integer.parseInt(m.group(3));
                double a = Double.parseDouble(m.group(4));
                if (validHsl(h, s, l) && a >= 0 && a <= 1) {
                    out.add(value(line, m, ln, CssParse.hslToColor(h, s, l, a),
                            ViritinColorValue.Format.NEW_HSL_4));
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    private static void scanHsl3(String line, int ln, List<ColorValue> out) {
        Matcher m = P_NEW_HSL_3.matcher(line);
        while (m.find()) {
            if (overlapsExisting(out, m)) continue;
            try {
                int h = Integer.parseInt(m.group(1));
                int s = Integer.parseInt(m.group(2));
                int l = Integer.parseInt(m.group(3));
                if (validHsl(h, s, l)) {
                    out.add(value(line, m, ln, CssParse.hslToColor(h, s, l, 1.0),
                            ViritinColorValue.Format.NEW_HSL_3));
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    private static void scanHslOf(String line, int ln, List<ColorValue> out) {
        Matcher m = P_HSL_OF.matcher(line);
        while (m.find()) {
            Color c = CssParse.hsl(m.group(1));
            if (c != null) out.add(value(line, m, ln, c, ViritinColorValue.Format.HSL_OF));
        }
    }

    private static void scanParseCss(String line, int ln, List<ColorValue> out) {
        Matcher m = P_PARSE_CSS.matcher(line);
        while (m.find()) {
            Color c = CssParse.any(m.group(1));
            if (c != null) out.add(value(line, m, ln, c, ViritinColorValue.Format.PARSE_CSS));
        }
    }

    private static void scanNamed(String line, int ln, List<ColorValue> out) {
        Matcher m = P_NAMED.matcher(line);
        while (m.find()) {
            Color c = NamedColorTable.colorFor(m.group(1));
            if (c != null) out.add(value(line, m, ln, c, ViritinColorValue.Format.NAMED_COLOR));
        }
    }

    private static ColorValue value(String line, Matcher m, int ln, Color c,
                                    ViritinColorValue.Format fmt) {
        OffsetRange range = new OffsetRange(m.start(), m.end());
        return new ViritinColorValue(line.substring(m.start(), m.end()), range, ln, c, fmt);
    }

    private static boolean overlapsExisting(List<ColorValue> out, Matcher m) {
        int s = m.start(), e = m.end();
        for (ColorValue v : out) {
            if (v.getStartOffset() <= s && e <= v.getEndOffset()) return true;
        }
        return false;
    }

    private static Color safeRgba(int r, int g, int b, double a) {
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) return null;
        if (a < 0 || a > 1) return null;
        return new Color(r, g, b, (int) Math.round(a * 255));
    }

    private static boolean validHsl(int h, int s, int l) {
        return h >= 0 && h <= 360 && s >= 0 && s <= 100 && l >= 0 && l <= 100;
    }

    // ── CSS Color 4 scanning ─────────────────────────────────────────────────

    private static void scanCss4New4(String line, int ln, List<ColorValue> out) {
        Matcher m = P_NEW_CSS4_4.matcher(line);
        while (m.find()) {
            Color c = css4Constructor(line.substring(m.start(), m.end()),
                    parseD(m.group(1)), parseD(m.group(2)), parseD(m.group(3)), parseD(m.group(4)));
            if (c != null) out.add(value(line, m, ln, c, ViritinColorValue.Format.CSS_COLOR_4));
        }
    }

    private static void scanCss4New3(String line, int ln, List<ColorValue> out) {
        Matcher m = P_NEW_CSS4_3.matcher(line);
        while (m.find()) {
            if (overlapsExisting(out, m)) continue;
            Color c = css4Constructor(line.substring(m.start(), m.end()),
                    parseD(m.group(1)), parseD(m.group(2)), parseD(m.group(3)), 1.0);
            if (c != null) out.add(value(line, m, ln, c, ViritinColorValue.Format.CSS_COLOR_4));
        }
    }

    private static void scanCss4Of(String line, int ln, List<ColorValue> out) {
        Matcher m = P_CSS4_OF.matcher(line);
        while (m.find()) {
            Color c = CssParse.any(m.group(1));
            if (c != null) out.add(value(line, m, ln, c, ViritinColorValue.Format.CSS_COLOR_4));
        }
    }

    private static void scanColorFunction5(String line, int ln, List<ColorValue> out) {
        Matcher m = P_NEW_COLOR_FN_5.matcher(line);
        while (m.find()) {
            Color c = colorFunctionConstructor(m.group(1),
                    parseD(m.group(2)), parseD(m.group(3)), parseD(m.group(4)), parseD(m.group(5)));
            if (c != null) out.add(value(line, m, ln, c, ViritinColorValue.Format.CSS_COLOR_4));
        }
    }

    private static void scanColorFunction4(String line, int ln, List<ColorValue> out) {
        Matcher m = P_NEW_COLOR_FN_4.matcher(line);
        while (m.find()) {
            if (overlapsExisting(out, m)) continue;
            Color c = colorFunctionConstructor(m.group(1),
                    parseD(m.group(2)), parseD(m.group(3)), parseD(m.group(4)), 1.0);
            if (c != null) out.add(value(line, m, ln, c, ViritinColorValue.Format.CSS_COLOR_4));
        }
    }

    private static void scanColorFunctionOf(String line, int ln, List<ColorValue> out) {
        Matcher m = P_COLOR_FN_OF.matcher(line);
        while (m.find()) {
            Color c = CssParse.any(m.group(1));
            if (c != null) out.add(value(line, m, ln, c, ViritinColorValue.Format.CSS_COLOR_4));
        }
    }

    /** Reads the simple type name from a {@code new XxxColor(...)} substring and dispatches. */
    private static Color css4Constructor(String matched, double c1, double c2, double c3, double alpha) {
        try {
            in.virit.color.Color rec;
            if (matched.contains("HwbColor"))   rec = new in.virit.color.HwbColor(c1, c2, c3, alpha);
            else if (matched.contains("LabColor"))   rec = new in.virit.color.LabColor(c1, c2, c3, alpha);
            else if (matched.contains("LchColor"))   rec = new in.virit.color.LchColor(c1, c2, c3, alpha);
            else if (matched.contains("OklabColor")) rec = new in.virit.color.OklabColor(c1, c2, c3, alpha);
            else if (matched.contains("OklchColor")) rec = new in.virit.color.OklchColor(c1, c2, c3, alpha);
            else return null;
            return CssParse.toAwt(rec.toRgbColor());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static Color colorFunctionConstructor(String spaceName, double c1, double c2, double c3, double alpha) {
        try {
            in.virit.color.ColorSpace space = in.virit.color.ColorSpace.valueOf(spaceName);
            return CssParse.toAwt(new in.virit.color.ColorFunction(space, c1, c2, c3, alpha).toRgbColor());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static double parseD(String s) {
        return Double.parseDouble(s.trim());
    }
}
