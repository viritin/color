package in.virit;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import in.virit.color.Color;
import in.virit.color.ColorFunction;
import in.virit.color.HexColor;
import in.virit.color.HwbColor;
import in.virit.color.LabColor;
import in.virit.color.LchColor;
import in.virit.color.NamedColor;
import in.virit.color.OklabColor;
import in.virit.color.OklchColor;

public class ParsingTest {

    @Test
    void testParsing() {
        // Test parsing of different color formats
        String hexColor = "#FF5733";
        String rgbColor = "rgb(255, 87, 51)";
        String hslColor = "hsl(9, 100%, 60%)";
        Color parsed;


        Color parsed2, parsed3, parsed4, parsed5, parsed6;

        parsed = Color.parseCssColor("#FF5733");
        parsed = Color.parseCssColor("rgb(255, 87, 51)");
        parsed2 = Color.parseCssColor("rgb(255, 87, 51, 0.5)");
        parsed3 = Color.parseCssColor("rgb(255 87 51)");
        parsed4 = Color.parseCssColor("rgb(255 87 51 0.5)");
        assertEquals(parsed, parsed3);
        assertEquals(parsed2, parsed4);
        parsed = Color.parseCssColor("rgb(0, 100%, 60%, 0.5)");
        parsed2 = Color.parseCssColor("rgb(0, 255, 60%, 50%)");
        parsed3 = Color.parseCssColor("rgb(0%, 255, 60%, 50%)");

        assertEquals(parsed, parsed2);
        assertEquals(parsed3, parsed2);

        assertThrows(IllegalArgumentException.class, () -> {
            Color.parseCssColor("rgb(0, 100%, 60%, 1.5)"); // alpha out of range
        });


        parsed = Color.parseCssColor("hsl(9, 100%, 60%)");
        parsed = Color.parseCssColor("hsl(9, 100%, 60%, 0.5)");
        parsed = Color.parseCssColor("hsl(9 100% 60% / 0.5)");
        parsed2 = Color.parseCssColor("hsl(9 100% 60% / 50%)");

        assertEquals(parsed, parsed2);

        assertThrows(IllegalArgumentException.class, () -> {
            Color.parseCssColor("hsla(0, 101%, 60%, 1)"); // alpha out of range
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Color.parseCssColor("hsla(0, 99%, -1%, 1)"); // alpha out of range
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Color.parseCssColor("hsl(0 99% 1 / 1.3)"); // alpha out of range
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Color.parseCssColor("hsl(0 99% 1 / -0.5)"); // alpha out of range
        });

    }

    @Test
    void tryParseCssColorRecoversFromMalformedInput() {
        // Malformed hex — Bruno's #ggg case.
        assertFalse(Color.tryParseCssColor("#ggg").isPresent());
        // Hex of an invalid length.
        assertFalse(Color.tryParseCssColor("#1234567").isPresent());
        // Range violations.
        assertFalse(Color.tryParseCssColor("rgb(300, 0, 0)").isPresent());
        assertFalse(Color.tryParseCssColor("hsl(0, 101%, 50%)").isPresent());
        // Wrong arity / unparseable numbers.
        assertFalse(Color.tryParseCssColor("rgb(only one)").isPresent());
        assertFalse(Color.tryParseCssColor("rgb(a b c)").isPresent());
        // Unknown named color.
        assertFalse(Color.tryParseCssColor("notacolor").isPresent());
        // Null and empty.
        assertFalse(Color.tryParseCssColor(null).isPresent());
        assertFalse(Color.tryParseCssColor("").isPresent());
    }

    @Test
    void tryParseCssColorReturnsValidColors() {
        Optional<Color> hex = Color.tryParseCssColor("#ff0000");
        assertTrue(hex.isPresent());
        assertInstanceOf(HexColor.class, hex.get());
        assertEquals(255, hex.get().toRgbColor().r());

        Optional<Color> named = Color.tryParseCssColor("red");
        assertTrue(named.isPresent());
        assertInstanceOf(NamedColor.class, named.get());

        // Sanity: lenient path produces the same value as strict for valid input.
        assertEquals(Color.parseCssColor("rgb(255 0 0/50%)"),
                Color.tryParseCssColor("rgb(255 0 0/50%)").orElseThrow());
    }

    @Test
    void testCssColor4Dispatch() {
        assertInstanceOf(HwbColor.class, Color.parseCssColor("hwb(0 0% 0%)"));
        assertInstanceOf(LabColor.class, Color.parseCssColor("lab(50 20 -30)"));
        assertInstanceOf(LchColor.class, Color.parseCssColor("lch(50 30 120)"));
        // oklab/oklch must dispatch before lab/lch despite shared substrings
        assertInstanceOf(OklabColor.class, Color.parseCssColor("oklab(0.5 0.1 -0.1)"));
        assertInstanceOf(OklchColor.class, Color.parseCssColor("oklch(0.5 0.2 120)"));
        assertInstanceOf(ColorFunction.class, Color.parseCssColor("color(display-p3 1 0 0)"));
    }

}
