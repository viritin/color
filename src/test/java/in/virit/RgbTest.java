package in.virit;

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
}
