package thito.resourcebanner;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Experimental {

	public static void createRatings(Graphics g, Rectangle bound, double average) {
		double gap = (bound.width / 100D) * 2;
		bound.width -= gap;
		int width = bound.width / 5;
		for (int i = 0; i < 5; i++) {
			if (average >= 1) {
				average -= 1;
				try {
					g.drawImage(ImageIO.read(BannerMaker.getResource("star.png")), bound.x + (((int) gap + width) * i),
							bound.y, width, bound.height, null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (average >= 0.5) {
				average -= 0.5;
				try {
					g.drawImage(ImageIO.read(BannerMaker.getResource("star_half.png")),
							bound.x + (((int) gap + width) * i), bound.y, width, bound.height, null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (average < 0.5) {
				try {
					g.drawImage(ImageIO.read(BannerMaker.getResource("star_off.png")),
							bound.x + (((int) gap + width) * i), bound.y, width, bound.height, null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
