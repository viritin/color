package in.virit;

import in.virit.color.NamedColor;
import in.virit.color.RgbColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import in.virit.color.Color;

public class NamedColorsTest {

    @Test
    public void testNamedColors() {

        // Test the named colors
        assertEquals(Color.parseCssColor("#000000"), NamedColor.BLACK.toRgbColor().toHexColor());
        assertEquals(Color.parseCssColor("#FFFFFF"), NamedColor.WHITE.toRgbColor().toHexColor());
        assertEquals(Color.parseCssColor("#FF0000"), NamedColor.RED.toRgbColor().toHexColor());
        assertEquals(Color.parseCssColor("#008000"), NamedColor.GREEN.toRgbColor().toHexColor());
        assertEquals(Color.parseCssColor("#0000FF"), NamedColor.BLUE.toRgbColor().toHexColor());
        assertEquals(Color.parseCssColor("#FFFF00"), NamedColor.YELLOW.toRgbColor().toHexColor());
        assertEquals(Color.parseCssColor("#FF00FF"), NamedColor.MAGENTA.toRgbColor().toHexColor());
        assertEquals(Color.parseCssColor("#00FFFF"), NamedColor.CYAN.toRgbColor().toHexColor());
    }

    @Test
    public void caseInsensitiveLookup() {
        // CSS named colors are case-insensitive. All casings must resolve to the
        // same enum constant, so consumers (e.g. SVG parsing) need not lowercase
        // before delegating to the library.
        assertSame(NamedColor.RED, NamedColor.of("red"));
        assertSame(NamedColor.RED, NamedColor.of("RED"));
        assertSame(NamedColor.RED, NamedColor.of("Red"));
        assertSame(NamedColor.CORNFLOWERBLUE, NamedColor.of("CornflowerBlue"));
        assertSame(NamedColor.RED, (NamedColor) Color.parseCssColor("ReD"));
    }

    @Test
    public void transparentKeyword() {
        // CSS Color 4: "transparent" is a shorthand for rgba(0, 0, 0, 0).
        Color parsed = Color.parseCssColor("transparent");
        assertSame(NamedColor.TRANSPARENT, parsed);
        RgbColor rgb = parsed.toRgbColor();
        assertEquals(0, rgb.r());
        assertEquals(0, rgb.g());
        assertEquals(0, rgb.b());
        assertEquals(0.0, rgb.a());
    }
}
