package thito.resourcebanner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MemeGenerator {

  public static final int SIZE = 400;

  public static BufferedImage generate(String additionalCaption, int fontSize) throws IOException {
    return additionalCaption == null ? loadRandomMeme() : addCaption(loadRandomMeme(), additionalCaption, fontSize);
  }

  private static BufferedImage loadRandomMeme() throws IOException {
    File memeFolder = BannerMaker.getFile("memes");
      if (!memeFolder.exists()) {
          memeFolder.mkdirs();
      }
    if (memeFolder.isDirectory()) {
      File[] memeFiles = memeFolder.listFiles(ImageUtil.IMAGES);
        if (memeFiles.length == 0) {
            return null;
        }
      File memeFile = memeFiles[ImageUtil.random.nextInt(memeFiles.length)];
      return ImageIO.read(memeFile);
    }
    return null;
  }

  private static BufferedImage addCaption(BufferedImage img, String caption, int size) {
      if (img == null) {
          return null;
      }
    JPanel panel = new JPanel(new BorderLayout());
    JLabel label = new JLabel("<html><center>" + caption);
    label.setFont(new Font("Arial Black", Font.BOLD, size));
    label.setHorizontalAlignment(SwingConstants.CENTER);
    label.setVerticalAlignment(SwingConstants.CENTER);
    label.setForeground(Color.WHITE);
    panel.add(label, BorderLayout.NORTH);
    panel.setBackground(Color.BLACK);
    ImagePanel image = new ImagePanel();
    image.image = img;
    image.setBounds(0, 0, SIZE, SIZE);
    panel.add(image, BorderLayout.CENTER);
    panel.setSize(SIZE, SIZE);
    return SwingUtil.convert(panel);
  }
}
