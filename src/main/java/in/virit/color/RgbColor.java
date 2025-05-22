package in.virit.color;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

/**
 * RGB color representation, with optional alpha channel.
 *
 * @param r Red (0-255)
 * @param g Green (0-255)
 * @param b Blue (0-255)
 * @param a Alpha (0-1), optional, defaults to 1
 */
public record RgbColor(int r, int g, int b, double a) implements CssColor {

    /**
     * Basic constructor for RgbColor.
     *
     * @param r Red (0-255)
     * @param g Green (0-255)
     * @param b Blue (0-255)
     * @param a Alpha (0-1)
     */
    public RgbColor {
        if(a < 0 || a > 1) {
            throw new IllegalArgumentException("Alpha value must be between 0 and 1");
        }
        if(r < 0 || r > 255) {
            throw new IllegalArgumentException("Red value must be between 0 and 255");
        }
        if(g < 0 || g > 255) {
            throw new IllegalArgumentException("Green value must be between 0 and 255");
        }
        if(b < 0 || b > 255) {
            throw new IllegalArgumentException("Blue value must be between 0 and 255");
        }
    }

    /**
     * Constructor without alpha value, defaults to 1.
     *
     * @param r Red (0-255)
     * @param g Green (0-255)
     * @param b Blue (0-255)
     */
    public RgbColor(int r, int g, int b) {
        this(r, g, b, 1);
    }

    @Override
    @JsonValue
    public String toString() {
        String alpha = a == 1.0 ? "" : String.format(Locale.US, " / %.2f", a);

        return "rgb(" + r + " " + g + " " + b + alpha + ")";
    }

    @Override
    public RgbColor toRgbColor() {
        return this;
    }

    /**
     * Converts this RGB color to HSL color representation.
     *
     * @return HSL color representation of this RGB color.
     */
    public HslColor toHslColor() {
        double rNorm = r / 255.0;
        double gNorm = g / 255.0;
        double bNorm = b / 255.0;

        double max = Math.max(rNorm, Math.max(gNorm, bNorm));
        double min = Math.min(rNorm, Math.min(gNorm, bNorm));
        double delta = max - min;

        double h = 0;
        if (delta == 0) {
            h = 0;
        } else if (max == rNorm) {
            h = 60 * (((gNorm - bNorm) / delta) % 6);
        } else if (max == gNorm) {
            h = 60 * (((bNorm - rNorm) / delta) + 2);
        } else if (max == bNorm) {
            h = 60 * (((rNorm - gNorm) / delta) + 4);
        }

        if (h < 0) {
            h += 360;
        }

        double l = (max + min) / 2;

        double s = 0;
        if (delta != 0) {
            s = delta / (1 - Math.abs(2 * l - 1));
        }

        int hRounded = (int) Math.round(h);
        int sPercent = (int) Math.round(s * 100);
        int lPercent = (int) Math.round(l * 100);

        return new HslColor(hRounded, sPercent, lPercent, a);
    }


    /**
     * Parses a CSS color string and returns a RgbColor object.
     * <p>
     *     Css variables and calculations are not supported.
     * </p>
     *
     * @param cssColorString the CSS color string to parse
     * @return a RgbColor object representing the parsed color
     */
    public static RgbColor of(String cssColorString) {
        // RGB color
        // remove rgb( or rgba( and
        cssColorString = cssColorString.replaceAll("rgba?\\(", "");

        // remove trailing )
        cssColorString = cssColorString.replaceAll("\\)", "");
        String[] parts;
        if(cssColorString.contains(",")) {
            parts = cssColorString.split(",");
        } else {
            // Handle the case where the color is in the modern format "rgb(r g b)"

            // remove potential / for alpha
            cssColorString = cssColorString.replaceAll(" /", "");
            parts = cssColorString.split(" ");
        }

        double alpha = 1.0; // default alpha value
        if(parts.length == 4) {
            // If there are 4 parts, the last one is the alpha value
            alpha = parseAlpha(parts[3].trim());
        }

        int r = parseInteger(parts[0].trim());
        int g = parseInteger(parts[1].trim());
        int b = parseInteger(parts[2].trim());
        return new RgbColor(r, g, b, alpha);    }

    /**
     * Creates a new RgbColor with the same RGB values but a different alpha value.
     *
     * @param newAlphaValue the new alpha value (0-1)
     * @return a new RgbColor with the same RGB values but a different alpha value
     */
    public RgbColor withAlpha(double newAlphaValue) {
        return new RgbColor(r, g, b, newAlphaValue);
    }

    static double parseAlpha(String value) {
        // Check if the value is a percentage
        if (value.endsWith("%")) {
            // Remove the percentage sign and parse as an integer
            return (Double.parseDouble(value.replace("%", "")) / 100.0);
        } else {
            // Parse as an integer directly
            return Double.parseDouble(value);
        }
    }

    static int parseInteger(String value) {
        // Check if the value is a percentage
        if (value.endsWith("%")) {
            // Remove the percentage sign and parse as an integer
            return (int) Math.round(Double.parseDouble(value.replace("%", "")) * 2.55);
        } else {
            // Parse as an integer directly
            return Integer.parseInt(value);
        }
    }

    /**
     * Converts this RGB color to a HexColor representation.
     *
     * @return HexColor representation of this RGB color.
     */
    public HexColor toHexColor() {
        String hex = String.format("#%02X%02X%02X", r, g, b);
        if (a < 1.0) {
            int alpha = (int) (a * 255);
            hex += String.format("%02X", alpha);
        }
        return new HexColor(hex);
    }

}
