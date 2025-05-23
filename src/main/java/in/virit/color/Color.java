package in.virit.color;

///
/// Represents a CSS color.
///
///  To create an instance from a CSS string, use the
///  static {@link #parseCssColor(String)} method or directly on of the "of" methods
///  of the implementing classes {@link RgbColor}, {@link HexColor}, {@link HslColor}
///  or {@link NamedColor}.
///
public interface Color {

    /**
     * Converts the CSS color to a basic rgb format.
     *
     * @return the color in rgb format.
     */
    RgbColor toRgbColor();

    /**
     * Parses a CSS color string and returns a Color object.
     * <p>
     *     Css variables and calculations are not supported.
     * </p>
     * @param cssColorString the CSS color string to parse
     * @return a Color object representing the parsed color
     */
    static Color parseCssColor(String cssColorString) {
        // Check for named colors

        if (cssColorString.startsWith("#")) {
            // Hex color
            return HexColor.of(cssColorString);
        } else if (cssColorString.startsWith("rgb")) {
            return RgbColor.of(cssColorString);
        } else if (cssColorString.startsWith("hsl")) {
            // HSL color
            return HslColor.of(cssColorString);
        } else {
            return NamedColor.of(cssColorString);
        }
    }

}
