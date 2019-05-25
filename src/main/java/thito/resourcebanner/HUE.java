package thito.resourcebanner;

import java.awt.Color;

public class HUE {

	public static boolean WRAP = true;
	public static void main(String[] args) {
		System.out.println(maxHUE(Color.magenta));
	}

	public static int maxHUE(Color c) {
		if (WRAP) return 60;
		final HUE hue = new HUE(c);
		int index = 0;
		while (true) {
			if (c.equals(hue.next()))
				break;
			index++;
		}
		return index + 1;
	}

	float bri = 1F;
	float hue = ImageUtil.random.nextFloat();
	float sat = 0.5F;
	float step = 0.01F;
	boolean up = ImageUtil.random.nextBoolean();
	HUEArray arr;
	public HUE() {
	}

	public HUE(Color current) {
		final float[] vals = Color.RGBtoHSB(current.getRed(), current.getGreen(), current.getBlue(), new float[3]);
		hue = vals[0];
		sat = vals[1];
		bri = vals[2];
	}

	public Color next() {
		if (WRAP) {
			if (arr == null) arr = new HUEArray();
			return arr.next();
		}
		if (up) {
			if (hue >= hue*2) {
				up = false;
				hue -= step;
			} else
				hue += step;
		} else {
			if (hue <= hue*-2) {
				up = true;
				hue += step;
			} else
				hue -= step;
		}
		return Color.getHSBColor(hue, sat, bri);
	}
}
