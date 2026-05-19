package in.virit;

import in.virit.color.Color;
import in.virit.color.NamedColor;
import in.virit.color.RgbColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RgbTest {

    @Test
    void invalidValues() {
        assertThrows(IllegalArgumentException.class, () -> {
            new RgbColor(255, 0, 0, 2); // should throw exception
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new RgbColor(255, -1, 0, 0); // should throw exception
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new RgbColor(256, 0, 0, 0); // should throw exception
        });
    }

    @Test
    void format() {

        RgbColor hslaColor = new RgbColor(90, 100, 50, 1);
        assertEquals("rgb(90 100 50)", hslaColor.toString());

        hslaColor = new RgbColor(90, 100, 50, 0.5f);

        assertEquals("rgb(90 100 50 / 0.50)", hslaColor.toString());

        RgbColor rgbaColor = NamedColor.RED.toRgbColor();
        assertEquals("rgb(255 0 0)", rgbaColor.toString());
        RgbColor translucentRed = rgbaColor.withAlpha(0.5);
        assertEquals("rgb(255 0 0 / 0.50)", translucentRed.toString());

    }

    @Test
    void modernSpacedSyntax() {
        // CSS Color 4: whitespace-separated components, no commas.
        RgbColor red = RgbColor.of("rgb(255 0 0)");
        assertEquals(255, red.r());
        assertEquals(0, red.g());
        assertEquals(0, red.b());
        assertEquals(1.0, red.a());
    }

    @Test
    void slashAlphaWithoutSpaces() {
        // CSS Color 4 allows the slash separator with no whitespace around it.
        RgbColor red = RgbColor.of("rgb(255 0 0/50%)");
        assertEquals(255, red.r());
        assertEquals(0, red.g());
        assertEquals(0, red.b());
        assertEquals(0.5, red.a(), 0.001);
    }

    @Test
    void slashAlphaPartialSpaces() {
        // Slash separator with only one side spaced.
        RgbColor a = RgbColor.of("rgb(255 0 0 /50%)");
        assertEquals(0.5, a.a(), 0.001);
        RgbColor b = RgbColor.of("rgb(255 0 0/ 50%)");
        assertEquals(0.5, b.a(), 0.001);
    }

    @Test
    void slashAlphaParsedViaColorParse() {
        // Color.parseCssColor() must not fall back / fail for valid CSS.
        Color c = Color.parseCssColor("rgb(255 0 0/50%)");
        RgbColor rgb = c.toRgbColor();
        assertEquals(255, rgb.r());
        assertEquals(0, rgb.g());
        assertEquals(0, rgb.b());
        assertEquals(0.5, rgb.a(), 0.001);
    }
}
