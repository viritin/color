package in.virit.color.netbeans;

import com.junichi11.netbeans.modules.color.codes.preview.spi.ColorCodeFormatter;
import org.netbeans.api.annotations.common.NonNull;

import java.awt.Color;
import java.util.Locale;

/**
 * Rewrites a recognized Viritin Color expression in the same shape it was
 * matched. NamedColor references are rewritten as HexColor.of("#...") since
 * an arbitrary RGB picker result rarely lands on a named color exactly.
 */
final class ViritinColorFormatter implements ColorCodeFormatter {

    private final ViritinColorValue.Format format;

    ViritinColorFormatter(ViritinColorValue.Format format) {
        this.format = format;
    }

    @Override
    public @NonNull String format(Color c) {
        switch (format) {
            case NEW_RGB_3:
                return "new RgbColor(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ")";
            case NEW_RGB_4:
                return "new RgbColor(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue()
                        + ", " + alpha(c) + ")";
            case RGB_OF:
                return "RgbColor.of(\"" + cssRgb(c) + "\")";
            case NEW_HEX:
                return "new HexColor(\"" + HexUtil.toHex(c) + "\")";
            case HEX_OF:
                return "HexColor.of(\"" + HexUtil.toHex(c) + "\")";
            case NEW_HSL_3: {
                int[] hsl = HexUtil.toHsl(c);
                return "new HslColor(" + hsl[0] + ", " + hsl[1] + ", " + hsl[2] + ")";
            }
            case NEW_HSL_4: {
                int[] hsl = HexUtil.toHsl(c);
                return "new HslColor(" + hsl[0] + ", " + hsl[1] + ", " + hsl[2] + ", " + alpha(c) + ")";
            }
            case HSL_OF:
                return "HslColor.of(\"" + cssHsl(c) + "\")";
            case PARSE_CSS:
                return "Color.parseCssColor(\"" + HexUtil.toHex(c) + "\")";
            case NAMED_COLOR:
                return "HexColor.of(\"" + HexUtil.toHex(c) + "\")";
            case CSS_COLOR_4:
                return c.getAlpha() == 255
                        ? "new RgbColor(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ")"
                        : "new RgbColor(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue()
                                + ", " + alpha(c) + ")";
            default:
                return HexUtil.toHex(c);
        }
    }

    private static String alpha(Color c) {
        return String.format(Locale.US, "%.2f", c.getAlpha() / 255.0);
    }

    private static String cssRgb(Color c) {
        if (c.getAlpha() == 255) {
            return "rgb(" + c.getRed() + " " + c.getGreen() + " " + c.getBlue() + ")";
        }
        return String.format(Locale.US, "rgb(%d %d %d / %.2f)",
                c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha() / 255.0);
    }

    private static String cssHsl(Color c) {
        int[] hsl = HexUtil.toHsl(c);
        if (c.getAlpha() == 255) {
            return "hsl(" + hsl[0] + " " + hsl[1] + " " + hsl[2] + ")";
        }
        return String.format(Locale.US, "hsl(%d %d %d / %.2f)",
                hsl[0], hsl[1], hsl[2], c.getAlpha() / 255.0);
    }
}
