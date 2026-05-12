package in.virit;

import in.virit.color.OklabColor;
import in.virit.color.OklchColor;
import in.virit.color.RgbColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OklabTest {

    @Test
    void format() {
        String s = new OklabColor(0.5, 0.1, -0.1).toString();
        assertTrue(s.startsWith("oklab(0.5 0.1 -0.1"), "Got: " + s);

        OklabColor withAlpha = new OklabColor(0.5, 0, 0, 0.5);
        assertTrue(withAlpha.toString().endsWith(" / 0.50)"), "Got: " + withAlpha);
    }

    @Test
    void testOklabToRgb() {
        // oklab(1 0 0) → white
        RgbColor white = new OklabColor(1, 0, 0).toRgbColor();
        assertEquals(255, white.r());
        assertEquals(255, white.g());
        assertEquals(255, white.b());

        // oklab(0 0 0) → black
        RgbColor black = new OklabColor(0, 0, 0).toRgbColor();
        assertEquals(0, black.r());
        assertEquals(0, black.g());
        assertEquals(0, black.b());

        // Reference: sRGB red ≈ oklab(0.6279 0.2249 0.1258)
        RgbColor red = new OklabColor(0.6279, 0.2249, 0.1258).toRgbColor();
        assertWithinTolerance(255, red.r(), 2);
        assertWithinTolerance(0, red.g(), 2);
        assertWithinTolerance(0, red.b(), 2);
    }

    @Test
    void testOklabToOklch() {
        OklchColor lch = new OklabColor(0.5, 0, 0).toOklchColor();
        assertEquals(0, lch.c(), 1e-9);
        assertEquals(0, lch.h(), 1e-9);

        // a=0.3, b=0 → h=0, c=0.3
        OklchColor pos = new OklabColor(0.5, 0.3, 0).toOklchColor();
        assertEquals(0.3, pos.c(), 1e-9);
        assertEquals(0, pos.h(), 1e-9);
    }

    @Test
    void testParsing() {
        OklabColor parsed = OklabColor.of("oklab(0.5 0.1 -0.1)");
        assertEquals(0.5, parsed.l());
        assertEquals(0.1, parsed.a());
        assertEquals(-0.1, parsed.b());

        // l as percent: 50% = 0.5
        OklabColor pctL = OklabColor.of("oklab(50% 0.1 -0.1)");
        assertEquals(0.5, pctL.l(), 1e-9);

        // a/b as percent: 100% = 0.4
        OklabColor pctAb = OklabColor.of("oklab(0.5 100% -50%)");
        assertEquals(0.4, pctAb.a(), 1e-9);
        assertEquals(-0.2, pctAb.b(), 1e-9);

        OklabColor withAlpha = OklabColor.of("oklab(0.5 0 0 / 0.5)");
        assertEquals(0.5, withAlpha.alpha());
    }

    @Test
    void testValidation() {
        assertThrows(IllegalArgumentException.class, () -> new OklabColor(-0.1, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new OklabColor(1.1, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new OklabColor(0.5, 0, 0, -0.1));
    }

    private static void assertWithinTolerance(int expected, int actual, int tol) {
        int diff = Math.abs(expected - actual);
        assertTrue(diff <= tol,
                "Expected " + expected + " ± " + tol + " but got " + actual);
    }
}
