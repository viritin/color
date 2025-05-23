package in.virit.color;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

import static in.virit.color.RgbColor.parseAlpha;

/**
 * HSL color representation.
 *
 * @param h Hue (0-360)
 * @param s Saturation (0-100)
 * @param l Lightness (0-100)
 * @param a Alpha (0-1), optional, defaults to 1
 */
public record HslColor(int h, int s, int l, double a) implements Color {

    /**
     * Constructor for HslColor.
     *
     * @param h Hue (0-360)
     * @param s Saturation (0-100)
     * @param l Lightness (0-100)
     * @param a Alpha (0-1), optional, defaults to 1
     */
    public HslColor {
        if (a < 0 || a > 1) {
            throw new IllegalArgumentException("Alpha value must be between 0 and 1");
        }
        if (h < 0 || h > 360) {
            throw new IllegalArgumentException("Hue value must be between 0 and 360");
        }
        if (s < 0 || s > 100) {
            throw new IllegalArgumentException("Saturation value must be between 0 and 100");
        }
        if (l < 0 || l > 100) {
            throw new IllegalArgumentException("Lightness value must be between 0 and 100");
        }
    }

    /**
     * Constructor without alpha value, defaults to 1.
     *
     * @param h Hue (0-360)
     * @param s Saturation (0-100)
     * @param l Lightness (0-100)
     */
    public HslColor(int h, int s, int l) {
        this(h, s, l, 1);
    }

    @Override
    @JsonValue
    public String toString() {
        String alpha = a < 1.0f ? String.format(Locale.US, " / %.2f", a) : "";
        return String.format(Locale.US, "hsl(%d %d %d%s)", Math.round(h), Math.round(s), Math.round(l), alpha);
    }

    @Override
    public RgbColor toRgbColor() {
        double sNorm = s / 100.0;
        double lNorm = l / 100.0;

        double c = (1 - Math.abs(2 * lNorm - 1)) * sNorm;
        double x = c * (1 - Math.abs((h / 60.0) % 2 - 1));
        double m = lNorm - c / 2;

        double rPrime = 0, gPrime = 0, bPrime = 0;

        if (0 <= h && h < 60) {
            rPrime = c;
            gPrime = x;
            bPrime = 0;
        } else if (60 <= h && h < 120) {
            rPrime = x;
            gPrime = c;
            bPrime = 0;
        } else if (120 <= h && h < 180) {
            rPrime = 0;
            gPrime = c;
            bPrime = x;
        } else if (180 <= h && h < 240) {
            rPrime = 0;
            gPrime = x;
            bPrime = c;
        } else if (240 <= h && h < 300) {
            rPrime = x;
            gPrime = 0;
            bPrime = c;
        } else if (300 <= h && h < 360) {
            rPrime = c;
            gPrime = 0;
            bPrime = x;
        }

        int r = (int) Math.round((rPrime + m) * 255);
        int g = (int) Math.round((gPrime + m) * 255);
        int b = (int) Math.round((bPrime + m) * 255);

        return new RgbColor(r, g, b, a);
    }

    /**
     * Creates a new HslColor with the same hue, saturation and luminance, but a different alpha value.
     *
     * @param newAlphaValue the new alpha value (0-1)
     * @return a new HslColor with the specified alpha value
     */
    public HslColor withAlpha(double newAlphaValue) {
        return new HslColor(h, s, l, newAlphaValue);
    }

    /**
     * Creates a new HslColor with the same hue, saturation and alpha, but a different luminance value.
     *
     * @param newLuminanceValue the new luminance value (0-100)
     * @return a new HslColor with the specified luminance value
     */
    public HslColor withLuminance(int newLuminanceValue) {
        return new HslColor(h, s, newLuminanceValue, a);
    }

    /**
     * Creates a new HslColor with the same hue, luminance and alpha, but a different saturation value.
     *
     * @param newSaturationValue the new saturation value (0-100)
     * @return a new HslColor with the specified saturation value
     */
    public HslColor withSaturation(int newSaturationValue) {
        return new HslColor(h, newSaturationValue, l, a);
    }

    /**
     * Creates a new HslColor with the same saturation, luminance and alpha, but a different hue value.
     *
     * @param newHueValue the new hue value (0-360)
     * @return a new HslColor with the specified hue value
     */
    public HslColor withHue(int newHueValue) {
        return new HslColor(newHueValue, s, l, a);
    }

    /**
     * Creates a darker variant of this color by reducing the luminance.
     *
     * @param absoluteAmount the absolute amount to reduce the luminance (0-100)
     * @return a new HslColor with the specified hue value
     */
    public HslColor darken(int absoluteAmount) {
        int newLuminance = clamp(l - absoluteAmount);
        return new HslColor(h, s, newLuminance, a);
    }

    /**
     * Creates a darker variant of this color by reducing the luminance.
     *
     * @param relativeAmount the relative amount to reduce the luminance (0-1)
     * @return a new HslColor with the specified hue value
     */
    public HslColor darken(double relativeAmount) {
        int newLuminance = clamp(l - (l * relativeAmount));
        return new HslColor(h, s, newLuminance, a);
    }

    /**
     * Creates a lighter variant of this color by increasing the luminance.
     *
     * @param absoluteAmount the absolute amount to increase the luminance (0-100)
     * @return a new HslColor with the specified hue value
     */
    public HslColor lighten(int absoluteAmount) {
        int newLuminance = clamp(l + absoluteAmount);
        return new HslColor(h, s, newLuminance, a);
    }

    /**
     * Creates a lighter variant of this color by increasing the luminance.
     *
     * @param relativeAmount the relative amount to increase the luminance (0-1)
     * @return a new HslColor with the specified hue value
     */
    public HslColor lighten(double relativeAmount) {
        int newLuminance = clamp(l + (l * relativeAmount));
        return new HslColor(h, s, newLuminance, a);
    }

    /**
     * Creates a more saturated variant of this color by increasing the saturation.
     *
     * @param absoluteAmount the absolute amount to increase the saturation (0-100)
     * @return a new HslColor with the specified hue value
     */
    public HslColor saturate(int absoluteAmount) {
        return new HslColor(h, clamp(s + absoluteAmount), l, a);
    }

    /**
     * Creates a more saturated variant of this color by increasing the saturation.
     *
     * @param relativeAmount the relative amount to increase the saturation (0-1)
     * @return a new HslColor with the specified hue value
     */
    public HslColor saturate(double relativeAmount) {
        int newSaturation = clamp(s + (s * relativeAmount));
        return new HslColor(h, newSaturation, l, a);
    }

    /**
     * Creates a variant of this color by shifting the hue.
     *
     * @param absoluteAmount the absolute amount to shift the hue (0-360)
     * @return a new HslColor with the specified hue value
     */
    public HslColor shift(int absoluteAmount) {
        int newHue = (h + absoluteAmount) % 360;
        if (newHue < 0) {
            newHue += 360;
        }
        return new HslColor(newHue, s, l, a);
    }

    /**
     * Creates a variant of this color by shifting the hue to the opposite side of the color wheel.
     *
     * @return a new HslColor with complemented hue
     */
    public HslColor complement() {
        int newHue = (h + 180) % 360;
        return new HslColor(newHue, s, l, a);
    }

    private int clamp(double doubleValue) {
        int value = (int) Math.round(doubleValue);
        if (value < 0) {
            return 0;
        } else if (value > 100) {
            return 100;
        }
        return value;
    }

    /**
     * Creates a new HslColor from a CSS color string.
     *
     * @param cssColorString the CSS color string (e.g. "hsl(120, 100%, 50%)")
     * @return a new HslColor object
     */
    public static HslColor of(String cssColorString) {
        // remove hsl( or hsla( and
        cssColorString = cssColorString.replaceAll("hsla?\\(", "");
        // remove trailing )
        cssColorString = cssColorString.replaceAll("\\)", "");
        // remove potential / for alpha
        cssColorString = cssColorString.replaceAll(" /", "");

        // Split the string by commas or spaces (the modern format)
        // and trim each part
        if(cssColorString.contains(",")) {
            cssColorString = cssColorString.replaceAll(",", " ");
        }
        String[] parts = cssColorString.split(" +");

        double alpha = 1.0; // default alpha value
        if(parts.length == 4) {
            // If there are 4 parts, the last one is the alpha value
            alpha = parseAlpha(parts[3].trim());
        }
        int h = angleToDegrees(parts[0].trim());
        int s = parsePercentage(parts[1].trim());
        int l = parsePercentage(parts[2].trim());
        return new HslColor(h, s, l, alpha);
    }

    private static int angleToDegrees(String cssNumberOrAngle) {
        // Check if the value is a percentage
        if (cssNumberOrAngle.endsWith("deg")) {
            // Remove the degree sign and parse as an integer
            return Integer.parseInt(cssNumberOrAngle.replace("deg", ""));
        } else if (cssNumberOrAngle.endsWith("rad")) {
            // Convert radians to degrees
            double radians = Double.parseDouble(cssNumberOrAngle.replace("rad", ""));
            return (int) Math.round(Math.toDegrees(radians));
        } else if (cssNumberOrAngle.endsWith("grad")) {
            // Convert gradians to degrees
            double gradians = Double.parseDouble(cssNumberOrAngle.replace("grad", ""));
            return (int) Math.round(gradians * 0.9);
        } else {
            // Parse as an integer directly
            return Integer.parseInt(cssNumberOrAngle);
        }
    }

    /**
     * MDN: A percentage or the keyword none (equivalent to 0% in this case). This value represents the color's saturation. Here 100% is completely saturated, while 0% is completely unsaturated (gray).
     * @param css the css string value
     * @return the parsed percentage value
     */
    private  static int parsePercentage(String css) {
        // Check if the value is a percentage
        if (css.endsWith("%")) {
            // Remove the percentage sign and parse as an integer
            return Integer.parseInt(css.replace("%", ""));
        } else {
            // Parse as an integer directly
            return Integer.parseInt(css);
        }
    }

}
