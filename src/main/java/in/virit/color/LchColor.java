package in.virit.color;

import in.virit.color.internal.ColorMath;

import java.util.Locale;

/**
 * CIELCh color from CSS Color Module Level 4: the polar form of {@link LabColor}.
 *
 * @param l     Lightness (0–100)
 * @param c     Chroma (≥ 0; typically up to ~150 within sRGB)
 * @param h     Hue in degrees (0–360)
 * @param alpha Alpha (0–1)
 */
public record LchColor(double l, double c, double h, double alpha) implements Color {

    /**
     * Validating constructor.
     *
     * @param l     Lightness (0–100)
     * @param c     Chroma (≥ 0)
     * @param h     Hue in degrees (0–360)
     * @param alpha Alpha (0–1)
     */
    public LchColor {
        if (alpha < 0 || alpha > 1) {
            throw new IllegalArgumentException("Alpha value must be between 0 and 1");
        }
        if (l < 0 || l > 100) {
            throw new IllegalArgumentException("Lightness value must be between 0 and 100");
        }
        if (c < 0) {
            throw new IllegalArgumentException("Chroma value must be non-negative");
        }
        if (h < 0 || h > 360) {
            throw new IllegalArgumentException("Hue value must be between 0 and 360");
        }
    }

    /**
     * Convenience constructor, alpha defaults to 1.
     *
     * @param l Lightness (0–100)
     * @param c Chroma (≥ 0)
     * @param h Hue in degrees (0–360)
     */
    public LchColor(double l, double c, double h) {
        this(l, c, h, 1);
    }

    @Override
    public RgbColor toRgbColor() {
        return toLabColor().toRgbColor();
    }

    /**
     * Converts to the rectangular {@link LabColor} representation.
     *
     * @return equivalent Lab color
     */
    public LabColor toLabColor() {
        double rad = Math.toRadians(h);
        double a = c * Math.cos(rad);
        double b = c * Math.sin(rad);
        return new LabColor(l, a, b, alpha);
    }

    /**
     * @param newAlpha new alpha (0–1)
     * @return new LchColor with the given alpha
     */
    public LchColor withAlpha(double newAlpha) {
        return new LchColor(l, c, h, newAlpha);
    }

    @Override
    public String toString() {
        String alphaPart = alpha == 1.0 ? "" : String.format(Locale.US, " / %.2f", alpha);
        return String.format(Locale.US, "lch(%s %s %s%s)",
                formatNum(l), formatNum(c), formatNum(h), alphaPart);
    }

    private static String formatNum(double v) {
        if (v == Math.floor(v) && !Double.isInfinite(v)) {
            return String.valueOf((long) v);
        }
        return String.format(Locale.US, "%.2f", v);
    }

    /**
     * Parses a CSS {@code lch(...)} string. Percent on chroma is interpreted
     * with 100% = 150 (CSS Color 4 reference).
     *
     * @param cssColorString e.g. {@code "lch(50 30 120)"} or {@code "lch(50% 30 120deg / 0.5)"}
     * @return parsed LchColor
     */
    public static LchColor of(String cssColorString) {
        String[] parts = ColorMath.splitComponents(cssColorString);
        if (parts.length < 3 || parts.length > 4) {
            throw new IllegalArgumentException("lch() requires 3 or 4 components: " + cssColorString);
        }
        double l = ColorMath.parsePercentOrNumber(parts[0], 100);
        double c = ColorMath.parsePercentOrNumber(parts[1], 150);
        double h = ColorMath.parseAngle(parts[2]);
        double alpha = parts.length == 4 ? ColorMath.parseAlpha(parts[3]) : 1.0;
        h = ((h % 360) + 360) % 360;
        return new LchColor(l, c, h, alpha);
    }
}
