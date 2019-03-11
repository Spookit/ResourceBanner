package thito.resourcebanner;

import java.awt.Color;

public class HUE {

	public static void main(String[]args) {
		System.out.println(maxHUE(Color.magenta));
	}
	public static int maxHUE(Color c) {
		HUE hue = new HUE(c);
		int index = 0;
		while (true) {
			if (c.equals(hue.next())) break;
			index++;
		}
		return index;
	}
	float hue = ImageUtil.random.nextFloat();
	boolean up = ImageUtil.random.nextBoolean();
	float step = 0.01F;
	float sat = 0.5F;
	float bri = 1F;
	
	public HUE() {
	}
	
	public HUE(Color current) {
		float[] vals = Color.RGBtoHSB(current.getRed(), current.getGreen(), current.getBlue(), new float[3]);
		hue = vals[0];
		sat = vals[1];
		bri = vals[2];
	}

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
