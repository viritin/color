package in.virit.color.internal;

/**
 * Internal math utilities for CSS Color 4 conversions: gamma functions,
 * matrix multiplication, and the per-space transforms to linear sRGB.
 *
 * <p>Matrices and constants follow the CSS Color Module Level 4 spec.
 * All matrices operate on column vectors: {@code result = M * input}.
 *
 * <p>Out-of-gamut sRGB values are returned as-is — callers clamp via
 * {@link #clampToByte(double)} when producing 0..255 RGB output.
 */
public final class ColorMath {

    private ColorMath() {
    }

    // ---------- sRGB gamma ----------

    public static double linearSrgbToSrgb(double v) {
        double sign = v < 0 ? -1 : 1;
        double abs = Math.abs(v);
        if (abs <= 0.0031308) {
            return 12.92 * v;
        }
        return sign * (1.055 * Math.pow(abs, 1.0 / 2.4) - 0.055);
    }

    public static double srgbToLinearSrgb(double v) {
        double sign = v < 0 ? -1 : 1;
        double abs = Math.abs(v);
        if (abs <= 0.04045) {
            return v / 12.92;
        }
        return sign * Math.pow((abs + 0.055) / 1.055, 2.4);
    }

    // ---------- Matrix helpers ----------

    private static double[] mul(double[][] m, double a, double b, double c) {
        return new double[] {
                m[0][0] * a + m[0][1] * b + m[0][2] * c,
                m[1][0] * a + m[1][1] * b + m[1][2] * c,
                m[2][0] * a + m[2][1] * b + m[2][2] * c
        };
    }

    // ---------- CIELAB / LCh ----------

    // D50 white point (CSS Color 4 reference)
    private static final double D50_X = 0.96422;
    private static final double D50_Y = 1.00000;
    private static final double D50_Z = 0.82521;

    private static final double LAB_K = 24389.0 / 27.0;
    private static final double LAB_E = 216.0 / 24389.0;

    public static double[] labToXyzD50(double L, double a, double b) {
        double f1 = (L + 16) / 116;
        double f0 = a / 500 + f1;
        double f2 = f1 - b / 200;

        double f0c = f0 * f0 * f0;
        double f2c = f2 * f2 * f2;

        double x = (f0c > LAB_E) ? f0c : (116 * f0 - 16) / LAB_K;
        double y = (L > LAB_K * LAB_E) ? Math.pow((L + 16) / 116, 3) : L / LAB_K;
        double z = (f2c > LAB_E) ? f2c : (116 * f2 - 16) / LAB_K;

        return new double[] { x * D50_X, y * D50_Y, z * D50_Z };
    }

    // Bradford D50 → D65 adaptation (CSS Color 4 spec)
    private static final double[][] BRADFORD_D50_TO_D65 = {
            { 0.9554734527042182,  -0.023098536874261423, 0.0632593086610217 },
            { -0.028369706963208136, 1.0099954580058226,  0.021041398966943008 },
            { 0.012314001688319899, -0.020507696433477912, 1.3303659366080753 }
    };

    public static double[] xyzD50ToXyzD65(double x, double y, double z) {
        return mul(BRADFORD_D50_TO_D65, x, y, z);
    }

    // XYZ-D65 → linear sRGB (CSS Color 4 spec)
    private static final double[][] XYZ_D65_TO_LIN_SRGB = {
            {  3.2409699419045226,  -1.5373831775700939,  -0.4986107602930034 },
            { -0.9692436362808796,   1.8759675015077202,   0.04155505740717561 },
            {  0.05563007969699366, -0.20397695888897652,  1.0569715142428786 }
    };

    public static double[] xyzD65ToLinearSrgb(double x, double y, double z) {
        return mul(XYZ_D65_TO_LIN_SRGB, x, y, z);
    }

    // ---------- OKLab ----------

    public static double[] oklabToLinearSrgb(double L, double a, double b) {
        double l_ = L + 0.3963377774 * a + 0.2158037573 * b;
        double m_ = L - 0.1055613458 * a - 0.0638541728 * b;
        double s_ = L - 0.0894841775 * a - 1.2914855480 * b;

        double l = l_ * l_ * l_;
        double m = m_ * m_ * m_;
        double s = s_ * s_ * s_;

        double r =  4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s;
        double g = -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s;
        double bb = -0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s;

        return new double[] { r, g, bb };
    }

    // ---------- Wide-gamut RGB ----------

    // Linear Display P3 (D65) → XYZ-D65 (CSS Color 4 spec)
    private static final double[][] LIN_P3_TO_XYZ_D65 = {
            { 0.4865709486482162, 0.26566769316909306, 0.1982172852343625 },
            { 0.2289745640697488, 0.6917385218365064,  0.079286914093745 },
            { 0.0,                0.04511338185890264, 1.043944368900976 }
    };

    public static double[] displayP3ToLinearSrgb(double r, double g, double b) {
        // Display P3 uses sRGB transfer function
        double lr = srgbToLinearSrgb(r);
        double lg = srgbToLinearSrgb(g);
        double lb = srgbToLinearSrgb(b);
        double[] xyz = mul(LIN_P3_TO_XYZ_D65, lr, lg, lb);
        return xyzD65ToLinearSrgb(xyz[0], xyz[1], xyz[2]);
    }

    // Linear A98 RGB (D65) → XYZ-D65
    private static final double[][] LIN_A98_TO_XYZ_D65 = {
            { 0.5766690429101305,  0.1855582379065463,  0.1882286462349947 },
            { 0.29734497525053605, 0.6273635662554661,  0.07529145849399788 },
            { 0.02703136138502536, 0.07068885253582723, 0.9913375368376388 }
    };

    public static double[] a98RgbToLinearSrgb(double r, double g, double b) {
        // Adobe RGB transfer: simple gamma 2.19921875
        double exp = 563.0 / 256.0;
        double lr = Math.signum(r) * Math.pow(Math.abs(r), exp);
        double lg = Math.signum(g) * Math.pow(Math.abs(g), exp);
        double lb = Math.signum(b) * Math.pow(Math.abs(b), exp);
        double[] xyz = mul(LIN_A98_TO_XYZ_D65, lr, lg, lb);
        return xyzD65ToLinearSrgb(xyz[0], xyz[1], xyz[2]);
    }

    // Linear Rec.2020 (D65) → XYZ-D65
    private static final double[][] LIN_REC2020_TO_XYZ_D65 = {
            { 0.6369580483012914, 0.14461690358620832, 0.1688809751641721 },
            { 0.2627002120112671, 0.6779980715188708,  0.05930171646986196 },
            { 0.000000000000000,  0.028072693049087428, 1.060985057710791 }
    };

    public static double[] rec2020ToLinearSrgb(double r, double g, double b) {
        // Rec.2020 transfer function (BT.2020)
        final double alpha = 1.09929682680944;
        final double beta = 0.018053968510807;
        double lr = rec2020Linearize(r, alpha, beta);
        double lg = rec2020Linearize(g, alpha, beta);
        double lb = rec2020Linearize(b, alpha, beta);
        double[] xyz = mul(LIN_REC2020_TO_XYZ_D65, lr, lg, lb);
        return xyzD65ToLinearSrgb(xyz[0], xyz[1], xyz[2]);
    }

    private static double rec2020Linearize(double v, double alpha, double beta) {
        double sign = v < 0 ? -1 : 1;
        double abs = Math.abs(v);
        if (abs < beta * 4.5) {
            return v / 4.5;
        }
        return sign * Math.pow((abs + alpha - 1) / alpha, 1.0 / 0.45);
    }

    // Linear ProPhoto RGB (D50) → XYZ-D50
    private static final double[][] LIN_PROPHOTO_TO_XYZ_D50 = {
            { 0.7977666449006423,  0.13518129740053308, 0.0313477341283922 },
            { 0.2880748288194013,  0.7118352342418731,  0.00008993693872564,  },
            { 0.0,                 0.0,                 0.8251046025104602 }
    };

    public static double[] prophotoRgbToLinearSrgb(double r, double g, double b) {
        // ProPhoto: gamma 1.8 with small linear segment near zero
        double lr = prophotoLinearize(r);
        double lg = prophotoLinearize(g);
        double lb = prophotoLinearize(b);
        double[] xyzD50 = mul(LIN_PROPHOTO_TO_XYZ_D50, lr, lg, lb);
        double[] xyzD65 = xyzD50ToXyzD65(xyzD50[0], xyzD50[1], xyzD50[2]);
        return xyzD65ToLinearSrgb(xyzD65[0], xyzD65[1], xyzD65[2]);
    }

    private static double prophotoLinearize(double v) {
        double sign = v < 0 ? -1 : 1;
        double abs = Math.abs(v);
        if (abs <= 16.0 / 512.0) {
            return v / 16.0;
        }
        return sign * Math.pow(abs, 1.8);
    }

    // ---------- Output helpers ----------

    public static int clampToByte(double v) {
        long r = Math.round(v * 255);
        if (r < 0) return 0;
        if (r > 255) return 255;
        return (int) r;
    }

    public static double clampAlpha(double a) {
        if (a < 0) return 0;
        if (a > 1) return 1;
        return a;
    }

    // ---------- CSS parsing helpers ----------

    /**
     * Parses a CSS angle (deg, rad, grad, turn, or bare number) to degrees.
     * The {@code none} keyword is treated as 0.
     */
    public static double parseAngle(String s) {
        s = s.trim();
        if ("none".equalsIgnoreCase(s)) return 0;
        if (s.endsWith("deg")) {
            return Double.parseDouble(s.substring(0, s.length() - 3));
        }
        if (s.endsWith("grad")) {
            return Double.parseDouble(s.substring(0, s.length() - 4)) * 0.9;
        }
        if (s.endsWith("rad")) {
            return Math.toDegrees(Double.parseDouble(s.substring(0, s.length() - 3)));
        }
        if (s.endsWith("turn")) {
            return Double.parseDouble(s.substring(0, s.length() - 4)) * 360.0;
        }
        return Double.parseDouble(s);
    }

    /**
     * Parses a CSS number or percentage. For a percentage, returns
     * {@code (pct/100) * percentMax}. For a bare number, returns the number as-is.
     * The {@code none} keyword is treated as 0.
     */
    public static double parsePercentOrNumber(String s, double percentMax) {
        s = s.trim();
        if ("none".equalsIgnoreCase(s)) return 0;
        if (s.endsWith("%")) {
            return Double.parseDouble(s.substring(0, s.length() - 1)) / 100.0 * percentMax;
        }
        return Double.parseDouble(s);
    }

    /**
     * Parses an alpha value: percentage (e.g. {@code 50%}) or a number 0..1.
     */
    public static double parseAlpha(String s) {
        s = s.trim();
        if ("none".equalsIgnoreCase(s)) return 0;
        if (s.endsWith("%")) {
            return Double.parseDouble(s.substring(0, s.length() - 1)) / 100.0;
        }
        return Double.parseDouble(s);
    }

    /**
     * Strips a CSS function wrapper {@code "name(...)"} and returns whitespace-separated
     * components. Commas are normalised to spaces (legacy comma form) and the {@code /}
     * alpha separator is converted to whitespace so the alpha appears as a trailing token.
     */
    public static String[] splitComponents(String css) {
        int open = css.indexOf('(');
        int close = css.lastIndexOf(')');
        if (open < 0 || close < 0 || close < open) {
            throw new IllegalArgumentException("Invalid CSS color function: " + css);
        }
        String inner = css.substring(open + 1, close).trim();
        inner = inner.replace(",", " ");
        inner = inner.replace("/", " ");
        return inner.trim().split("\\s+");
    }
}
