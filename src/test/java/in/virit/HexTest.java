package in.virit;

import in.virit.color.HexColor;
import in.virit.color.HslColor;
import in.virit.color.RgbColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HexTest {

    @Test
    void format() {

        HexColor hexColor = new HexColor("#FF573399");
        assertEquals("#FF573399", hexColor.toString());

        RgbColor rgbaColor = hexColor.toRgbaColor();
        assertEquals("rgb(255 87 51 / 0.60)", rgbaColor.toString());

    }
}
