package thito.resourcebanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Marquee {

	class Char {
		char c;
		boolean color;
		char n;

		Char(char b) {
			n = b;
		}

		Char(char a, char b) {
			c = a;
			n = b;
			color = true;
		}

		@Override
		public String toString() {
			return color ? COLOR_CHAR + "" + c + "" + n : n + "";
		}

		public String toStringColorOnly() {
			return color ? COLOR_CHAR + "" + c : n + "";
		}
	}

	public static final char COLOR_CHAR = '§';

	public static void main(String[] args) {
		final Marquee mar = new Marquee("i want &asomething &rjust like this".replace('&', COLOR_CHAR), 200);
		final JLabel label = new JLabel(mar.next());
		new Thread() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, label);
				System.exit(0);
			}
		}.start();
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				label.setText("----" + mar.next() + "----");
			}
		}, 1L, 1L);
	}

	public static void main0(String[] args) {
		final Marquee mar = new Marquee("i want &asomething &rjust like this".replace('&', COLOR_CHAR), 50);
		for (final Char c : mar.chars) {
			System.out.println(mar.next());
		}
	}

	private final Char[] chars;
	private Char lastColor = new Char('r', ' ');
	private int offset = 0;
	private final int size;

	public Marquee(String text, int size) {
		this.size = size;
		final List<Char> ch = new ArrayList<>();
		boolean color = false;
		Character last = null;
		for (final char c : text.toCharArray()) {
			if (c == COLOR_CHAR) {
				if (color) {
					ch.add(new Char(c));
				} else {
					color = true;
				}
			} else {
				if (color) {
					if (last == null) {
						last = c;
					} else {
						ch.add(new Char(last, c));
						last = null;
						color = false;
					}
				} else {
					ch.add(new Char(c));
				}
			}
		}
		chars = ch.toArray(new Char[ch.size()]);
	}

	Char getCharAt(int index) {
		return chars[index % chars.length];
	}

	public String next() {
		if (offset >= chars.length)
			offset = 0;
		String string = lastColor.toStringColorOnly();
		for (int i = offset; i < offset + size; i++) {
			final Char last = getCharAt(i);
			if (last.color && i == offset) {
				lastColor = last;
			}
			string += last.toString();
		}
		offset++;
		return string;
	}
}
