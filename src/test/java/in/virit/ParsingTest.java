package in.virit;

import in.virit.color.CssColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParsingTest {

    @Test
    void testParsing() {
        // Test parsing of different color formats
        String hexColor = "#FF5733";
        String rgbColor = "rgb(255, 87, 51)";
        String hslColor = "hsl(9, 100%, 60%)";


        CssColor parsed, parsed2, parsed3, parsed4, parsed5, parsed6;

        parsed = CssColor.parse("#FF5733");
        parsed = CssColor.parse("rgb(255, 87, 51)");
        parsed2 = CssColor.parse("rgb(255, 87, 51, 0.5)");
        parsed3 = CssColor.parse("rgb(255 87 51)");
        parsed4 = CssColor.parse("rgb(255 87 51 0.5)");
        assertEquals(parsed, parsed3);
        assertEquals(parsed2, parsed4);
        parsed = CssColor.parse("rgb(0, 100%, 60%, 0.5)");
        parsed2 = CssColor.parse("rgb(0, 255, 60%, 50%)");
        parsed3 = CssColor.parse("rgb(0%, 255, 60%, 50%)");

        assertEquals(parsed, parsed2);
        assertEquals(parsed3, parsed2);

        assertThrows(IllegalArgumentException.class, () -> {
            CssColor.parse("rgb(0, 100%, 60%, 1.5)"); // alpha out of range
        });


        parsed = CssColor.parse("hsl(9, 100%, 60%)");
        parsed = CssColor.parse("hsl(9, 100%, 60%, 0.5)");
        parsed = CssColor.parse("hsl(9 100% 60% / 0.5)");
        parsed2 = CssColor.parse("hsl(9 100% 60% / 50%)");

        assertEquals(parsed, parsed2);

        assertThrows(IllegalArgumentException.class, () -> {
            CssColor.parse("hsla(0, 101%, 60%, 1)"); // alpha out of range
        });

        assertThrows(IllegalArgumentException.class, () -> {
            CssColor.parse("hsla(0, 99%, -1%, 1)"); // alpha out of range
        });

        assertThrows(IllegalArgumentException.class, () -> {
            CssColor.parse("hsl(0 99% 1 / 1.3)"); // alpha out of range
        });
        assertThrows(IllegalArgumentException.class, () -> {
            CssColor.parse("hsl(0 99% 1 / -0.5)"); // alpha out of range
        });

    }

}
