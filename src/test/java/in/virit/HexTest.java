package in.virit;

import in.virit.color.HexColor;
import in.virit.color.RgbColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class HexTest {

    @Test
    void format() {

        HexColor hexColor = new HexColor("#FF573399");
        assertEquals("#FF573399", hexColor.toString());

        RgbColor rgbaColor = hexColor.toRgbColor();
        assertEquals("rgb(255 87 51 / 0.60)", rgbaColor.toString());

    }

    @Test
    void shortForm() {

        HexColor shortHex = new HexColor("#f60");
        HexColor hexColor = new HexColor("#ff6600");
        assertNotEquals(shortHex, hexColor);
        assertEquals(shortHex.toRgbColor(), hexColor.toRgbColor());

    }

}
