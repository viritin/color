package in.virit.color;

import in.virit.color.internal.ColorMath;

import java.util.Locale;

/**
 * OKLab color from CSS Color Module Level 4 — a perceptually uniform color space.
 *
 * <p>CSS Color 4 expresses lightness as either a number 0..1 or a percentage where
 * 100% = 1. The {@code a} and {@code b} axes are unbounded; useful sRGB values
 * fall within roughly ±0.4 (100% percent reference). Only {@code l} and
 * {@code alpha} ranges are validated.
 *
 * @param l     Lightness (0–1)
 * @param a     a-axis (red-green); CSS percent reference: 100% = 0.4
 * @param b     b-axis (yellow-blue); CSS percent reference: 100% = 0.4
 * @param alpha Alpha (0–1)
 */
public record OklabColor(double l, double a, double b, double alpha) implements Color {

    /**
     * Validating constructor.
     *
     * @param l     Lightness (0–1)
     * @param a     a-axis
     * @param b     b-axis
     * @param alpha Alpha (0–1)
     */
    public OklabColor {
        if (alpha < 0 || alpha > 1) {
            throw new ColorParseException("Alpha value must be between 0 and 1");
        }
        if (l < 0 || l > 1) {
            throw new ColorParseException("Lightness value must be between 0 and 1");
        }
    }

    /**
     * Convenience constructor, alpha defaults to 1.
     *
     * @param l Lightness (0–1)
     * @param a a-axis
     * @param b b-axis
     */
    public OklabColor(double l, double a, double b) {
        this(l, a, b, 1);
    }

    @Override
    public RgbColor toRgbColor() {
        double[] lin = ColorMath.oklabToLinearSrgb(l, a, b);
        return new RgbColor(
                ColorMath.clampToByte(ColorMath.linearSrgbToSrgb(lin[0])),
                ColorMath.clampToByte(ColorMath.linearSrgbToSrgb(lin[1])),
                ColorMath.clampToByte(ColorMath.linearSrgbToSrgb(lin[2])),
                alpha);
    }

    /**
     * Converts to the polar {@link OklchColor} representation.
     *
     * @return equivalent OkLCh color
     */
    public OklchColor toOklchColor() {
        double c = Math.sqrt(a * a + b * b);
        double h = Math.toDegrees(Math.atan2(b, a));
        if (h < 0) h += 360;
        if (c < 1e-7) h = 0;
        return new OklchColor(l, c, h, alpha);
    }

    /**
     * Returns a copy of this color with a different alpha value.
     *
     * @param newAlpha new alpha (0–1)
     * @return new OklabColor with the given alpha
     */
    public OklabColor withAlpha(double newAlpha) {
        return new OklabColor(l, a, b, newAlpha);
    }

    @Override
    public String toString() {
        String alphaPart = alpha == 1.0 ? "" : String.format(Locale.US, " / %.2f", alpha);
        return String.format(Locale.US, "oklab(%s %s %s%s)",
                formatNum(l), formatNum(a), formatNum(b), alphaPart);
    }

    private static String formatNum(double v) {
        return String.format(Locale.US, "%.4f", v).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    /**
     * Parses a CSS {@code oklab(...)} string. Percent on lightness is interpreted
     * with 100% = 1; percent on a/b with 100% = 0.4 (CSS Color 4 reference).
     *
     * @param cssColorString e.g. {@code "oklab(0.628 0.225 0.126)"} or {@code "oklab(62.8% 50% 31.5%)"}
     * @return parsed OklabColor
     */
    public static OklabColor of(String cssColorString) {
        String[] parts = ColorMath.splitComponents(cssColorString);
        if (parts.length < 3 || parts.length > 4) {
            throw new ColorParseException("oklab() requires 3 or 4 components: " + cssColorString);
        }
        double l = ColorMath.parsePercentOrNumber(parts[0], 1.0);
        double a = ColorMath.parsePercentOrNumber(parts[1], 0.4);
        double b = ColorMath.parsePercentOrNumber(parts[2], 0.4);
        double alpha = parts.length == 4 ? ColorMath.parseAlpha(parts[3]) : 1.0;
        return new OklabColor(l, a, b, alpha);
    }
}
