package in.virit.color.netbeans;

import java.awt.Color;

final class HexUtil {

    static Color parseHex(String hex) {
        if (hex == null) return null;
        String s = hex.trim();
        if (!s.startsWith("#")) return null;
        s = s.substring(1);
        try {
            switch (s.length()) {
                case 3:
                    return new Color(
                            digit(s, 0) * 17,
                            digit(s, 1) * 17,
                            digit(s, 2) * 17);
                case 6:
                    return new Color(
                            Integer.parseInt(s.substring(0, 2), 16),
                            Integer.parseInt(s.substring(2, 4), 16),
                            Integer.parseInt(s.substring(4, 6), 16));
                case 8:
                    return new Color(
                            Integer.parseInt(s.substring(0, 2), 16),
                            Integer.parseInt(s.substring(2, 4), 16),
                            Integer.parseInt(s.substring(4, 6), 16),
                            Integer.parseInt(s.substring(6, 8), 16));
                default:
                    return null;
            }
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

    static int[] toHsl(Color c) {
        double r = c.getRed() / 255.0, g = c.getGreen() / 255.0, b = c.getBlue() / 255.0;
        double max = Math.max(r, Math.max(g, b)), min = Math.min(r, Math.min(g, b));
        double d = max - min;
        double h = 0;
        if (d != 0) {
            if (max == r) h = 60 * (((g - b) / d) % 6);
            else if (max == g) h = 60 * (((b - r) / d) + 2);
            else h = 60 * (((r - g) / d) + 4);
        }
        if (h < 0) h += 360;
        double l = (max + min) / 2;
        double s = d == 0 ? 0 : d / (1 - Math.abs(2 * l - 1));
        return new int[]{(int) Math.round(h), (int) Math.round(s * 100), (int) Math.round(l * 100)};
    }

    private static int digit(String s, int i) {
        return Integer.parseInt(s.substring(i, i + 1), 16);
    }

    private HexUtil() {}
}
