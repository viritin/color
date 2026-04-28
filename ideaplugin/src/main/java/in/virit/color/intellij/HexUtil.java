package in.virit.color.intellij;

import java.awt.Color;

final class HexUtil {

    static Color parseHex(String hex) {
        if (hex == null) return null;
        String s = hex.trim();
        if (!s.startsWith("#")) return null;
        s = s.substring(1);
        try {
            return switch (s.length()) {
                case 3 -> new Color(
                        digit(s, 0) * 17,
                        digit(s, 1) * 17,
                        digit(s, 2) * 17);
                case 6 -> new Color(
                        Integer.parseInt(s.substring(0, 2), 16),
                        Integer.parseInt(s.substring(2, 4), 16),
                        Integer.parseInt(s.substring(4, 6), 16));
                case 8 -> new Color(
                        Integer.parseInt(s.substring(0, 2), 16),
                        Integer.parseInt(s.substring(2, 4), 16),
                        Integer.parseInt(s.substring(4, 6), 16),
                        Integer.parseInt(s.substring(6, 8), 16));
                default -> null;
            };
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static String toHex(Color c) {
        if (c.getAlpha() == 255) {
            return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
        }
        return String.format("#%02X%02X%02X%02X",
                c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    }

    private static int digit(String s, int i) {
        return Integer.parseInt(s.substring(i, i + 1), 16);
    }

    private HexUtil() {}
}
