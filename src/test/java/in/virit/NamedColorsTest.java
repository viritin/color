package in.virit;

import in.virit.color.CssColor;
import in.virit.color.NamedColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NamedColorsTest {

    @Test
    public void testNamedColors() {

        // Test the named colors
        assertEquals(CssColor.parse("#000000"), NamedColor.BLACK.toRgbColor().toHexColor());
        assertEquals(CssColor.parse("#FFFFFF"), NamedColor.WHITE.toRgbColor().toHexColor());
        assertEquals(CssColor.parse("#FF0000"), NamedColor.RED.toRgbColor().toHexColor());
        assertEquals(CssColor.parse("#008000"), NamedColor.GREEN.toRgbColor().toHexColor());
        assertEquals(CssColor.parse("#0000FF"), NamedColor.BLUE.toRgbColor().toHexColor());
        assertEquals(CssColor.parse("#FFFF00"), NamedColor.YELLOW.toRgbColor().toHexColor());
        assertEquals(CssColor.parse("#FF00FF"), NamedColor.MAGENTA.toRgbColor().toHexColor());
        assertEquals(CssColor.parse("#00FFFF"), NamedColor.CYAN.toRgbColor().toHexColor());
    }
}
