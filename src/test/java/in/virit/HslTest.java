package in.virit;

import in.virit.color.Color;
import in.virit.color.HslColor;
import in.virit.color.RgbColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HslTest {

    @Test
    void format() {

        HslColor hslaColor = new HslColor(90, 100, 50, 1);
        assertEquals("hsl(90 100 50)", hslaColor.toString());

        hslaColor = new HslColor(90, 100, 50, 0.5f);

        assertEquals("hsl(90 100 50 / 0.50)", hslaColor.toString());
    }

    @Test
    void testHslColor() {
        /*
            Red
            #ff0000
            rgb(255, 0, 0)
            hsl(0, 100%, 50%)
         */

        HslColor hslColor = new HslColor(0, 100, 50);
        assertEquals("hsl(0 100 50)", hslColor.toString());
        RgbColor rgbaColor = hslColor.toRgbColor();
        assertEquals("rgb(255 0 0)", rgbaColor.toString());
        HslColor hslColor2 = rgbaColor.toHslColor();
        assertEquals("hsl(0 100 50)", hslColor2.toString());

        // Magenta	(300°,100%,50%)	#FF00FF	(255,0,255)

        hslColor = new HslColor(300, 100, 50);
        assertEquals("hsl(300 100 50)", hslColor.toString());
        rgbaColor = hslColor.toRgbColor();
        assertEquals("rgb(255 0 255)", rgbaColor.toString());

        // Navy	(240°,100%,25%)	#000080	(0,0,128)
        hslColor = new HslColor(240, 100, 25);
        assertEquals("hsl(240 100 25)", hslColor.toString());
        rgbaColor = hslColor.toRgbColor();
        assertEquals("rgb(0 0 128)", rgbaColor.toString());

    }
    
    
    @Test
    void testHslColorWithAlpha() {
        HslColor hslColor = new HslColor(240, 100, 50, 0.5);
        assertEquals("hsl(240 100 50 / 0.50)", hslColor.toString());

        var modified = hslColor.withAlpha(0.75);
        assertEquals(0.75, modified.a());
    }

    @Test
    void testLuminaceFunctions() {
        HslColor hslColor = new HslColor(240, 100, 50);
        assertEquals(50, hslColor.l());

        hslColor = hslColor.withLuminance(100);

        assertEquals(100, hslColor.l());

        hslColor = hslColor.lighten(10);

        assertEquals(100, hslColor.l());

        hslColor = hslColor.lighten(-10);
        assertEquals(90, hslColor.l());

        hslColor = hslColor.lighten(0.5);
        assertEquals(100, hslColor.l());

        hslColor = hslColor.darken(10);
        assertEquals(90, hslColor.l());

        hslColor = hslColor.darken(0.5);
        assertEquals(45, hslColor.l());

    }

    @Test
    void slashAlphaWithoutSpaces() {
        // CSS Color 4 allows slash separator with no whitespace around it.
        HslColor c = HslColor.of("hsl(0 100% 50%/50%)");
        assertEquals(0, c.h());
        assertEquals(100, c.s());
        assertEquals(50, c.l());
        assertEquals(0.5, c.a(), 0.001);
    }

    @Test
    void slashAlphaParsedViaColorParse() {
        Color c = Color.parseCssColor("hsl(0 100% 50%/50%)");
        RgbColor rgb = c.toRgbColor();
        assertEquals(255, rgb.r());
        assertEquals(0, rgb.g());
        assertEquals(0, rgb.b());
        assertEquals(0.5, rgb.a(), 0.001);
    }
}
