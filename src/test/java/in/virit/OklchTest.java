package in.virit;

import in.virit.color.OklabColor;
import in.virit.color.OklchColor;
import in.virit.color.RgbColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OklchTest {

    @Test
    void format() {
        String s = new OklchColor(0.5, 0.2, 120).toString();
        assertTrue(s.startsWith("oklch(0.5 0.2 120"), "Got: " + s);
    }

    @Test
    void testOklchToRgb() {
        // White: oklch(1 0 0)
        RgbColor white = new OklchColor(1, 0, 0).toRgbColor();
        assertEquals(255, white.r());
        assertEquals(255, white.g());
        assertEquals(255, white.b());

        // Reference: sRGB red ≈ oklch(0.6279 0.2576 29.23)
        RgbColor red = new OklchColor(0.6279, 0.2576, 29.23).toRgbColor();
        assertWithinTolerance(255, red.r(), 2);
        assertWithinTolerance(0, red.g(), 2);
        assertWithinTolerance(0, red.b(), 2);
    }

    @Test
    void testOklchOklabRoundtrip() {
        OklchColor original = new OklchColor(0.5, 0.2, 120);
        OklabColor lab = original.toOklabColor();
        OklchColor back = lab.toOklchColor();
        assertEquals(original.l(), back.l(), 1e-9);
        assertEquals(original.c(), back.c(), 1e-9);
        assertEquals(original.h(), back.h(), 1e-6);
    }

    @Test
    void testParsing() {
        OklchColor parsed = OklchColor.of("oklch(0.628 0.258 29.234)");
        assertEquals(0.628, parsed.l());
        assertEquals(0.258, parsed.c());
        assertEquals(29.234, parsed.h(), 1e-9);

        // hue with degrees
        OklchColor withDeg = OklchColor.of("oklch(0.5 0.2 90deg)");
        assertEquals(90, withDeg.h(), 1e-9);

        // c as percent: 100% = 0.4
        OklchColor pctC = OklchColor.of("oklch(0.5 100% 0)");
        assertEquals(0.4, pctC.c(), 1e-9);

        OklchColor withAlpha = OklchColor.of("oklch(0.5 0.2 0 / 0.5)");
        assertEquals(0.5, withAlpha.alpha());
    }

    @Test
    void testValidation() {
        assertThrows(IllegalArgumentException.class, () -> new OklchColor(-0.1, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new OklchColor(0.5, -0.1, 0));
        assertThrows(IllegalArgumentException.class, () -> new OklchColor(0.5, 0, 361));
    }

    private static void assertWithinTolerance(int expected, int actual, int tol) {
        int diff = Math.abs(expected - actual);
        assertTrue(diff <= tol,
                "Expected " + expected + " ± " + tol + " but got " + actual);
    }
}
