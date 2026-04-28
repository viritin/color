package in.virit.color.intellij;

import java.awt.Color;

/**
 * Minimal CSS color parser sufficient for swatch rendering.
 * Mirrors the formats accepted by RgbColor.of / HslColor.of / Color.parseCssColor.
 */
final class CssParse {

    static Color any(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.startsWith("#")) return HexUtil.parseHex(t);
        if (t.startsWith("rgb")) return rgb(t);
        if (t.startsWith("hsl")) return hsl(t);
        return null;
    }

    static Color rgb(String s) {
        String[] p = stripFn(s, "rgba?");
        if (p == null || (p.length != 3 && p.length != 4)) return null;
        try {
            int r = parseByte(p[0]);
            int g = parseByte(p[1]);
            int b = parseByte(p[2]);
            int a = p.length == 4 ? (int) Math.round(parseAlpha(p[3]) * 255) : 255;
            if (clamped(r) || clamped(g) || clamped(b) || clamped(a)) return null;
            return new Color(r, g, b, a);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static Color hsl(String s) {
        String[] p = stripFn(s, "hsla?");
        if (p == null || (p.length != 3 && p.length != 4)) return null;
        try {
            int h = parseAngle(p[0]);
            int sat = parsePercent(p[1]);
            int lum = parsePercent(p[2]);
            double a = p.length == 4 ? parseAlpha(p[3]) : 1.0;
            return hslToColor(h, sat, lum, a);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static Color hslToColor(int h, int s, int l, double alpha) {
        double sN = s / 100.0, lN = l / 100.0;
        double c = (1 - Math.abs(2 * lN - 1)) * sN;
        double x = c * (1 - Math.abs((h / 60.0) % 2 - 1));
        double m = lN - c / 2;
        double rp, gp, bp;
        if (h < 60)        { rp = c; gp = x; bp = 0; }
        else if (h < 120)  { rp = x; gp = c; bp = 0; }
        else if (h < 180)  { rp = 0; gp = c; bp = x; }
        else if (h < 240)  { rp = 0; gp = x; bp = c; }
        else if (h < 300)  { rp = x; gp = 0; bp = c; }
        else               { rp = c; gp = 0; bp = x; }
        int r = clamp((int) Math.round((rp + m) * 255));
        int g = clamp((int) Math.round((gp + m) * 255));
        int b = clamp((int) Math.round((bp + m) * 255));
        int aByte = clamp((int) Math.round(alpha * 255));
        return new Color(r, g, b, aByte);
    }

    private static String[] stripFn(String s, String fnPattern) {
        String t = s.trim().replaceAll("^" + fnPattern + "\\(", "");
        if (!t.endsWith(")")) return null;
        t = t.substring(0, t.length() - 1);
        t = t.replace(" /", " ");
        if (t.contains(",")) t = t.replace(',', ' ');
        return t.trim().split("\\s+");
    }

    private static int parseByte(String t) {
        if (t.endsWith("%")) {
            return (int) Math.round(Double.parseDouble(t.substring(0, t.length() - 1)) * 2.55);
        }
        return Integer.parseInt(t);
    }

    private static double parseAlpha(String t) {
        if (t.endsWith("%")) {
            return Double.parseDouble(t.substring(0, t.length() - 1)) / 100.0;
        }
        return Double.parseDouble(t);
    }

    private static int parsePercent(String t) {
        if (t.endsWith("%")) t = t.substring(0, t.length() - 1);
        return Integer.parseInt(t);
    }

    private static int parseAngle(String t) {
        if (t.endsWith("deg")) return Integer.parseInt(t.substring(0, t.length() - 3));
        if (t.endsWith("rad")) {
            return (int) Math.round(Math.toDegrees(Double.parseDouble(t.substring(0, t.length() - 3))));
        }
        if (t.endsWith("grad")) {
            return (int) Math.round(Double.parseDouble(t.substring(0, t.length() - 4)) * 0.9);
        }
        return Integer.parseInt(t);
    }

    private static boolean clamped(int v) {
        return v < 0 || v > 255;
    }

    private static int clamp(int v) {
        return v < 0 ? 0 : Math.min(v, 255);
    }

    private CssParse() {}
}
