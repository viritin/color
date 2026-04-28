package sample;

import in.virit.color.Color;
import in.virit.color.HexColor;
import in.virit.color.HslColor;
import in.virit.color.NamedColor;
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
