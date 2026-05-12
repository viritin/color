package in.virit;

import in.virit.color.LabColor;
import in.virit.color.LchColor;
import in.virit.color.RgbColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LabTest {

    @Test
    void format() {
        assertEquals("lab(50 20 -30)", new LabColor(50, 20, -30).toString());
        assertEquals("lab(50 20 -30 / 0.50)", new LabColor(50, 20, -30, 0.5).toString());
    }

    @Test
    void testLabToRgb() {
        // L=100, a=0, b=0 → white
        RgbColor white = new LabColor(100, 0, 0).toRgbColor();
        assertEquals(255, white.r());
        assertEquals(255, white.g());
        assertEquals(255, white.b());

        // L=0, a=0, b=0 → black
        RgbColor black = new LabColor(0, 0, 0).toRgbColor();
        assertEquals(0, black.r());
        assertEquals(0, black.g());
        assertEquals(0, black.b());

        // Reference: sRGB red ≈ lab(54.29 80.81 69.89) (CSS Color 4 spec example)
        RgbColor red = new LabColor(54.29, 80.81, 69.89).toRgbColor();
        assertWithinTolerance(255, red.r(), 2);
        assertWithinTolerance(0, red.g(), 2);
        assertWithinTolerance(0, red.b(), 2);
    }

    @Test
    void testLabToLch() {
        LchColor lch = new LabColor(50, 0, 0).toLchColor();
        assertEquals(0, lch.c(), 1e-9);
        assertEquals(0, lch.h(), 1e-9);
        assertEquals(50, lch.l());
    }

    @Test
    void testLabToLchAngle() {
        // a=0, b=positive → hue 90 degrees
        LchColor lch = new LabColor(50, 0, 50).toLchColor();
        assertEquals(50, lch.c(), 1e-9);
        assertEquals(90, lch.h(), 1e-9);
    }

    @Test
    void testParsing() {
        LabColor parsed = LabColor.of("lab(50 20 -30)");
        assertEquals(50, parsed.l());
        assertEquals(20, parsed.a());
        assertEquals(-30, parsed.b());

        // l as percent: 50% = 50
        LabColor pct = LabColor.of("lab(50% 20 -30)");
        assertEquals(50, pct.l(), 1e-9);

        // a/b as percent: 100% = 125
        LabColor pctAb = LabColor.of("lab(50 100% -100%)");
        assertEquals(125, pctAb.a(), 1e-9);
        assertEquals(-125, pctAb.b(), 1e-9);

        // legacy commas
        LabColor legacy = LabColor.of("lab(50, 20, -30)");
        assertEquals(20, legacy.a());

        LabColor withAlpha = LabColor.of("lab(50 20 -30 / 0.5)");
        assertEquals(0.5, withAlpha.alpha());
    }

    @Test
    void testValidation() {
        assertThrows(IllegalArgumentException.class, () -> new LabColor(-1, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new LabColor(101, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new LabColor(50, 0, 0, 1.5));
    }

    private static void assertWithinTolerance(int expected, int actual, int tol) {
        int diff = Math.abs(expected - actual);
        assertTrue(diff <= tol,
                "Expected " + expected + " ± " + tol + " but got " + actual);
    }
}
