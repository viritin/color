package in.virit.color;

/**
 * Represents a CSS color.
 */
public interface CssColor {

    RgbColor toRgbaColor();

    /**
     * Parses a CSS color string and returns a Color object.
     * <p>
     *     Css variables and calculations are not supported.
     * </p>
     * @param cssColorString the CSS color string to parse
     * @return a Color object representing the parsed color
     */
    static CssColor parse(String cssColorString) {
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
