package thito.resourcebanner;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class IDCard extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    Author author;
    String f, sf;
    Color co;

    public IDCard(String authorID, int limit, Color c, String font, String subfont, int width) {
        author = Author.getAuthorWithIcon(authorID);
        setLayout(new BorderLayout(5, 5));
        if(c == null) c = ImageUtil.random();
        co = c;
        JLabel name = new JLabel(author.name);
        name.setHorizontalAlignment(SwingConstants.CENTER);
        name.setFont(new Font(font, Font.BOLD, 24));
        name.setForeground(BevelShape.x(c));
        add(new ImageRenderer(author.icon.get()), BorderLayout.CENTER);
        add(name, BorderLayout.NORTH);
        //setSize(Math.max(card.getWidth(),name.getWidth()),card.getHeight()+5+name.getHeight());
        setSize(300, 500);
        setOpaque(false);
    }

    public void paint(Graphics g) {
        if(g instanceof Graphics2D) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        g.setColor(co.darker());
        g.fillRoundRect(0, 8, getWidth(), getHeight() - 8, 25, 25);
        g.setColor(co);
        g.fillRoundRect(0, 0, getWidth(), getHeight() - 8, 25, 25);
        super.paint(g);

    }
}
