package in.virit.color;

import java.util.Optional;

///
/// Represents a CSS color.
///
///  To create an instance from a CSS string, use the
///  static {@link #parseCssColor(String)} method or directly one of the "of" methods
///  of the implementing classes {@link RgbColor}, {@link HexColor}, {@link HslColor},
///  {@link NamedColor}, {@link HwbColor}, {@link LabColor}, {@link LchColor},
///  {@link OklabColor}, {@link OklchColor} or {@link ColorFunction}.
///
public interface Color {

    /**
     * Converts the CSS color to a basic rgb format.
     *
     * @return the color in rgb format.
     */
    RgbColor toRgbColor();

    /**
     * Parses a CSS color string and returns a Color object. Supports:
     * {@code #hex}, {@code rgb()/rgba()}, {@code hsl()/hsla()}, {@code hwb()},
     * {@code lab()}, {@code lch()}, {@code oklab()}, {@code oklch()},
     * {@code color()} with predefined spaces, and CSS named colors.
     * <p>
     *     CSS variables, {@code none} keyword, {@code color-mix()} and relative
     *     color syntax are not supported.
     * </p>
     * <p>
     *     Throws {@link IllegalArgumentException} for any malformed input. Use
     *     {@link #tryParseCssColor(String)} when the caller wants to recover
     *     from parse failures (for example when consuming untrusted SVG/CSS).
     * </p>
     * @param cssColorString the CSS color string to parse
     * @return a Color object representing the parsed color
     */
    static Color parseCssColor(String cssColorString) {
        String s = cssColorString.trim();
        if (s.startsWith("#")) {
            return HexColor.of(s);
        } else if (s.startsWith("rgb")) {
            return RgbColor.of(s);
        } else if (s.startsWith("hsl")) {
            return HslColor.of(s);
        } else if (s.startsWith("hwb")) {
            return HwbColor.of(s);
        } else if (s.startsWith("oklab")) {
            return OklabColor.of(s);
        } else if (s.startsWith("oklch")) {
            return OklchColor.of(s);
        } else if (s.startsWith("lab")) {
            return LabColor.of(s);
        } else if (s.startsWith("lch")) {
            return LchColor.of(s);
        } else if (s.startsWith("color(")) {
            return ColorFunction.of(s);
        } else {
            return NamedColor.of(s);
        }
    }

    /**
     * Lenient counterpart of {@link #parseCssColor(String)}. Returns
     * {@link Optional#empty()} for {@code null} input or any value that
     * cannot be parsed, instead of throwing. Successful parses return the
     * same {@link Color} that {@code parseCssColor} would.
     * <p>
     *     Intended for callers that consume color strings from untrusted or
     *     loosely-validated sources (e.g. SVG attributes) and would rather
     *     fall back to a default than abort.
     * </p>
     * @param cssColorString the CSS color string to parse, or {@code null}
     * @return the parsed color, or empty if the input was null or malformed
     */
    static Optional<Color> tryParseCssColor(String cssColorString) {
        if (cssColorString == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(parseCssColor(cssColorString));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

}
