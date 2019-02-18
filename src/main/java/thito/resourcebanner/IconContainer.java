package thito.resourcebanner;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.imageio.ImageIO;

public class IconContainer {

	public String data;

	public BufferedImage get() {
		try {
			return get(data);
		} catch (Throwable e) {
			return null;
		}
	}

	BufferedImage get(String icon) throws Throwable {
		if (icon == null)
			return null;
		Decoder decoder = Base64.getDecoder();
		byte[] x = decoder.decode(icon.getBytes("UTF-8"));
		return ImageIO.read(new ByteArrayInputStream(x));
	}
}
