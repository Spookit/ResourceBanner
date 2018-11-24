package thito.resourcebanner;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.imageio.ImageIO;

public class IconContainer {
    public String url;
    public String data;

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    public BufferedImage get() {
        try {
            return get(data);
        } catch(Throwable e) {
            return null;
        }
    }

    BufferedImage get(String icon) throws Throwable {
        if(icon == null) return null;
        Decoder decoder = Base64.getDecoder();
        byte[] x = decoder.decode(icon.getBytes("UTF-8"));
        return ImageIO.read(new ByteArrayInputStream(x));
    }
}
