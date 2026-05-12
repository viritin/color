package in.virit.color;

import in.virit.color.internal.ColorMath;

import java.util.Locale;

/**
 * A CSS {@code color()} function value referencing one of the predefined
 * {@link ColorSpace}s. RGB-family components are expressed in their native
 * 0..1 range; XYZ-family components are tristimulus values (typically 0..1
 * for in-gamut white-referenced colors but unbounded in principle).
 *
 * @param space the predefined color space
 * @param c1    first component (R for RGB spaces, X for XYZ)
 * @param c2    second component (G or Y)
 * @param c3    third component (B or Z)
 * @param alpha Alpha (0–1)
 */
public record ColorFunction(ColorSpace space, double c1, double c2, double c3, double alpha) implements Color {

    /**
     * Validating constructor.
     *
     * @param space color space
     * @param c1    first component
     * @param c2    second component
     * @param c3    third component
     * @param alpha Alpha (0–1)
     */
    public ColorFunction {
        if (space == null) {
            throw new IllegalArgumentException("Color space must not be null");
        }
        if (alpha < 0 || alpha > 1) {
            throw new IllegalArgumentException("Alpha value must be between 0 and 1");
        }
    }

    /**
     * Convenience constructor, alpha defaults to 1.
     *
     * @param space color space
     * @param c1    first component
     * @param c2    second component
     * @param c3    third component
     */
    public ColorFunction(ColorSpace space, double c1, double c2, double c3) {
        this(space, c1, c2, c3, 1);
    }

    @Override
    public RgbColor toRgbColor() {
        double[] lin = toLinearSrgb();
        return new RgbColor(
                ColorMath.clampToByte(ColorMath.linearSrgbToSrgb(lin[0])),
                ColorMath.clampToByte(ColorMath.linearSrgbToSrgb(lin[1])),
                ColorMath.clampToByte(ColorMath.linearSrgbToSrgb(lin[2])),
                alpha);
    }

    private double[] toLinearSrgb() {
        switch (space) {
            case SRGB:
                return new double[] {
                        ColorMath.srgbToLinearSrgb(c1),
                        ColorMath.srgbToLinearSrgb(c2),
                        ColorMath.srgbToLinearSrgb(c3)
                };
            case SRGB_LINEAR:
                return new double[] { c1, c2, c3 };
            case DISPLAY_P3:
                return ColorMath.displayP3ToLinearSrgb(c1, c2, c3);
            case A98_RGB:
                return ColorMath.a98RgbToLinearSrgb(c1, c2, c3);
            case PROPHOTO_RGB:
                return ColorMath.prophotoRgbToLinearSrgb(c1, c2, c3);
            case REC2020:
                return ColorMath.rec2020ToLinearSrgb(c1, c2, c3);
            case XYZ:
            case XYZ_D65:
                return ColorMath.xyzD65ToLinearSrgb(c1, c2, c3);
            case XYZ_D50: {
                double[] xyzD65 = ColorMath.xyzD50ToXyzD65(c1, c2, c3);
                return ColorMath.xyzD65ToLinearSrgb(xyzD65[0], xyzD65[1], xyzD65[2]);
            }
            default:
                throw new IllegalStateException("Unsupported color space: " + space);
        }
    }

    /**
     * Returns a copy of this color with a different alpha value.
     *
     * @param newAlpha new alpha (0–1)
     * @return new ColorFunction with the given alpha
     */
    public ColorFunction withAlpha(double newAlpha) {
        return new ColorFunction(space, c1, c2, c3, newAlpha);
    }

    @Override
    public String toString() {
        String alphaPart = alpha == 1.0 ? "" : String.format(Locale.US, " / %.2f", alpha);
        return String.format(Locale.US, "color(%s %s %s %s%s)",
                space.cssName(), formatNum(c1), formatNum(c2), formatNum(c3), alphaPart);
    }

    private static String formatNum(double v) {
        if (v == Math.floor(v) && !Double.isInfinite(v) && Math.abs(v) < 1e15) {
            return String.valueOf((long) v);
        }
        return String.format(Locale.US, "%.4f", v).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    /**
     * Parses a CSS {@code color(<space> c1 c2 c3 [/ alpha])} string.
     *
     * @param cssColorString e.g. {@code "color(display-p3 1 0 0)"} or {@code "color(srgb 1 0 0 / 0.5)"}
     * @return parsed ColorFunction
     */
    public static ColorFunction of(String cssColorString) {
        String[] parts = ColorMath.splitComponents(cssColorString);
        if (parts.length < 4 || parts.length > 5) {
            throw new IllegalArgumentException("color() requires a space name and 3 or 4 components: " + cssColorString);
        }
        ColorSpace space = ColorSpace.fromCssName(parts[0]);
        double c1 = ColorMath.parsePercentOrNumber(parts[1], 1.0);
        double c2 = ColorMath.parsePercentOrNumber(parts[2], 1.0);
        double c3 = ColorMath.parsePercentOrNumber(parts[3], 1.0);
        double alpha = parts.length == 5 ? ColorMath.parseAlpha(parts[4]) : 1.0;
        return new ColorFunction(space, c1, c2, c3, alpha);
    }
}
