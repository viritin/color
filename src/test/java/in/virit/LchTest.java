package in.virit;

import in.virit.color.LabColor;
import in.virit.color.LchColor;
import in.virit.color.RgbColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LchTest {

    @Test
    void format() {
        assertEquals("lch(50 30 120)", new LchColor(50, 30, 120).toString());
        assertEquals("lch(50 30 120 / 0.50)", new LchColor(50, 30, 120, 0.5).toString());
    }

    @Test
    void testLchToRgb() {
        // L=100, C=0, H=0 → white
        RgbColor white = new LchColor(100, 0, 0).toRgbColor();
        assertEquals(255, white.r());
        assertEquals(255, white.g());
        assertEquals(255, white.b());

        // L=0, C=0, H=0 → black
        RgbColor black = new LchColor(0, 0, 0).toRgbColor();
        assertEquals(0, black.r());
        assertEquals(0, black.g());
        assertEquals(0, black.b());

        // sRGB red ≈ lch(54.29 106.84 40.86) (CSS Color 4 spec example)
        RgbColor red = new LchColor(54.29, 106.84, 40.86).toRgbColor();
        assertWithinTolerance(255, red.r(), 2);
        assertWithinTolerance(0, red.g(), 2);
        assertWithinTolerance(0, red.b(), 2);
    }

    @Test
    void testLchLabRoundtrip() {
        LchColor original = new LchColor(50, 30, 120);
        LabColor lab = original.toLabColor();
        LchColor back = lab.toLchColor();
        assertEquals(original.l(), back.l(), 1e-9);
        assertEquals(original.c(), back.c(), 1e-9);
        assertEquals(original.h(), back.h(), 1e-6);
    }

    @Test
    void testParsing() {
        LchColor parsed = LchColor.of("lch(50 30 120)");
        assertEquals(50, parsed.l());
        assertEquals(30, parsed.c());
        assertEquals(120, parsed.h());

        // hue in degrees suffix
        LchColor withDeg = LchColor.of("lch(50 30 120deg)");
        assertEquals(120, withDeg.h(), 1e-9);

        // c as percent: 100% = 150
        LchColor pctC = LchColor.of("lch(50 100% 0)");
        assertEquals(150, pctC.c(), 1e-9);

        LchColor withAlpha = LchColor.of("lch(50 30 120 / 0.5)");
        assertEquals(0.5, withAlpha.alpha());
    }

    @Test
    void testValidation() {
        assertThrows(IllegalArgumentException.class, () -> new LchColor(-1, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new LchColor(50, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> new LchColor(50, 0, 361));
    }

    private static void assertWithinTolerance(int expected, int actual, int tol) {
        int diff = Math.abs(expected - actual);
        assertTrue(diff <= tol,
                "Expected " + expected + " ± " + tol + " but got " + actual);
    }
}
