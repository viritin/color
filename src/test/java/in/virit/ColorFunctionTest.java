package in.virit;

import in.virit.color.ColorFunction;
import in.virit.color.ColorSpace;
import in.virit.color.RgbColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ColorFunctionTest {

    @Test
    void format() {
        assertEquals("color(srgb 1 0 0)",
                new ColorFunction(ColorSpace.SRGB, 1, 0, 0).toString());
        assertEquals("color(display-p3 1 0 0)",
                new ColorFunction(ColorSpace.DISPLAY_P3, 1, 0, 0).toString());
        assertEquals("color(srgb 1 0 0 / 0.50)",
                new ColorFunction(ColorSpace.SRGB, 1, 0, 0, 0.5).toString());
    }

    @Test
    void testSrgb() {
        // color(srgb 1 0 0) should be pure red
        RgbColor red = new ColorFunction(ColorSpace.SRGB, 1, 0, 0).toRgbColor();
        assertEquals(255, red.r());
        assertEquals(0, red.g());
        assertEquals(0, red.b());

        // color(srgb 1 1 1) → white
        RgbColor white = new ColorFunction(ColorSpace.SRGB, 1, 1, 1).toRgbColor();
        assertEquals(255, white.r());
        assertEquals(255, white.g());
        assertEquals(255, white.b());
    }

    @Test
    void testSrgbLinear() {
        // color(srgb-linear 0 0 0) → black, (1 1 1) → white
        RgbColor white = new ColorFunction(ColorSpace.SRGB_LINEAR, 1, 1, 1).toRgbColor();
        assertEquals(255, white.r());

        // linear 0.5 → sRGB 0.735 → 188
        RgbColor mid = new ColorFunction(ColorSpace.SRGB_LINEAR, 0.5, 0.5, 0.5).toRgbColor();
        assertWithinTolerance(188, mid.r(), 1);
    }

    @Test
    void testDisplayP3() {
        // color(display-p3 1 0 0) — outside sRGB gamut, clamps but stays red-dominant
        RgbColor red = new ColorFunction(ColorSpace.DISPLAY_P3, 1, 0, 0).toRgbColor();
        assertEquals(255, red.r());
        // The clamped sRGB representation has a small negative green/blue which clamps to 0
        assertTrue(red.g() <= 5, "Expected near-zero green, got " + red.g());
        assertTrue(red.b() <= 5, "Expected near-zero blue, got " + red.b());

        // color(display-p3 1 1 1) → white (D65 maps cleanly)
        RgbColor white = new ColorFunction(ColorSpace.DISPLAY_P3, 1, 1, 1).toRgbColor();
        assertEquals(255, white.r());
        assertEquals(255, white.g());
        assertEquals(255, white.b());
    }

    @Test
    void testRec2020() {
        // White roundtrip
        RgbColor white = new ColorFunction(ColorSpace.REC2020, 1, 1, 1).toRgbColor();
        assertEquals(255, white.r());
        assertEquals(255, white.g());
        assertEquals(255, white.b());
    }

    @Test
    void testProphoto() {
        // ProPhoto white is at D50; after adaptation should reach near sRGB white
        RgbColor white = new ColorFunction(ColorSpace.PROPHOTO_RGB, 1, 1, 1).toRgbColor();
        assertWithinTolerance(255, white.r(), 2);
        assertWithinTolerance(255, white.g(), 2);
        assertWithinTolerance(255, white.b(), 2);
    }

    @Test
    void testA98() {
        // A98 (1,1,1) → sRGB white
        RgbColor white = new ColorFunction(ColorSpace.A98_RGB, 1, 1, 1).toRgbColor();
        assertEquals(255, white.r());
        assertEquals(255, white.g());
        assertEquals(255, white.b());
    }

    @Test
    void testXyz() {
        // D65 white point: X=0.9505, Y=1, Z=1.0888 → sRGB ≈ (255, 255, 255)
        RgbColor white = new ColorFunction(ColorSpace.XYZ_D65, 0.9505, 1.0, 1.0888).toRgbColor();
        assertWithinTolerance(255, white.r(), 1);
        assertWithinTolerance(255, white.g(), 1);
        assertWithinTolerance(255, white.b(), 1);

        // xyz is an alias for xyz-d65
        RgbColor alias = new ColorFunction(ColorSpace.XYZ, 0.9505, 1.0, 1.0888).toRgbColor();
        assertEquals(white.r(), alias.r());
        assertEquals(white.g(), alias.g());
        assertEquals(white.b(), alias.b());
    }

    @Test
    void testXyzD50() {
        // D50 white: X=0.96422, Y=1, Z=0.82521 → ~sRGB white after Bradford
        RgbColor white = new ColorFunction(ColorSpace.XYZ_D50, 0.96422, 1.0, 0.82521).toRgbColor();
        assertWithinTolerance(255, white.r(), 2);
        assertWithinTolerance(255, white.g(), 2);
        assertWithinTolerance(255, white.b(), 2);
    }

    @Test
    void testParsing() {
        ColorFunction parsed = ColorFunction.of("color(display-p3 1 0 0)");
        assertEquals(ColorSpace.DISPLAY_P3, parsed.space());
        assertEquals(1, parsed.c1());
        assertEquals(0, parsed.c2());
        assertEquals(0, parsed.c3());
        assertEquals(1, parsed.alpha());

        ColorFunction withAlpha = ColorFunction.of("color(srgb 1 0 0 / 0.5)");
        assertEquals(0.5, withAlpha.alpha());

        // percent component
        ColorFunction pct = ColorFunction.of("color(srgb 100% 0% 0%)");
        assertEquals(1.0, pct.c1(), 1e-9);

        assertThrows(IllegalArgumentException.class,
                () -> ColorFunction.of("color(bogus-space 1 0 0)"));
    }

    private static void assertWithinTolerance(int expected, int actual, int tol) {
        int diff = Math.abs(expected - actual);
        assertTrue(diff <= tol,
                "Expected " + expected + " ± " + tol + " but got " + actual);
    }
}
