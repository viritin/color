package in.virit.color;

import in.virit.color.internal.ColorMath;

import java.util.Locale;

/**
 * HWB (Hue, Whiteness, Blackness) color from CSS Color Module Level 4.
 *
 * @param h     Hue in degrees (0–360)
 * @param w     Whiteness (0–100)
 * @param b     Blackness (0–100)
 * @param alpha Alpha (0–1)
 */
public record HwbColor(double h, double w, double b, double alpha) implements Color {

    /**
     * Validating constructor.
     *
     * @param h     Hue in degrees (0–360)
     * @param w     Whiteness (0–100)
     * @param b     Blackness (0–100)
     * @param alpha Alpha (0–1)
     */
    public HwbColor {
        if (alpha < 0 || alpha > 1) {
            throw new IllegalArgumentException("Alpha value must be between 0 and 1");
        }
        if (h < 0 || h > 360) {
            throw new IllegalArgumentException("Hue value must be between 0 and 360");
        }
        if (w < 0 || w > 100) {
            throw new IllegalArgumentException("Whiteness value must be between 0 and 100");
        }
        if (b < 0 || b > 100) {
            throw new IllegalArgumentException("Blackness value must be between 0 and 100");
        }
    }

    /**
     * Convenience constructor, alpha defaults to 1.
     *
     * @param h Hue in degrees (0–360)
     * @param w Whiteness (0–100)
     * @param b Blackness (0–100)
     */
    public HwbColor(double h, double w, double b) {
        this(h, w, b, 1);
    }

    @Override
    public RgbColor toRgbColor() {
        double wn = w / 100.0;
        double bn = b / 100.0;
        if (wn + bn >= 1) {
            int gray = ColorMath.clampToByte(wn / (wn + bn));
            return new RgbColor(gray, gray, gray, alpha);
        }
        // Start from HSL with S=100%, L=50% — equivalently base hue at full saturation
        double[] rgb = hueToRgb(h);
        for (int i = 0; i < 3; i++) {
            rgb[i] = rgb[i] * (1 - wn - bn) + wn;
        }
        return new RgbColor(
                ColorMath.clampToByte(rgb[0]),
                ColorMath.clampToByte(rgb[1]),
                ColorMath.clampToByte(rgb[2]),
                alpha);
    }

    private static double[] hueToRgb(double hueDeg) {
        double hue = ((hueDeg % 360) + 360) % 360 / 60.0;
        double x = 1 - Math.abs((hue % 2) - 1);
        if (hue < 1) return new double[] { 1, x, 0 };
        if (hue < 2) return new double[] { x, 1, 0 };
        if (hue < 3) return new double[] { 0, 1, x };
        if (hue < 4) return new double[] { 0, x, 1 };
        if (hue < 5) return new double[] { x, 0, 1 };
        return new double[] { 1, 0, x };
    }

    @Override
    public String toString() {
        String alphaPart = alpha == 1.0 ? "" : String.format(Locale.US, " / %.2f", alpha);
        return String.format(Locale.US, "hwb(%s %s %s%s)",
                formatNum(h), formatNum(w), formatNum(b), alphaPart);
    }

    private static String formatNum(double v) {
        if (v == Math.floor(v) && !Double.isInfinite(v)) {
            return String.valueOf((long) v);
        }
        return String.format(Locale.US, "%.2f", v);
    }

    /**
     * @param newAlpha new alpha (0–1)
     * @return new HwbColor with the given alpha
     */
    public HwbColor withAlpha(double newAlpha) {
        return new HwbColor(h, w, b, newAlpha);
    }

    /**
     * Parses a CSS {@code hwb(...)} string.
     *
     * @param cssColorString e.g. {@code "hwb(0 0% 0%)"} or {@code "hwb(120 50% 0% / 0.5)"}
     * @return parsed HwbColor
     */
    public static HwbColor of(String cssColorString) {
        String[] parts = ColorMath.splitComponents(cssColorString);
        if (parts.length < 3 || parts.length > 4) {
            throw new IllegalArgumentException("hwb() requires 3 or 4 components: " + cssColorString);
        }
        double h = ColorMath.parseAngle(parts[0]);
        double w = ColorMath.parsePercentOrNumber(parts[1], 100);
        double b = ColorMath.parsePercentOrNumber(parts[2], 100);
        double a = parts.length == 4 ? ColorMath.parseAlpha(parts[3]) : 1.0;
        // Normalize hue into [0, 360]
        h = ((h % 360) + 360) % 360;
        return new HwbColor(h, w, b, a);
    }
}
