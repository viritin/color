package in.virit;

import in.virit.color.HwbColor;
import in.virit.color.RgbColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HwbTest {

    @Test
    void format() {
        assertEquals("hwb(120 50 0)", new HwbColor(120, 50, 0).toString());
        assertEquals("hwb(0 0 0 / 0.50)", new HwbColor(0, 0, 0, 0.5).toString());
    }

    @Test
    void testHwbToRgb() {
        // hwb(0 0% 0%) is pure red
        assertEquals("rgb(255 0 0)", new HwbColor(0, 0, 0).toRgbColor().toString());
        // hwb(120 0% 0%) is pure green
        assertEquals("rgb(0 255 0)", new HwbColor(120, 0, 0).toRgbColor().toString());
        // hwb(240 0% 0%) is pure blue
        assertEquals("rgb(0 0 255)", new HwbColor(240, 0, 0).toRgbColor().toString());
        // hwb(0 100% 0%) is white
        assertEquals("rgb(255 255 255)", new HwbColor(0, 100, 0).toRgbColor().toString());
        // hwb(0 0% 100%) is black
        assertEquals("rgb(0 0 0)", new HwbColor(0, 0, 100).toRgbColor().toString());
        // hwb(0 50% 50%) — w+b==100, grayscale at w/(w+b) = 0.5 → 128
        RgbColor gray = new HwbColor(0, 50, 50).toRgbColor();
        assertEquals(128, gray.r());
        assertEquals(128, gray.g());
        assertEquals(128, gray.b());
    }

    @Test
    void testParsing() {
        HwbColor parsed = HwbColor.of("hwb(120 50% 0%)");
        assertEquals(120, parsed.h());
        assertEquals(50, parsed.w());
        assertEquals(0, parsed.b());
        assertEquals(1, parsed.alpha());

        HwbColor withAlpha = HwbColor.of("hwb(120 50% 0% / 0.5)");
        assertEquals(0.5, withAlpha.alpha());

        // legacy commas (some sources emit this even though spec uses spaces)
        HwbColor legacy = HwbColor.of("hwb(120, 50%, 0%)");
        assertEquals(120, legacy.h());
        assertEquals(50, legacy.w());

        // alpha as percentage
        HwbColor pctAlpha = HwbColor.of("hwb(0 0% 0% / 50%)");
        assertEquals(0.5, pctAlpha.alpha());
    }

    @Test
    void testAngleUnits() {
        assertEquals(180, HwbColor.of("hwb(180deg 0% 0%)").h());
        // 0.5 turn = 180 degrees
        assertEquals(180, HwbColor.of("hwb(0.5turn 0% 0%)").h(), 1e-9);
        // π radians ≈ 180 degrees
        assertEquals(180, HwbColor.of("hwb(3.141592653589793rad 0% 0%)").h(), 1e-6);
        // 200 gradians = 180 degrees
        assertEquals(180, HwbColor.of("hwb(200grad 0% 0%)").h(), 1e-9);
    }

    @Test
    void testValidation() {
        assertThrows(IllegalArgumentException.class, () -> new HwbColor(361, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new HwbColor(0, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> new HwbColor(0, 0, 101));
        assertThrows(IllegalArgumentException.class, () -> new HwbColor(0, 0, 0, 1.1));
    }

    @Test
    void testWithAlpha() {
        HwbColor c = new HwbColor(120, 50, 0).withAlpha(0.25);
        assertEquals(0.25, c.alpha());
        assertEquals(120, c.h());
    }
}
