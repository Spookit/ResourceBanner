package thito.resourcebanner.resource;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.imageio.ImageIO;

public class IconContainer {

  public String data;

  public BufferedImage getResourceIcon() {
    try {
      if (data == null) {
        return null;
      }
      Decoder decoder = Base64.getDecoder();
      byte[] x = decoder.decode(data.getBytes(StandardCharsets.UTF_8));
      return ImageIO.read(new ByteArrayInputStream(x));
    } catch (Throwable e) {
      return null;
    }
  }

}
