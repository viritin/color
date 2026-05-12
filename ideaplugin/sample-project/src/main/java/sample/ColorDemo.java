package sample;

import in.virit.color.Color;
import in.virit.color.ColorFunction;
import in.virit.color.ColorSpace;
import in.virit.color.HexColor;
import in.virit.color.HslColor;
import in.virit.color.HwbColor;
import in.virit.color.LabColor;
import in.virit.color.LchColor;
import in.virit.color.NamedColor;
import in.virit.color.OklabColor;
import in.virit.color.OklchColor;
import in.virit.color.RgbColor;

/**
 * Sandbox for the Viritin Color IntelliJ plugin.
 * Each line below should show a color swatch in the gutter; clicking opens the IDE color picker.
 */
public class ColorDemo {

    void rgb() {
        Color a = new RgbColor(0, 166, 255);
        Color b = new RgbColor(239, 19, 199, 0.50);
        Color c = RgbColor.of("rgb(0 0 255)");
        Color d = RgbColor.of("rgba(128, 64, 32, 0.8)");
    }

    void hex() {
        Color a = new HexColor("#FF5733");
        Color b = HexColor.of("#82CB32");
        Color shortForm = HexColor.of("#F0A");
        Color withAlpha = HexColor.of("#82CB32CC");
    }

    void hsl() {
        Color a = new HslColor(120, 100, 50);
        Color b = new HslColor(220, 80, 60, 0.7);
        Color c = HslColor.of("hsl(240 100% 50%)");
        Color d = HslColor.of("hsla(0, 100%, 50%, 0.5)");
    }

    void parseAny() {
        Color hex = Color.parseCssColor("#FF00FF");
        Color rgb = Color.parseCssColor("rgb(10 20 30)");
        Color hsl = Color.parseCssColor("hsl(0 100% 50%)");
        Color hwb = Color.parseCssColor("hwb(120 30% 10%)");
        Color lab = Color.parseCssColor("lab(54.29 80.81 69.89)");
        Color oklch = Color.parseCssColor("oklch(0.628 0.258 29.234)");
        Color p3 = Color.parseCssColor("color(display-p3 1 0 0)");
    }

    void hwb() {
        Color a = new HwbColor(120, 30, 10);
        Color b = new HwbColor(0, 50, 0, 0.5);
        Color c = HwbColor.of("hwb(240 0% 0%)");
    }

    void lab() {
        Color white = new LabColor(100, 0, 0);
        Color red = new LabColor(54.29, 80.81, 69.89);
        Color withAlpha = new LabColor(50, 20, -30, 0.5);
        Color parsed = LabColor.of("lab(50% 20 -30)");
    }

    void lch() {
        Color a = new LchColor(54.29, 106.84, 40.86);
        Color b = LchColor.of("lch(50 30 120deg)");
    }

    void oklab() {
        Color a = new OklabColor(0.628, 0.225, 0.126);
        Color b = OklabColor.of("oklab(0.5 0.1 -0.1 / 0.5)");
    }

    void oklch() {
        Color red = new OklchColor(0.628, 0.258, 29.234);
        Color withAlpha = new OklchColor(0.5, 0.2, 120, 0.7);
        Color parsed = OklchColor.of("oklch(0.628 0.258 29.234)");
    }

    void colorFunction() {
        Color srgb = new ColorFunction(ColorSpace.SRGB, 1, 0, 0);
        Color p3 = new ColorFunction(ColorSpace.DISPLAY_P3, 1, 0, 0);
        Color rec2020 = new ColorFunction(ColorSpace.REC2020, 0, 1, 0);
        Color parsed = ColorFunction.of("color(display-p3 1 0 0 / 0.5)");
    }

    void named() {
        Color red = NamedColor.RED;
        Color sea = NamedColor.SEAGREEN;
        Color tomato = NamedColor.TOMATO;
    }

    // Position your caret on a blank line below, type "Nam" — completion should
    // offer NamedColor.<NAME> entries with color swatch icons.
    Color tryCompletionHere() {
        return NamedColor.PLUM;
    }
}
