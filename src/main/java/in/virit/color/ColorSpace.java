package in.virit.color;

/**
 * Predefined color spaces accepted by the CSS {@code color()} function.
 *
 * <p>The CSS Color Level 4 specification defines two families:
 * <ul>
 *   <li><strong>RGB spaces</strong> — {@code srgb}, {@code srgb-linear},
 *       {@code display-p3}, {@code a98-rgb}, {@code prophoto-rgb}, {@code rec2020}</li>
 *   <li><strong>XYZ spaces</strong> — {@code xyz} (alias for {@code xyz-d65}),
 *       {@code xyz-d50}, {@code xyz-d65}</li>
 * </ul>
 */
public enum ColorSpace {

    /** sRGB with sRGB transfer function (the standard web color space). */
    SRGB("srgb"),

    /** Linear sRGB (no gamma applied). */
    SRGB_LINEAR("srgb-linear"),

    /** Apple Display P3 (D65), wider gamut than sRGB, sRGB transfer function. */
    DISPLAY_P3("display-p3"),

    /** Adobe RGB 1998 (D65), gamma 2.19921875. */
    A98_RGB("a98-rgb"),

    /** ProPhoto RGB (D50), gamma 1.8 with linear segment. */
    PROPHOTO_RGB("prophoto-rgb"),

    /** ITU-R BT.2020 (D65), HDR-capable wide gamut. */
    REC2020("rec2020"),

    /** CIE XYZ — alias of {@link #XYZ_D65} per CSS Color 4. */
    XYZ("xyz"),

    /** CIE XYZ at D50 white point. */
    XYZ_D50("xyz-d50"),

    /** CIE XYZ at D65 white point. */
    XYZ_D65("xyz-d65");

    private final String cssName;

    ColorSpace(String cssName) {
        this.cssName = cssName;
    }

    /**
     * Returns this color space's CSS identifier.
     *
     * @return the lowercase identifier used in CSS, e.g. {@code "display-p3"}.
     */
    public String cssName() {
        return cssName;
    }

    /**
     * Resolves a CSS color space identifier to its enum value.
     *
     * @param cssName CSS color space name, case-insensitive
     * @return the matching {@link ColorSpace}
     * @throws IllegalArgumentException if the identifier is not a known predefined space
     */
    public static ColorSpace fromCssName(String cssName) {
        String normalized = cssName.trim().toLowerCase(java.util.Locale.ROOT);
        for (ColorSpace s : values()) {
            if (s.cssName.equals(normalized)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown color space: " + cssName);
    }
}
