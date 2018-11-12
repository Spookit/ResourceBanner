package thito.resourcebanner;

import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SwingUtil {


    static ArrayList<Font> FONTS = new ArrayList<>();

    public static BufferedImage convert(Component c, int w, int h) {
        w = Math.max(c.getWidth(), w);
        h = Math.max(c.getHeight(), h);
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.createGraphics();
        layoutComponent(c, g);
        g.dispose();
        return img;
    }

    public static BufferedImage convert(Component c) {
        int w = c.getWidth();
        int h = c.getHeight();
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.createGraphics();
        c.doLayout();
        c.paint(g);
        g.dispose();
        return img;
    }

    static void layoutComponent(Component c, Graphics i) {
        synchronized(c.getTreeLock()) {
            c.doLayout();
            c.paint(i);
            if(c instanceof Container) for(Component x : ((Container) c).getComponents()) {
                layoutComponent(x, i);
            }
        }
    }

    public static String registerFont(File file) throws Throwable {
        Font f;
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f = Font.createFont(Font.TRUETYPE_FONT, file));
        FONTS.add(f);
        return f.getName();
    }

    public static JPanel collect(List<BevelShape> imgs, int customizedWidth) {
        int width = 600;
        if(customizedWidth <= 0) {
            for(int i = 0; i < imgs.size(); i += 3) {
                int w = 0;
                for(int a = 0; a < 3 && a + i < imgs.size(); a++) {
                    BevelShape im = imgs.get(a + i);
                    w += im.getWidth();
                }
                width = Math.max(width, w);
            }
        } else {
            width = customizedWidth;
        }
        if(width > 1200) width = 1200;
        WrapLayout layout = new WrapLayout();
        JPanel panel = new JPanel(layout);
        panel.setBounds(0, 0, width, 100);
        panel.setOpaque(false);
        for(BevelShape i : imgs) {
            i.setPreferredSize(i.getSize());
            panel.add(i);
        }
        panel.setSize(layout.preferredLayoutSize(panel));
        return panel;
    }
}
