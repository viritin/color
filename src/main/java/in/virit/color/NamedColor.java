package in.virit.color;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents a named color with its RGBA value.
 * This class is immutable and thread-safe. Instead of using this class directly,
 * you are encouraged to use the static instances from {@link CssColor} interface.
 * <p>
 *     The list is taken from MDN.
 * </p>
 */
public enum NamedColor implements CssColor {

    /** Black color (#000000). */
    BLACK("#000000"),
    /** Silver color (#C0C0C0). */
    SILVER("#C0C0C0"),
    /** Gray color (#808080). */
    GRAY("#808080"),
    /** White color (#FFFFFF). */
    WHITE("#FFFFFF"),
    /** Maroon color (#800000). */
    MAROON("#800000"),
    /** Red color (#FF0000). */
    RED("#FF0000"),
    /** Purple color (#800080). */
    PURPLE("#800080"),
    /** Fuchsia color (#FF00FF). */
    FUCHSIA("#FF00FF"),
    /** Green color (#008000). */
    GREEN("#008000"),
    /** Lime color (#00FF00). */
    LIME("#00FF00"),
    /** Olive color (#808000). */
    OLIVE("#808000"),
    /** Yellow color (#FFFF00). */
    YELLOW("#FFFF00"),
    /** Navy color (#000080). */
    NAVY("#000080"),
    /** Blue color (#0000FF). */
    BLUE("#0000FF"),
    /** Teal color (#008080). */
    TEAL("#008080"),
    /** Aqua color (#00FFFF). */
    AQUA("#00FFFF"),
    /** Aliceblue color (#F0F8FF). */
    ALICEBLUE("#F0F8FF"),
    /** Antiquewhite color (#FAEBD7). */
    ANTIQUEWHITE("#FAEBD7"),
    /** Aquamarine color (#7FFFD4). */
    AQUAMARINE("#7FFFD4"),
    /** Azure color (#F0FFFF). */
    AZURE("#F0FFFF"),
    /** Beige color (#F5F5DC). */
    BEIGE("#F5F5DC"),
    /** Bisque color (#FFE4C4). */
    BISQUE("#FFE4C4"),
    /** Blanchedalmond color (#FFEBCD). */
    BLANCHEDALMOND("#FFEBCD"),
    /** Blueviolet color (#8A2BE2). */
    BLUEVIOLET("#8A2BE2"),
    /** Brown color (#A52A2A). */
    BROWN("#A52A2A"),
    /** Burlywood color (#DEB887). */
    BURLYWOOD("#DEB887"),
    /** Cadetblue color (#5F9EA0). */
    CADETBLUE("#5F9EA0"),
    /** Chartreuse color (#7FFF00). */
    CHARTREUSE("#7FFF00"),
    /** Chocolate color (#D2691E). */
    CHOCOLATE("#D2691E"),
    /** Coral color (#FF7F50). */
    CORAL("#FF7F50"),
    /** Cornflowerblue color (#6495ED). */
    CORNFLOWERBLUE("#6495ED"),
    /** Cornsilk color (#FFF8DC). */
    CORNSILK("#FFF8DC"),
    /** Crimson color (#DC143C). */
    CRIMSON("#DC143C"),
    /** Cyan color (#00FFFF). */
    CYAN("#00FFFF"),
    /** Darkblue color (#00008B). */
    DARKBLUE("#00008B"),
    /** Darkcyan color (#008B8B). */
    DARKCYAN("#008B8B"),
    /** Darkgoldenrod color (#B8860B). */
    DARKGOLDENROD("#B8860B"),
    /** Darkgray color (#A9A9A9). */
    DARKGRAY("#A9A9A9"),
    /** Darkgreen color (#006400). */
    DARKGREEN("#006400"),
    /** Darkgrey color (#A9A9A9). */
    DARKGREY("#A9A9A9"),
    /** Darkkhaki color (#BDB76B). */
    DARKKHAKI("#BDB76B"),
    /** Darkmagenta color (#8B008B). */
    DARKMAGENTA("#8B008B"),
    /** Darkolivegreen color (#556B2F). */
    DARKOLIVEGREEN("#556B2F"),
    /** Darkorange color (#FF8C00). */
    DARKORANGE("#FF8C00"),
    /** Darkorchid color (#9932CC). */
    DARKORCHID("#9932CC"),
    /** Darkred color (#8B0000). */
    DARKRED("#8B0000"),
    /** Darksalmon color (#E9967A). */
    DARKSALMON("#E9967A"),
    /** Darkseagreen color (#8FBC8F). */
    DARKSEAGREEN("#8FBC8F"),
    /** Darkslateblue color (#483D8B). */
    DARKSLATEBLUE("#483D8B"),
    /** Darkslategray color (#2F4F4F). */
    DARKSLATEGRAY("#2F4F4F"),
    /** Darkslategrey color (#2F4F4F). */
    DARKSLATEGREY("#2F4F4F"),
    /** Darkturquoise color (#00CED1). */
    DARKTURQUOISE("#00CED1"),
    /** Darkviolet color (#9400D3). */
    DARKVIOLET("#9400D3"),
    /** Deeppink color (#FF1493). */
    DEEPPINK("#FF1493"),
    /** Deepskyblue color (#00BFFF). */
    DEEPSKYBLUE("#00BFFF"),
    /** Dimgray color (#696969). */
    DIMGRAY("#696969"),
    /** Dimgrey color (#696969). */
    DIMGREY("#696969"),
    /** Dodgerblue color (#1E90FF). */
    DODGERBLUE("#1E90FF"),
    /** Firebrick color (#B22222). */
    FIREBRICK("#B22222"),
    /** Floralwhite color (#FFFAF0). */
    FLORALWHITE("#FFFAF0"),
    /** Forestgreen color (#228B22). */
    FORESTGREEN("#228B22"),
    /** Gainsboro color (#DCDCDC). */
    GAINSBORO("#DCDCDC"),
    /** Ghostwhite color (#F8F8FF). */
    GHOSTWHITE("#F8F8FF"),
    /** Gold color (#FFD700). */
    GOLD("#FFD700"),
    /** Goldenrod color (#DAA520). */
    GOLDENROD("#DAA520"),
    /** Greenyellow color (#ADFF2F). */
    GREENYELLOW("#ADFF2F"),
    /** Grey color (#808080). */
    GREY("#808080"),
    /** Honeydew color (#F0FFF0). */
    HONEYDEW("#F0FFF0"),
    /** Hotpink color (#FF69B4). */
    HOTPINK("#FF69B4"),
    /** Indianred color (#CD5C5C). */
    INDIANRED("#CD5C5C"),
    /** Indigo color (#4B0082). */
    INDIGO("#4B0082"),
    /** Ivory color (#FFFFF0). */
    IVORY("#FFFFF0"),
    /** Khaki color (#F0E68C). */
    KHAKI("#F0E68C"),
    /** Lavender color (#E6E6FA). */
    LAVENDER("#E6E6FA"),
    /** Lavenderblush color (#FFF0F5). */
    LAVENDERBLUSH("#FFF0F5"),
    /** Lawngreen color (#7CFC00). */
    LAWNGREEN("#7CFC00"),
    /** Lemonchiffon color (#FFFACD). */
    LEMONCHIFFON("#FFFACD"),
    /** Lightblue color (#ADD8E6). */
    LIGHTBLUE("#ADD8E6"),
    /** Lightcoral color (#F08080). */
    LIGHTCORAL("#F08080"),
    /** Lightcyan color (#E0FFFF). */
    LIGHTCYAN("#E0FFFF"),
    /** Lightgoldenrodyellow color (#FAFAD2). */
    LIGHTGOLDENRODYELLOW("#FAFAD2"),
    /** Lightgray color (#D3D3D3). */
    LIGHTGRAY("#D3D3D3"),
    /** Lightgreen color (#90EE90). */
    LIGHTGREEN("#90EE90"),
    /** Lightgrey color (#D3D3D3). */
    LIGHTGREY("#D3D3D3"),
    /** Lightpink color (#FFB6C1). */
    LIGHTPINK("#FFB6C1"),
    /** Lightsalmon color (#FFA07A). */
    LIGHTSALMON("#FFA07A"),
    /** Lightseagreen color (#20B2AA). */
    LIGHTSEAGREEN("#20B2AA"),
    /** Lightskyblue color (#87CEFA). */
    LIGHTSKYBLUE("#87CEFA"),
    /** Lightslategray color (#778899). */
    LIGHTSLATEGRAY("#778899"),
    /** Lightslategrey color (#778899). */
    LIGHTSLATEGREY("#778899"),
    /** Lightsteelblue color (#B0C4DE). */
    LIGHTSTEELBLUE("#B0C4DE"),
    /** Lightyellow color (#FFFFE0). */
    LIGHTYELLOW("#FFFFE0"),
    /** Limegreen color (#32CD32). */
    LIMEGREEN("#32CD32"),
    /** Linen color (#FAF0E6). */
    LINEN("#FAF0E6"),
    /** Magenta color (#FF00FF). */
    MAGENTA("#FF00FF"),
    /** Mediumaquamarine color (#66CDAA). */
    MEDIUMAQUAMARINE("#66CDAA"),
    /** Mediumblue color (#0000CD). */
    MEDIUMBLUE("#0000CD"),
    /** Mediumorchid color (#BA55D3). */
    MEDIUMORCHID("#BA55D3"),
    /** Mediumpurple color (#9370DB). */
    MEDIUMPURPLE("#9370DB"),
    /** Mediumseagreen color (#3CB371). */
    MEDIUMSEAGREEN("#3CB371"),
    /** Mediumslateblue color (#7B68EE). */
    MEDIUMSLATEBLUE("#7B68EE"),
    /** Mediumspringgreen color (#00FA9A). */
    MEDIUMSPRINGGREEN("#00FA9A"),
    /** Mediumturquoise color (#48D1CC). */
    MEDIUMTURQUOISE("#48D1CC"),
    /** Mediumvioletred color (#C71585). */
    MEDIUMVIOLETRED("#C71585"),
    /** Midnightblue color (#191970). */
    MIDNIGHTBLUE("#191970"),
    /** Mintcream color (#F5FFFA). */
    MINTCREAM("#F5FFFA"),
    /** Mistyrose color (#FFE4E1). */
    MISTYROSE("#FFE4E1"),
    /** Moccasin color (#FFE4B5). */
    MOCCASIN("#FFE4B5"),
    /** Navajowhite color (#FFDEAD). */
    NAVAJOWHITE("#FFDEAD"),
    /** Oldlace color (#FDF5E6). */
    OLDLACE("#FDF5E6"),
    /** Olivedrab color (#6B8E23). */
    OLIVEDRAB("#6B8E23"),
    /** Orange color (#FFA500). */
    ORANGE("#FFA500"),
    /** Orangered color (#FF4500). */
    ORANGERED("#FF4500"),
    /** Orchid color (#DA70D6). */
    ORCHID("#DA70D6"),
    /** Palegoldenrod color (#EEE8AA). */
    PALEGOLDENROD("#EEE8AA"),
    /** Palegreen color (#98FB98). */
    PALEGREEN("#98FB98"),
    /** Paleturquoise color (#AFEEEE). */
    PALETURQUOISE("#AFEEEE"),
    /** Palevioletred color (#DB7093). */
    PALEVIOLETRED("#DB7093"),
    /** Papayawhip color (#FFEFD5). */
    PAPAYAWHIP("#FFEFD5"),
    /** Peachpuff color (#FFDAB9). */
    PEACHPUFF("#FFDAB9"),
    /** Peru color (#CD853F). */
    PERU("#CD853F"),
    /** Pink color (#FFC0CB). */
    PINK("#FFC0CB"),
    /** Plum color (#DDA0DD). */
    PLUM("#DDA0DD"),
    /** Powderblue color (#B0E0E6). */
    POWDERBLUE("#B0E0E6"),
    /** Rebeccapurple color (#663399). */
    REBECCAPURPLE("#663399"),
    /** Rosybrown color (#BC8F8F). */
    ROSYBROWN("#BC8F8F"),
    /** Royalblue color (#4169E1). */
    ROYALBLUE("#4169E1"),
    /** Saddlebrown color (#8B4513). */
    SADDLEBROWN("#8B4513"),
    /** Salmon color (#FA8072). */
    SALMON("#FA8072"),
    /** Sandybrown color (#F4A460). */
    SANDYBROWN("#F4A460"),
    /** Seagreen color (#2E8B57). */
    SEAGREEN("#2E8B57"),
    /** Seashell color (#FFF5EE). */
    SEASHELL("#FFF5EE"),
    /** Sienna color (#A0522D). */
    SIENNA("#A0522D"),
    /** Skyblue color (#87CEEB). */
    SKYBLUE("#87CEEB"),
    /** Slateblue color (#6A5ACD). */
    SLATEBLUE("#6A5ACD"),
    /** Slategray color (#708090). */
    SLATEGRAY("#708090"),
    /** Slategrey color (#708090). */
    SLATEGREY("#708090"),
    /** Snow color (#FFFAFA). */
    SNOW("#FFFAFA"),
    /** Springgreen color (#00FF7F). */
    SPRINGGREEN("#00FF7F"),
    /** Steelblue color (#4682B4). */
    STEELBLUE("#4682B4"),
    /** Tan color (#D2B48C). */
    TAN("#D2B48C"),
    /** Thistle color (#D8BFD8). */
    THISTLE("#D8BFD8"),
    /** Tomato color (#FF6347). */
    TOMATO("#FF6347"),
    /** Turquoise color (#40E0D0). */
    TURQUOISE("#40E0D0"),
    /** Violet color (#EE82EE). */
    VIOLET("#EE82EE"),
    /** Wheat color (#F5DEB3). */
    WHEAT("#F5DEB3"),
    /** Whitesmoke color (#F5F5F5). */
    WHITESMOKE("#F5F5F5"),
    /** Yellowgreen color (#9ACD32). */
    YELLOWGREEN("#9ACD32");

    private final RgbColor rgbaValue;

    NamedColor(String hex) {
        this.rgbaValue = HexColor.of(hex).toRgbColor();
    }

    @Override
    @JsonValue
    public String toString() {
        return name().toLowerCase();
    }

    @Override
    public RgbColor toRgbColor() {
        return rgbaValue;
    }

    /**
     * Finds a named color by its name. Css color names are lowercase.
     *
     * @param cssColorString the CSS color string to parse
     * @return a NamedColor object representing the parsed color
     */
    public static NamedColor of(String cssColorString) {
        // Named color
        for (NamedColor nc : NamedColor.values()) {
            if (nc.name().toLowerCase().equals(cssColorString)) {
                return nc;
            }
        }
        throw new IllegalArgumentException("Invalid named color: " + cssColorString);
    }

}
