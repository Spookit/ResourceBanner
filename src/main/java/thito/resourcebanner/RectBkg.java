package thito.resourcebanner;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class RectBkg extends JPanel {

	private static final FontRenderContext frc = new FontRenderContext(null, true, true);
	private static final long serialVersionUID = 1L;

	private static int getBrightness(Color c) {
		return (int) Math.sqrt(
				c.getRed() * c.getRed() * .241 + c.getGreen() * c.getGreen() * .691 + c.getBlue() * c.getBlue() * .068);
	}

	public static Color x(Color color) {
		if (getBrightness(color) <= 130) {
			return Color.white;
		} else {
			return color.darker().darker().darker();
		}
	}

	private double average;
	private boolean countBoth = true;
	private HUE hue;
	private final Map<BufferedImage, Rectangle> image = new HashMap<>();
	private Boolean lightText;
	protected Color rate;
	private Rectangle rectangle;
	private boolean rounded = true;
	private Color textColor;
	private final Map<TextLayout, Point> texts = new HashMap<>();

	public RectBkg(Boolean bright) {
		rate = new Color(
				bright == null ? ImageUtil.random.nextInt(255) : ImageUtil.random.nextInt(130) + (bright ? 125 : 0),
				bright == null ? ImageUtil.random.nextInt(255) : ImageUtil.random.nextInt(130) + (bright ? 125 : 0),
				bright == null ? ImageUtil.random.nextInt(255) : ImageUtil.random.nextInt(130) + (bright ? 125 : 0));
		setBounds(0, 0, 50, 100);
		setSize(50, 100);
	}

	public void addImage(BufferedImage img, int x, int y, int w, int h) {
		image.put(img, new Rectangle(x, y, w, h));
	}

	public void addText(String s, Font font, int x, int y) {
		final FontMetrics met = getFontMetrics(font);
		final int width = x + met.stringWidth(s) + (countBoth ? x : 15);
		if (width > getWidth()) {
			setSize(width, getHeight());
		}
		final TextLayout t = new TextLayout(s, font, frc);
		texts.put(t, new Point(x, y));
	}

	private void applyResourceRatingStars(Graphics g, Rectangle bound, double average) {
		final double gap = bound.width / 100D * 2;
		bound.width -= gap;
		final int width = bound.width / 5;
		for (int i = 0; i < 5; i++) {
			if (average >= 1) {
				average -= 1;
				try {
					g.drawImage(ImageIO.read(BannerMaker.getResource("star.png")), bound.x + ((int) gap + width) * i,
							bound.y, width, bound.height, null);
				} catch (final IOException e) {
					e.printStackTrace();
				}
			} else if (average >= 0.5) {
				average -= 0.5;
				try {
					g.drawImage(ImageIO.read(BannerMaker.getResource("star_half.png")),
							bound.x + ((int) gap + width) * i, bound.y, width, bound.height, null);
				} catch (final IOException e) {
					e.printStackTrace();
				}
			} else if (average < 0.5) {
				try {
					g.drawImage(ImageIO.read(BannerMaker.getResource("star_off.png")),
							bound.x + ((int) gap + width) * i, bound.y, width, bound.height, null);
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void nextHUE() {
		if (lightText == null)
			lightText = getBrightness(rate) <= 130;
		if (hue == null)
			hue = new HUE(rate);
		rate = hue.next();
	}

	@Override
	public void paint(Graphics g) {
		if (g instanceof Graphics2D) {
			final Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g.setColor(rate.darker());
		if (rounded) {
			g.fillRoundRect(0, 8, getWidth(), getHeight() - 8, 25, 25);
		}
		g.setColor(rate);
		if (rounded) {
			g.fillRoundRect(0, 0, getWidth(), getHeight() - 8, 25, 25);
		} else {
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		if (hue == null) {
			textColor = x(rate);
		}
		if (textColor == null)
			textColor = x(rate);
		g.setColor(textColor);
		if (g instanceof Graphics2D) {
			for (final Entry<TextLayout, Point> t : texts.entrySet()) {
				t.getKey().draw((Graphics2D) g, (float) t.getValue().getX(), (float) t.getValue().getY());
			}
		}
		for (final Entry<BufferedImage, Rectangle> img : image.entrySet()) {
			g.drawImage(img.getKey(), img.getValue().x, img.getValue().y, img.getValue().width, img.getValue().height,
					this);
		}
		if (rectangle != null) {
			applyResourceRatingStars(g, new Rectangle(rectangle), average);
		}
	}

	public void setCountBoth(boolean countBoth) {
		this.countBoth = countBoth;
	}

	public void setRate(Color rate) {
		this.rate = rate;
	}

	public void setRatings(int x, int y, int w, int h, double average) {
		rectangle = new Rectangle(x, y, w, h);
		this.average = average;
	}

	public void setRounded(boolean rounded) {
		this.rounded = rounded;
		if (!rounded) {
			final int height = getHeight() - 8;
			setBounds(0, 0, getWidth(), height);
			setSize(getWidth(), height);
		}
	}
}
