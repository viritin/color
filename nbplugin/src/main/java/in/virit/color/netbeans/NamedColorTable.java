package in.virit.color.netbeans;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Static lookup of CSS named colors mirroring {@code in.virit.color.NamedColor}.
 * Keep entries in sync with NamedColor.java (use the enum constant name as the key).
 */
final class NamedColorTable {

    private static final Map<String, String> HEX_BY_NAME = new LinkedHashMap<>();

    static {
        Map<String, String> m = HEX_BY_NAME;
        m.put("BLACK", "#000000");
        m.put("SILVER", "#C0C0C0");
        m.put("GRAY", "#808080");
        m.put("WHITE", "#FFFFFF");
        m.put("MAROON", "#800000");
        m.put("RED", "#FF0000");
        m.put("PURPLE", "#800080");
        m.put("FUCHSIA", "#FF00FF");
        m.put("GREEN", "#008000");
        m.put("LIME", "#00FF00");
        m.put("OLIVE", "#808000");
        m.put("YELLOW", "#FFFF00");
        m.put("NAVY", "#000080");
        m.put("BLUE", "#0000FF");
        m.put("TEAL", "#008080");
        m.put("AQUA", "#00FFFF");
        m.put("ALICEBLUE", "#F0F8FF");
        m.put("ANTIQUEWHITE", "#FAEBD7");
        m.put("AQUAMARINE", "#7FFFD4");
        m.put("AZURE", "#F0FFFF");
        m.put("BEIGE", "#F5F5DC");
        m.put("BISQUE", "#FFE4C4");
        m.put("BLANCHEDALMOND", "#FFEBCD");
        m.put("BLUEVIOLET", "#8A2BE2");
        m.put("BROWN", "#A52A2A");
        m.put("BURLYWOOD", "#DEB887");
        m.put("CADETBLUE", "#5F9EA0");
        m.put("CHARTREUSE", "#7FFF00");
        m.put("CHOCOLATE", "#D2691E");
        m.put("CORAL", "#FF7F50");
        m.put("CORNFLOWERBLUE", "#6495ED");
        m.put("CORNSILK", "#FFF8DC");
        m.put("CRIMSON", "#DC143C");
        m.put("CYAN", "#00FFFF");
        m.put("DARKBLUE", "#00008B");
        m.put("DARKCYAN", "#008B8B");
        m.put("DARKGOLDENROD", "#B8860B");
        m.put("DARKGRAY", "#A9A9A9");
        m.put("DARKGREEN", "#006400");
        m.put("DARKGREY", "#A9A9A9");
        m.put("DARKKHAKI", "#BDB76B");
        m.put("DARKMAGENTA", "#8B008B");
        m.put("DARKOLIVEGREEN", "#556B2F");
        m.put("DARKORANGE", "#FF8C00");
        m.put("DARKORCHID", "#9932CC");
        m.put("DARKRED", "#8B0000");
        m.put("DARKSALMON", "#E9967A");
        m.put("DARKSEAGREEN", "#8FBC8F");
        m.put("DARKSLATEBLUE", "#483D8B");
        m.put("DARKSLATEGRAY", "#2F4F4F");
        m.put("DARKSLATEGREY", "#2F4F4F");
        m.put("DARKTURQUOISE", "#00CED1");
        m.put("DARKVIOLET", "#9400D3");
        m.put("DEEPPINK", "#FF1493");
        m.put("DEEPSKYBLUE", "#00BFFF");
        m.put("DIMGRAY", "#696969");
        m.put("DIMGREY", "#696969");
        m.put("DODGERBLUE", "#1E90FF");
        m.put("FIREBRICK", "#B22222");
        m.put("FLORALWHITE", "#FFFAF0");
        m.put("FORESTGREEN", "#228B22");
        m.put("GAINSBORO", "#DCDCDC");
        m.put("GHOSTWHITE", "#F8F8FF");
        m.put("GOLD", "#FFD700");
        m.put("GOLDENROD", "#DAA520");
        m.put("GREENYELLOW", "#ADFF2F");
        m.put("GREY", "#808080");
        m.put("HONEYDEW", "#F0FFF0");
        m.put("HOTPINK", "#FF69B4");
        m.put("INDIANRED", "#CD5C5C");
        m.put("INDIGO", "#4B0082");
        m.put("IVORY", "#FFFFF0");
        m.put("KHAKI", "#F0E68C");
        m.put("LAVENDER", "#E6E6FA");
        m.put("LAVENDERBLUSH", "#FFF0F5");
        m.put("LAWNGREEN", "#7CFC00");
        m.put("LEMONCHIFFON", "#FFFACD");
        m.put("LIGHTBLUE", "#ADD8E6");
        m.put("LIGHTCORAL", "#F08080");
        m.put("LIGHTCYAN", "#E0FFFF");
        m.put("LIGHTGOLDENRODYELLOW", "#FAFAD2");
        m.put("LIGHTGRAY", "#D3D3D3");
        m.put("LIGHTGREEN", "#90EE90");
        m.put("LIGHTGREY", "#D3D3D3");
        m.put("LIGHTPINK", "#FFB6C1");
        m.put("LIGHTSALMON", "#FFA07A");
        m.put("LIGHTSEAGREEN", "#20B2AA");
        m.put("LIGHTSKYBLUE", "#87CEFA");
        m.put("LIGHTSLATEGRAY", "#778899");
        m.put("LIGHTSLATEGREY", "#778899");
        m.put("LIGHTSTEELBLUE", "#B0C4DE");
        m.put("LIGHTYELLOW", "#FFFFE0");
        m.put("LIMEGREEN", "#32CD32");
        m.put("LINEN", "#FAF0E6");
        m.put("MAGENTA", "#FF00FF");
        m.put("MEDIUMAQUAMARINE", "#66CDAA");
        m.put("MEDIUMBLUE", "#0000CD");
        m.put("MEDIUMORCHID", "#BA55D3");
        m.put("MEDIUMPURPLE", "#9370DB");
        m.put("MEDIUMSEAGREEN", "#3CB371");
        m.put("MEDIUMSLATEBLUE", "#7B68EE");
        m.put("MEDIUMSPRINGGREEN", "#00FA9A");
        m.put("MEDIUMTURQUOISE", "#48D1CC");
        m.put("MEDIUMVIOLETRED", "#C71585");
        m.put("MIDNIGHTBLUE", "#191970");
        m.put("MINTCREAM", "#F5FFFA");
        m.put("MISTYROSE", "#FFE4E1");
        m.put("MOCCASIN", "#FFE4B5");
        m.put("NAVAJOWHITE", "#FFDEAD");
        m.put("OLDLACE", "#FDF5E6");
        m.put("OLIVEDRAB", "#6B8E23");
        m.put("ORANGE", "#FFA500");
        m.put("ORANGERED", "#FF4500");
        m.put("ORCHID", "#DA70D6");
        m.put("PALEGOLDENROD", "#EEE8AA");
        m.put("PALEGREEN", "#98FB98");
        m.put("PALETURQUOISE", "#AFEEEE");
        m.put("PALEVIOLETRED", "#DB7093");
        m.put("PAPAYAWHIP", "#FFEFD5");
        m.put("PEACHPUFF", "#FFDAB9");
        m.put("PERU", "#CD853F");
        m.put("PINK", "#FFC0CB");
        m.put("PLUM", "#DDA0DD");
        m.put("POWDERBLUE", "#B0E0E6");
        m.put("REBECCAPURPLE", "#663399");
        m.put("ROSYBROWN", "#BC8F8F");
        m.put("ROYALBLUE", "#4169E1");
        m.put("SADDLEBROWN", "#8B4513");
        m.put("SALMON", "#FA8072");
        m.put("SANDYBROWN", "#F4A460");
        m.put("SEAGREEN", "#2E8B57");
        m.put("SEASHELL", "#FFF5EE");
        m.put("SIENNA", "#A0522D");
        m.put("SKYBLUE", "#87CEEB");
        m.put("SLATEBLUE", "#6A5ACD");
        m.put("SLATEGRAY", "#708090");
        m.put("SLATEGREY", "#708090");
        m.put("SNOW", "#FFFAFA");
        m.put("SPRINGGREEN", "#00FF7F");
        m.put("STEELBLUE", "#4682B4");
        m.put("TAN", "#D2B48C");
        m.put("THISTLE", "#D8BFD8");
        m.put("TOMATO", "#FF6347");
        m.put("TURQUOISE", "#40E0D0");
        m.put("VIOLET", "#EE82EE");
        m.put("WHEAT", "#F5DEB3");
        m.put("WHITESMOKE", "#F5F5F5");
        m.put("YELLOWGREEN", "#9ACD32");
    }

    static Color colorFor(String enumConstantName) {
        if (enumConstantName == null) return null;
        String hex = HEX_BY_NAME.get(enumConstantName);
        return hex == null ? null : HexUtil.parseHex(hex);
    }

    static Map<String, String> entries() {
        return HEX_BY_NAME;
    }

    private NamedColorTable() {}
}
