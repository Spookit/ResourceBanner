package thito.resourcebanner;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class SwingUtil {

  static List<Font> FONTS = new ArrayList<>();

  public static JPanel collect(List<RoundRectBkg> imgs, int customizedWidth) {
    int width = 600;
    if (customizedWidth <= 0) {
      for (int i = 0; i < imgs.size(); i += 3) {
        int w = 0;
        for (int a = 0; a < 3 && a + i < imgs.size(); a++) {
          RoundRectBkg im = imgs.get(a + i);
          w += im.getWidth();
        }
        width = Math.max(width, w);
      }
    } else {
      width = customizedWidth;
    }
    if (width > 1200) {
      width = 1200;
    }
    WrapLayout layout = new WrapLayout();
    JPanel panel = new JPanel(layout);
    panel.setBounds(0, 0, width, 100);
    panel.setOpaque(false);
    for (RoundRectBkg i : imgs) {
      i.setPreferredSize(i.getSize());
      panel.add(i);
    }
    panel.setSize(layout.preferredLayoutSize(panel));
    return panel;
  }

  public static BufferedImage convert(Component c) {
    int w = Math.max(50, Math.max(c.getWidth(), c.getBounds().width));
    int h = Math.max(100, Math.max(c.getHeight(), c.getBounds().height));
    BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    Graphics g = img.createGraphics();
    c.doLayout();
    c.paint(g);
    g.dispose();
    return img;
  }

  public static String registerFont(File file) throws Throwable {
    Font f;
    GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f = Font.createFont(Font.TRUETYPE_FONT, file));
    FONTS.add(f);
    return f.getName();
  }
}
