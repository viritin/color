package in.virit.color;

/**
 * Hex color representation. Technically this is RGB(A) color, but prints
 * as hex string.
 *
 * @param hex Hex color string, e.g. #FF5733 or #FF5733FF
 */
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
        int len = hex.length();
        if (len == 4) {
            // Short form #RGB: duplicate each digit (0xF -> 0xFF).
            int r = Character.digit(hex.charAt(1), 16) * 17;
            int g = Character.digit(hex.charAt(2), 16) * 17;
            int b = Character.digit(hex.charAt(3), 16) * 17;
            return new RgbColor(r, g, b);
        }
        // 7 or 9: #RRGGBB[AA]. Use the index-based parseInt overload so we
        // don't allocate a substring per channel.
        int r = Integer.parseInt(hex, 1, 3, 16);
        int g = Integer.parseInt(hex, 3, 5, 16);
        int b = Integer.parseInt(hex, 5, 7, 16);
        if (len == 9) {
            double a = Integer.parseInt(hex, 7, 9, 16) / 255.0;
            return new RgbColor(r, g, b, a);
        }
        return new RgbColor(r, g, b);
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
