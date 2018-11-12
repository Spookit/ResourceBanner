package thito.resourcebanner;

import javax.swing.JPanel;
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
import java.util.HashMap;
import java.util.Map.Entry;

public class BevelShape extends JPanel {

    static final FontRenderContext frc = new FontRenderContext(null, true, true);
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    Color rate;
    HashMap<TextLayout, Point> texts = new HashMap<>();
    HashMap<BufferedImage, Rectangle> image = new HashMap<>();

    public BevelShape(Boolean bright) {
        rate = new Color(
                bright == null ? ImageUtil.random.nextInt(255) : ImageUtil.random.nextInt(130) + (bright ? 125 : 0),
                bright == null ? ImageUtil.random.nextInt(255) : ImageUtil.random.nextInt(130) + (bright ? 125 : 0),
                bright == null ? ImageUtil.random.nextInt(255) : ImageUtil.random.nextInt(130) + (bright ? 125 : 0)
        );
        setBounds(0, 0, 50, 100);
    }

    private static int getBrightness(Color c) {
        return (int) Math.sqrt(
                c.getRed() * c.getRed() * .241 +
                        c.getGreen() * c.getGreen() * .691 +
                        c.getBlue() * c.getBlue() * .068);
    }

    public static Color x(Color color) {
        if((getBrightness(color)) <= 130)
            return Color.white;
        else
            return color.darker().darker().darker();
    }

    public static int perc(double x, double max) {
        return Double.valueOf(x / max).intValue();
    }

    public boolean isBright() {
        return rate.getBlue() > 200 || rate.getGreen() > 200 || rate.getRed() > 200;
    }

    public void addText(String s, Font font, int x, int y) {
        FontMetrics met = this.getFontMetrics(font);
        int width = x + met.stringWidth(s) + x;
        if(width > getWidth()) setSize(width, getHeight());
        TextLayout t = new TextLayout(s, font, frc);

        texts.put(t, new Point(x, y));
    }

    public void addImage(BufferedImage img, int x, int y, int w, int h) {
        image.put(img, new Rectangle(x, y, w, h));
    }

    public void paint(Graphics g) {
        if(g instanceof Graphics2D) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        g.setColor(rate.darker());
        g.fillRoundRect(0, 8, getWidth(), getHeight() - 8, 25, 25);
        g.setColor(rate);
        g.fillRoundRect(0, 0, getWidth(), getHeight() - 8, 25, 25);
        g.setColor(x(rate));
        if(g instanceof Graphics2D) for(Entry<TextLayout, Point> t : texts.entrySet()) {
            t.getKey().draw((Graphics2D) g, (float) t.getValue().getX(), (float) t.getValue().getY());
        }
        for(Entry<BufferedImage, Rectangle> img : image.entrySet()) {
            g.drawImage(img.getKey(), img.getValue().x, img.getValue().y, img.getValue().width, img.getValue().height, this);
        }
        g.dispose();
    }
}
