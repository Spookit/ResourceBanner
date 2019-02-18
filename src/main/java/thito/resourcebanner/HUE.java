package thito.resourcebanner;

import java.awt.Color;

public class HUE {

	float hue = ImageUtil.random.nextFloat();
	boolean up = ImageUtil.random.nextBoolean();
	float step = 0.01F;
	float sat = 0.5F;
	float bri = 1F;

	public Color next() {
		if (up) {
			if (hue >= 1) {
				up = false;
				hue -= step;
			} else
				hue += step;
		} else {
			if (hue <= 0) {
				up = true;
				hue += step;
			} else
				hue -= step;
		}
		return Color.getHSBColor(hue, sat, bri);
	}
}
