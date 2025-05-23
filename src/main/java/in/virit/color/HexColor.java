package in.virit.color;

import com.fasterxml.jackson.annotation.JsonValue;

///
/// Hex color representation. Technically this is RGB(A) color, but prints
/// as hex string.
///
/// @param hex Hex color string, e.g. #FF5733 or #FF5733FF
///
public record HexColor(String hex) implements Color {


    /**
     * Basic constructor for HexColor.
     *
     * @param hex Hex color string, e.g. #FF5733 or #FF5733FF
     */
    public HexColor{
        // Validate the hex color format
        if (hex == null || !hex.matches("^#([0-9A-Fa-f]{8}|[0-9A-Fa-f]{6}|[0-9A-Fa-f]{3})$")) {
            throw new IllegalArgumentException("Invalid hex color format: " + hex);
        }
    }

    @Override
    @JsonValue
    public String toString() {
        return hex;
    }

    @Override
    public RgbColor toRgbColor() {
        RgbColor rgbColor = new RgbColor(
                Integer.parseInt(hex.substring(1, 3), 16),
                Integer.parseInt(hex.substring(3, 5), 16),
                Integer.parseInt(hex.substring(5, 7), 16)
        );
        if (hex.length() == 9) {
            rgbColor = rgbColor.withAlpha(Integer.parseInt(hex.substring(7, 9), 16) / 255.0);
        }
        return rgbColor;
    }

    /**
     * Parses a CSS color string and returns a HexColor object.
     * <p>
     * Css variables and calculations are not supported.
     * </p>
     *
     * @param cssColorString the CSS color string to parse
     * @return a HexColor object representing the parsed color
     */
    public static HexColor of(String cssColorString) {
        return new HexColor(cssColorString);
    }
}
