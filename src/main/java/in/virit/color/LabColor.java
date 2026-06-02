package in.virit.color;

import in.virit.color.internal.ColorMath;

import java.util.Locale;

/**
 * CIELAB color from CSS Color Module Level 4.
 *
 * <p>The {@code a} (red-green) and {@code b} (yellow-blue) axes are unbounded
 * in principle; useful sRGB-coverage values fall roughly in ±125. Only the
 * lightness ({@code l}) and {@code alpha} ranges are validated.
 *
 * @param l     Lightness (0–100)
 * @param a     a-axis (red-green); CSS percent reference: 100% = 125
 * @param b     b-axis (yellow-blue); CSS percent reference: 100% = 125
 * @param alpha Alpha (0–1)
 */
public record LabColor(double l, double a, double b, double alpha) implements Color {

    /**
     * Validating constructor.
     *
     * @param l     Lightness (0–100)
     * @param a     a-axis
     * @param b     b-axis
     * @param alpha Alpha (0–1)
     */
    public LabColor {
        if (alpha < 0 || alpha > 1) {
            throw new ColorParseException("Alpha value must be between 0 and 1");
        }
        if (l < 0 || l > 100) {
            throw new ColorParseException("Lightness value must be between 0 and 100");
        }
    }

    /**
     * Convenience constructor, alpha defaults to 1.
     *
     * @param l Lightness (0–100)
     * @param a a-axis
     * @param b b-axis
     */
    public LabColor(double l, double a, double b) {
        this(l, a, b, 1);
    }

    @Override
    public RgbColor toRgbColor() {
        double[] xyzD50 = ColorMath.labToXyzD50(l, a, b);
        double[] xyzD65 = ColorMath.xyzD50ToXyzD65(xyzD50[0], xyzD50[1], xyzD50[2]);
        double[] lin = ColorMath.xyzD65ToLinearSrgb(xyzD65[0], xyzD65[1], xyzD65[2]);
        return new RgbColor(
                ColorMath.clampToByte(ColorMath.linearSrgbToSrgb(lin[0])),
                ColorMath.clampToByte(ColorMath.linearSrgbToSrgb(lin[1])),
                ColorMath.clampToByte(ColorMath.linearSrgbToSrgb(lin[2])),
                alpha);
    }

    /**
     * Converts to the polar {@link LchColor} representation.
     *
     * @return equivalent LCh color
     */
    public LchColor toLchColor() {
        double c = Math.sqrt(a * a + b * b);
        double h = Math.toDegrees(Math.atan2(b, a));
        if (h < 0) h += 360;
        if (c < 1e-7) h = 0;
        return new LchColor(l, c, h, alpha);
    }

    /**
     * Returns a copy of this color with a different alpha value.
     *
     * @param newAlpha new alpha (0–1)
     * @return new LabColor with the given alpha
     */
    public LabColor withAlpha(double newAlpha) {
        return new LabColor(l, a, b, newAlpha);
    }

    @Override
    public String toString() {
        String alphaPart = alpha == 1.0 ? "" : String.format(Locale.US, " / %.2f", alpha);
        return String.format(Locale.US, "lab(%s %s %s%s)",
                formatNum(l), formatNum(a), formatNum(b), alphaPart);
    }

    private static String formatNum(double v) {
        if (v == Math.floor(v) && !Double.isInfinite(v)) {
            return String.valueOf((long) v);
        }
        return String.format(Locale.US, "%.2f", v);
    }

    /**
     * Parses a CSS {@code lab(...)} string. Percentages on the {@code a}/{@code b}
     * axes are interpreted with 100% = 125 (CSS Color 4 reference).
     *
     * @param cssColorString e.g. {@code "lab(50 20 -30)"} or {@code "lab(50% 20 -30 / 0.5)"}
     * @return parsed LabColor
     */
    public static LabColor of(String cssColorString) {
        String[] parts = ColorMath.splitComponents(cssColorString);
        if (parts.length < 3 || parts.length > 4) {
            throw new ColorParseException("lab() requires 3 or 4 components: " + cssColorString);
        }
        double l = ColorMath.parsePercentOrNumber(parts[0], 100);
        double a = ColorMath.parsePercentOrNumber(parts[1], 125);
        double b = ColorMath.parsePercentOrNumber(parts[2], 125);
        double alpha = parts.length == 4 ? ColorMath.parseAlpha(parts[3]) : 1.0;
        return new LabColor(l, a, b, alpha);
    }
}
