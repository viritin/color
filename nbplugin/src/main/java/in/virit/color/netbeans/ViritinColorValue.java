package in.virit.color.netbeans;

import com.junichi11.netbeans.modules.color.codes.preview.api.OffsetRange;
import com.junichi11.netbeans.modules.color.codes.preview.spi.AbstractColorValue;
import com.junichi11.netbeans.modules.color.codes.preview.spi.ColorCodeFormatter;

import java.awt.Color;

/**
 * A recognized Viritin Color expression in source. Carries the parsed
 * {@link Color} and the {@link Format} so the corresponding formatter can
 * rewrite the source in the same shape when the user picks a new color.
 */
final class ViritinColorValue extends AbstractColorValue {

    enum Format {
        NEW_RGB_3,
        NEW_RGB_4,
        RGB_OF,
        NEW_HEX,
        HEX_OF,
        NEW_HSL_3,
        NEW_HSL_4,
        HSL_OF,
        PARSE_CSS,
        NAMED_COLOR,
        /**
         * Any CSS Color 4 expression (new HwbColor(...), HwbColor.of("hwb(...)"),
         * new OklchColor(...), ColorFunction.of("color(display-p3 ...)"), …).
         * Picker rewrites collapse these to {@code new RgbColor(r, g, b[, a])}
         * since round-tripping arbitrary picker output into Lab/OkLCh/HWB is not
         * lossless without gamut mapping the user rarely expects in this flow.
         */
        CSS_COLOR_4
    }

    private final Color color;
    private final Format format;

    ViritinColorValue(String value, OffsetRange range, int line, Color color, Format format) {
        super(value, range, line);
        this.color = color;
        this.format = format;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public ColorCodeFormatter getFormatter() {
        return new ViritinColorFormatter(format);
    }
}
