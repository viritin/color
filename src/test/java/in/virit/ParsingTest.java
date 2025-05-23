package in.virit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import in.virit.color.Color;

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

}
