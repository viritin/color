package in.virit.color.intellij;

import com.intellij.util.ui.JBUI;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Small square swatch icon used in completion popups.
 */
final class ColorIcon implements Icon {

    private final Color color;
    private final int size;

    ColorIcon(Color color) {
        this.color = color;
        this.size = JBUI.scale(12);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRect(x, y, size, size);
            g2.setColor(new Color(0, 0, 0, 80));
            g2.drawRect(x, y, size - 1, size - 1);
        } finally {
            g2.dispose();
        }
    }

    @Override public int getIconWidth() { return size; }
    @Override public int getIconHeight() { return size; }
}
