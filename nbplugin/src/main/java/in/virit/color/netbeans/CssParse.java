package in.virit.color.netbeans;

import in.virit.color.RgbColor;

import java.awt.Color;

/**
 * Thin adapter from CSS color strings to {@link Color}, delegating to the
 * bundled {@code in.virit:color} library. Supports the full set of formats
 * the library understands: hex, named, rgb()/rgba(), hsl()/hsla(), hwb(),
 * lab(), lch(), oklab(), oklch(), and color() with predefined spaces.
 */
final class CssParse {

    static Color any(String s) {
        if (s == null) return null;
        try {
            RgbColor rgb = in.virit.color.Color.parseCssColor(s.trim()).toRgbColor();
            return toAwt(rgb);
        } catch (RuntimeException e) {
            return null;
        }
    }

    static Color rgb(String s) {
        if (s == null) return null;
        try {
            return toAwt(RgbColor.of(s.trim()));
        } catch (RuntimeException e) {
            return null;
        }
    }

    static Color hsl(String s) {
        if (s == null) return null;
        try {
            return toAwt(in.virit.color.HslColor.of(s.trim()).toRgbColor());
        } catch (RuntimeException e) {
            return null;
        }
    }

    /** Build an AWT Color for {@code new HslColor(h, s, l[, alpha])} integer args. */
    static Color hslToColor(int h, int s, int l, double alpha) {
        if (h < 0 || h > 360 || s < 0 || s > 100 || l < 0 || l > 100) return null;
        if (alpha < 0 || alpha > 1) return null;
        return toAwt(new in.virit.color.HslColor(h, s, l, alpha).toRgbColor());
    }

    static Color toAwt(RgbColor c) {
        int alpha = (int) Math.round(Math.max(0, Math.min(1, c.a())) * 255);
        return new Color(c.r(), c.g(), c.b(), alpha);
    }

    private CssParse() {}
}
