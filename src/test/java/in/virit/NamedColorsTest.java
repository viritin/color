package in.virit;

import in.virit.color.NamedColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
