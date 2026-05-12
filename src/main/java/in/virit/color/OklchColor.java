package in.virit.color;

import in.virit.color.internal.ColorMath;

import java.util.Locale;

/**
 * OkLCh color from CSS Color Module Level 4: the polar form of {@link OklabColor}.
 *
 * @param l     Lightness (0–1)
 * @param c     Chroma (≥ 0; useful sRGB values up to ~0.4)
 * @param h     Hue in degrees (0–360)
 * @param alpha Alpha (0–1)
 */
public record OklchColor(double l, double c, double h, double alpha) implements Color {

    /**
     * Validating constructor.
     *
     * @param l     Lightness (0–1)
     * @param c     Chroma (≥ 0)
     * @param h     Hue in degrees (0–360)
     * @param alpha Alpha (0–1)
     */
    public OklchColor {
        if (alpha < 0 || alpha > 1) {
            throw new IllegalArgumentException("Alpha value must be between 0 and 1");
        }
        if (l < 0 || l > 1) {
            throw new IllegalArgumentException("Lightness value must be between 0 and 1");
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
     * @param l Lightness (0–1)
     * @param c Chroma (≥ 0)
     * @param h Hue in degrees (0–360)
     */
    public OklchColor(double l, double c, double h) {
        this(l, c, h, 1);
    }

    @Override
    public RgbColor toRgbColor() {
        return toOklabColor().toRgbColor();
    }

    /**
     * Converts to the rectangular {@link OklabColor} representation.
     *
     * @return equivalent OkLab color
     */
    public OklabColor toOklabColor() {
        double rad = Math.toRadians(h);
        double a = c * Math.cos(rad);
        double b = c * Math.sin(rad);
        return new OklabColor(l, a, b, alpha);
    }

    /**
     * Returns a copy of this color with a different alpha value.
     *
     * @param newAlpha new alpha (0–1)
     * @return new OklchColor with the given alpha
     */
    public OklchColor withAlpha(double newAlpha) {
        return new OklchColor(l, c, h, newAlpha);
    }

    @Override
    public String toString() {
        String alphaPart = alpha == 1.0 ? "" : String.format(Locale.US, " / %.2f", alpha);
        return String.format(Locale.US, "oklch(%s %s %s%s)",
                formatNum(l), formatNum(c), formatNum(h), alphaPart);
    }

    private static String formatNum(double v) {
        if (v == Math.floor(v) && !Double.isInfinite(v)) {
            return String.valueOf((long) v);
        }
        return String.format(Locale.US, "%.4f", v).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    /**
     * Parses a CSS {@code oklch(...)} string. Percent on lightness is interpreted
     * with 100% = 1; percent on chroma with 100% = 0.4.
     *
     * @param cssColorString e.g. {@code "oklch(0.628 0.258 29.234)"}
     * @return parsed OklchColor
     */
    public static OklchColor of(String cssColorString) {
        String[] parts = ColorMath.splitComponents(cssColorString);
        if (parts.length < 3 || parts.length > 4) {
            throw new IllegalArgumentException("oklch() requires 3 or 4 components: " + cssColorString);
        }
        double l = ColorMath.parsePercentOrNumber(parts[0], 1.0);
        double c = ColorMath.parsePercentOrNumber(parts[1], 0.4);
        double h = ColorMath.parseAngle(parts[2]);
        double alpha = parts.length == 4 ? ColorMath.parseAlpha(parts[3]) : 1.0;
        h = ((h % 360) + 360) % 360;
        return new OklchColor(l, c, h, alpha);
    }
}
