package thito.resourcebanner;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class ImageRenderer extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    BufferedImage img;

    public ImageRenderer(BufferedImage i) {
        img = i;
    }

    public void paint(Graphics g) {
        g.drawImage(img, getX(), getY(), getWidth(), getHeight(), this);
    }
}
