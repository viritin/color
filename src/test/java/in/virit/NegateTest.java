package in.virit;

import in.virit.color.Color;
import in.virit.color.HslColor;
import in.virit.color.NamedColor;
import in.virit.color.RgbColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NegateTest {

    @Test
    void primariesInvertToCmy() {
        // Pure primaries are the cases where sRGB inversion and HSL complement
        // agree: red↔cyan, green↔magenta, blue↔yellow.
        assertEquals(new RgbColor(0, 255, 255), new RgbColor(255, 0, 0).negate());
        assertEquals(new RgbColor(255, 0, 255), new RgbColor(0, 255, 0).negate());
        assertEquals(new RgbColor(255, 255, 0), new RgbColor(0, 0, 255).negate());
    }

    @Test
    void whiteAndBlackInvert() {
        // The cases where sRGB inversion diverges from HSL complement.
        assertEquals(new RgbColor(0, 0, 0), new RgbColor(255, 255, 255).negate());
        assertEquals(new RgbColor(255, 255, 255), new RgbColor(0, 0, 0).negate());
    }

    @Test
    void midGrayInvertsToNearGray() {
        // Mid-gray inverts to 127 (since 255 - 128 = 127), not exactly mid-gray.
        RgbColor inverted = new RgbColor(128, 128, 128).negate();
        assertEquals(127, inverted.r());
        assertEquals(127, inverted.g());
        assertEquals(127, inverted.b());
    }

    @Test
    void alphaIsPreserved() {
        RgbColor input = new RgbColor(128, 0, 64, 0.5);
        RgbColor inverted = input.negate();
        assertEquals(127, inverted.r());
        assertEquals(255, inverted.g());
        assertEquals(191, inverted.b());
        assertEquals(0.5, inverted.a());
    }

    @Test
    void worksAcrossTypes() {
        // The default method on Color routes through toRgbColor, so any
        // implementation gets it for free.
        RgbColor fromNamed = NamedColor.RED.negate();
        assertEquals(new RgbColor(0, 255, 255), fromNamed);

        // HSL red → toRgbColor → invert → cyan
        RgbColor fromHsl = new HslColor(0, 100, 50).negate();
        assertEquals(new RgbColor(0, 255, 255, 1.0), fromHsl);
    }

    @Test
    void namedComplementPair() {
        // Sanity that the operation matches the obvious named-color pair.
        Color cyan = Color.parseCssColor("cyan");
        assertEquals(cyan.toRgbColor(), NamedColor.RED.negate());
    }
}
