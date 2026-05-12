package in.virit.color;

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

}
