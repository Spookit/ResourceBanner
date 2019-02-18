package thito.resourcebanner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.FileFilter;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImageUtil {

  public static final FileFilter IMAGES;
  public static final Color[] COLORS = {new Color(178, 179, 253), new Color(166, 243, 157), new Color(248, 130, 129)};
  public static final Random random = new Random();

  static {
    IMAGES = file -> file != null && (
        file.getName().endsWith(".png") ||
            file.getName().endsWith(".jpg") ||
            file.getName().endsWith(".jpeg") ||
            file.getName().endsWith(".gif") ||
            file.getName().endsWith(".bmp") ||
            file.getName().endsWith(".webm")
    );
  }

  public static Color getBrightColor() {
    return new Color(125 + random.nextInt(130), 125 + random.nextInt(130), 125 + random.nextInt(130));
  }

  public static Color hexToRgb(String colorStr) {
      if (!colorStr.startsWith("#")) {
          colorStr = "#" + colorStr;
      }
    return Color.decode(colorStr);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("Color Picker :P");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JButton butt = new JButton("i love it");
    JButton next = new JButton("meh, NEXT!");
    JPanel shower = new JPanel();
    shower.setBackground(getBrightColor());
    butt.addActionListener(a -> {
      System.out.println("g(" + shower.getBackground().getRed() + "," + shower.getBackground().getGreen() + ","
          + shower.getBackground().getBlue() + "),");
      shower.setBackground(getBrightColor());
    });
    next.addActionListener(a -> shower.setBackground(getBrightColor()));
    frame.add(shower, BorderLayout.CENTER);
    frame.add(next, BorderLayout.WEST);
    frame.add(butt, BorderLayout.EAST);
    frame.setVisible(true);
  }

  public static Color getNiceColor() {
    return COLORS[random.nextInt(COLORS.length)];
  }

}
