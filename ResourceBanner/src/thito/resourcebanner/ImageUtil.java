package thito.resourcebanner;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Random;

public class ImageUtil {
	static Color g(int r,int g,int b) {
		return new Color(r,g,b);
	}
	static final Color[] COLORS = {
			g(0,102,51),
			g(102,102,255),
			g(51,102,102),
			g(51,153,102),
			g(153,51,204),
			g(204,51,153),
			g(255,102,102),
			g(0,51,5)
	};
	public static Color random() {
		return g(random.nextInt(255),random.nextInt(255),random.nextInt(255));
	}
	public static Color hex2Rgb(String colorStr) {
	    return new Color(
	            Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
	            Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
	            Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
	}
	public static Color niceColor() {
		return COLORS[random.nextInt(COLORS.length)];
	}
	static String limit(String x,int lim) {
		lim-=3;
		if (x.length() > lim) {
			x = x.substring(0,lim)+"...";
		}
		return x;
	}
	static final Random random = new Random();
	public static BufferedImage getMost(BufferedImage img,BufferedImage target) {
		int width = img.getWidth();
	    int height = img.getHeight();
	    WritableRaster raster = img.getRaster();
	    int reds = 0;
	    int greens = 0;
	    int blues = 0;
	    int pixelindex = 0;
	    WritableRaster r2 = target.getRaster();
	    for (int xx = 0; xx < target.getWidth(); xx++) {
		      for (int yy = 0; yy < target.getHeight(); yy++) {
		        int[] pixels = r2.getPixel(xx, yy, (int[]) null);
		        int red = pixels[0];
		        int green = pixels[1];
		        int blue = pixels[2];
		        reds+=red;
		        greens+=green;
		        blues+=blue;
		        pixelindex++;
		      }
		    }
	    Color x = new Color(reds/pixelindex,greens/pixelindex,blues/pixelindex);
	    for (int xx = 0; xx < width; xx++) {
		      for (int yy = 0; yy < height; yy++) {
		        int[] pixels = raster.getPixel(xx, yy, (int[]) null);
		        int red = pixels[0];
		        int green = pixels[1];
		        int blue = pixels[2];
		        int alpha = pixels[3];
		        if (red != 252 && green != 254 && blue != 252 && x != null){
		        	double percentage = ((double)green)/290;
		        	int[] c = mixColors(x,red,green,blue,percentage);
		        	red = c[0]++;
		        	green = c[1]++;
		        	blue = c[2]++;
		        }
		        Color col = new Color(red,green,blue,alpha);
		        col = col.brighter();
		        pixels[0] = col.getRed();
		        pixels[1] = col.getGreen();
		        pixels[2] = col.getBlue();
		        pixels[3] = col.getAlpha();
		        raster.setPixel(xx, yy, pixels);
		      }
		    }
		return img;
	}
	public static BufferedImage randomHUE(BufferedImage image,BevelShape img) {
	    int width = image.getWidth();
	    int height = image.getHeight();
	    WritableRaster raster = image.getRaster();
	    Color x = new Color(random.nextInt(200),random.nextInt(200),random.nextInt(200));
	    img.rate = x;
	    for (int xx = 0; xx < width; xx++) {
		      for (int yy = 0; yy < height; yy++) {
		        int[] pixels = raster.getPixel(xx, yy, (int[]) null);
		        int red = pixels[0];
		        int green = pixels[1];
		        int blue = pixels[2];
		        int alpha = pixels[3];
		        if (red != 252 && green != 254 && blue != 252 && x != null){
		        	double percentage = ((double)green)/290;
		        	int[] c = mixColors(x,red,green,blue,percentage);
		        	red = c[0]++;
		        	green = c[1]++;
		        	blue = c[2]++;
		        }
		        if (red == 255 && green == 0 && blue == 255) {
		        	alpha = 0;
		        }
		        Color col = new Color(red,green,blue,alpha);
		        col = col.brighter();
		        pixels[0] = col.getRed();
		        pixels[1] = col.getGreen();
		        pixels[2] = col.getBlue();
		        pixels[3] = col.getAlpha();
		        raster.setPixel(xx, yy, pixels);
		      }
		    }
	    return image;
	  }
	public static BufferedImage randomizeHUE(BufferedImage image) {
	    int width = image.getWidth();
	    int height = image.getHeight();
	    WritableRaster raster = image.getRaster();
	    Color x = COLORS[random.nextInt(COLORS.length)];
	    
	    for (int xx = 0; xx < width; xx++) {
		      for (int yy = 0; yy < height; yy++) {
		        int[] pixels = raster.getPixel(xx, yy, (int[]) null);
		        int red = pixels[0];
		        int green = pixels[1];
		        int blue = pixels[2];
		        int alpha = pixels[3];
		        if (red != 252 && green != 254 && blue != 252 && x != null){
		        	double percentage = ((double)green)/290;
		        	int[] c = mixColors(x,red,green,blue,percentage);
		        	red = c[0]++;
		        	green = c[1]++;
		        	blue = c[2]++;
		        }
		        if (red == 255 && green == 0 && blue == 255) {
		        	alpha = 0;
		        }
		        Color col = new Color(red,green,blue,alpha);
		        col = col.brighter();
		        pixels[0] = col.getRed();
		        pixels[1] = col.getGreen();
		        pixels[2] = col.getBlue();
		        pixels[3] = col.getAlpha();
		        raster.setPixel(xx, yy, pixels);
		      }
		    }
	    return image;
	  }
	public static int[] mixColors(Color color1, int red,int green,int blue, double percent){
	      double inverse_percent = 1.0 - percent;
	      int redPart = (int) (color1.getRed()*percent + red*inverse_percent);
	      int greenPart = (int) (color1.getGreen()*percent + green*inverse_percent);
	      int bluePart = (int) (color1.getBlue()*percent +  blue*inverse_percent);
	      return new int[] {Math.max(255, redPart),Math.max(255, greenPart),Math.max(255, bluePart)};
	}
}
