package in.virit.color.netbeans;

import com.junichi11.netbeans.modules.color.codes.preview.spi.ColorValue;
import org.junit.Test;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Smoke tests for the regex matchers. The Document parameter to
 * getColorValues is unused for these line-only patterns, so we pass null.
 */
public class ViritinColorCodesProviderTest {

    private final ViritinColorCodesProvider provider = new ViritinColorCodesProvider();

    private List<ColorValue> scan(String line) {
        return provider.getColorValues(null, line, 0, Collections.emptyMap());
    }

    @Test
    public void newRgb3() {
        List<ColorValue> values = scan("Color a = new RgbColor(255, 0, 0);");
        assertEquals(1, values.size());
        assertEquals(Color.RED, values.get(0).getColor());
    }

    @Test
    public void newRgb4() {
        List<ColorValue> values = scan("Color a = new RgbColor(0, 255, 0, 0.5);");
        assertEquals(1, values.size());
        Color c = values.get(0).getColor();
        assertEquals(0, c.getRed());
        assertEquals(255, c.getGreen());
        assertEquals(0, c.getBlue());
        assertTrue("alpha ~128", Math.abs(c.getAlpha() - 128) <= 1);
    }

    @Test
    public void rgbOf() {
        List<ColorValue> values = scan("var c = RgbColor.of(\"rgb(0 0 255)\");");
        assertEquals(1, values.size());
        assertEquals(Color.BLUE, values.get(0).getColor());
    }

    @Test
    public void newHex() {
        List<ColorValue> values = scan("var c = new HexColor(\"#FF5733\");");
        assertEquals(1, values.size());
        assertEquals(new Color(0xFF, 0x57, 0x33), values.get(0).getColor());
    }

    @Test
    public void hexOfShortForm() {
        List<ColorValue> values = scan("var c = HexColor.of(\"#F0A\");");
        assertEquals(1, values.size());
        assertEquals(new Color(0xFF, 0x00, 0xAA), values.get(0).getColor());
    }

    @Test
    public void hexOfWithAlpha() {
        List<ColorValue> values = scan("var c = HexColor.of(\"#82CB32CC\");");
        assertEquals(1, values.size());
        Color c = values.get(0).getColor();
        assertEquals(0x82, c.getRed());
        assertEquals(0xCC, c.getAlpha());
    }

    @Test
    public void hslOf() {
        List<ColorValue> values = scan("var c = HslColor.of(\"hsl(0 100% 50%)\");");
        assertEquals(1, values.size());
        assertEquals(Color.RED, values.get(0).getColor());
    }

    @Test
    public void parseCss() {
        List<ColorValue> values = scan("var c = Color.parseCssColor(\"#00FF00\");");
        assertEquals(1, values.size());
        assertEquals(Color.GREEN, values.get(0).getColor());
    }

    @Test
    public void namedColor() {
        List<ColorValue> values = scan("Color c = NamedColor.TOMATO;");
        assertEquals(1, values.size());
        assertEquals(new Color(0xFF, 0x63, 0x47), values.get(0).getColor());
    }

    @Test
    public void multipleOnSingleLine() {
        List<ColorValue> values = scan("a(NamedColor.RED, NamedColor.BLUE);");
        assertEquals(2, values.size());
    }

    @Test
    public void noFalsePositiveOnUnrelatedCode() {
        // Should not match — different package/class entirely.
        assertEquals(Collections.emptyList(),
                scan("System.out.println(\"hello\");"));
    }

    @Test
    public void rgbThreeArgDoesNotDoubleMatchFourArg() {
        // Both regexes can technically match the 3-arg suffix of a 4-arg call;
        // we de-dup via overlapsExisting.
        List<ColorValue> values = scan("new RgbColor(0, 200, 80, 0.5);");
        assertEquals(1, values.size());
    }

    @Test
    public void offsetRangeCoversFullExpression() {
        String line = "Color c = NamedColor.TOMATO;";
        List<ColorValue> values = scan(line);
        ColorValue v = values.get(0);
        assertEquals("NamedColor.TOMATO", line.substring(v.getStartOffset(), v.getEndOffset()));
    }

    @Test
    public void formatterRoundTripsHexOf() {
        List<ColorValue> values = scan("HexColor.of(\"#FF5733\");");
        assertNotNull(values.get(0).getFormatter());
        String formatted = values.get(0).getFormatter().format(new Color(0x82, 0xCB, 0x32));
        assertEquals("HexColor.of(\"#82CB32\")", formatted);
    }

    @Test
    public void formatterRoundTripsNewRgb3() {
        List<ColorValue> values = scan("new RgbColor(255, 0, 0);");
        String formatted = values.get(0).getFormatter().format(new Color(10, 20, 30));
        assertEquals("new RgbColor(10, 20, 30)", formatted);
    }
}
