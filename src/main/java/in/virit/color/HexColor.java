package in.virit.color;

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
    public HexColor {
        if (!isValidHex(hex)) {
            throw new IllegalArgumentException("Invalid hex color format: " + hex);
        }
    }

    private static boolean isValidHex(String hex) {
        if (hex == null) return false;
        int len = hex.length();
        // CSS hex colors: #RGB, #RRGGBB, or #RRGGBBAA.
        if (len != 4 && len != 7 && len != 9) return false;
        if (hex.charAt(0) != '#') return false;
        for (int i = 1; i < len; i++) {
            char c = hex.charAt(i);
            if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))) {
                return false;
            }
        }
        return true;
    }

    private static RgbColor toRgbColor(String hex) {
        if (hex.length() == 4) {
            String r = hex.substring(1, 2);
            String g = hex.substring(2, 3);
            String b = hex.substring(3, 4);
            // normalize short form
            hex = "#" + r + r + g + g + b + b;
        }
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

    @Override
    public String toString() {
        return hex;
    }

    @Override
    public RgbColor toRgbColor() {
        return toRgbColor(hex);
    }
}
