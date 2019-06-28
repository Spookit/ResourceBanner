package thito.resourcebanner;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

import thito.resourcebanner.resource.SpigotResource;
import thito.resourcebanner.utils.Utils;
import thito.septo.io.Client;
import thito.septo.io.Client.Paths;
import thito.septo.io.ClientListener;
import thito.septo.io.ContentType;
import thito.septo.io.HttpField;
import thito.septo.io.Response;
import thito.septo.io.ResponseType;
import thito.septo.io.Server;

public class BannerMakerServer extends Server implements ClientListener {

	public BannerMakerServer(int port) {
		super(port);
		addListener(this);
	}
	private final String[] supportedTypes = { "png", "jpg", "jpeg", "webp" };
	@Override
	public void accept(Client client) throws IOException {
		BannerMaker.increaseTotalRequests();
		BannerMaker.setLastConnection(System.currentTimeMillis());
		Response response = new Response(ResponseType.ACCEPTED);
		response.setRequestProperty(HttpField.CACHE_CONTROL, "private, no-store, no-cache, must-revalidate");
		response.setRequestProperty(HttpField.PRAGMA, "no-cache");
		response.setRequestProperty(HttpField.DATE, new Date());
		response.setRequestProperty(HttpField.EXPIRES, new Date());
		/*
		 * Global Queries
		 */
		String fontName = "?";
		String subFont = null;
		Boolean bright = null;
		final int defaultType = 0;
		String format = supportedTypes[defaultType];
		final Paths paths = client.createPaths();
		final Properties query = paths.parseQuery();
		int width = -1;
		int sizeLimit = 6;
		Sort.SortType sortBy = null;
		int truncate = -1;
		Sort.SortDirection sortOrder = null;
		Color defColor = null;
		boolean rounded = true;
		boolean rainbow = false;
		/* End of Global Queries*/
		if (query.containsKey("font")) fontName = query.getProperty("font");
		if (query.containsKey("subfont")) subFont = query.getProperty("subfont");
		if (query.containsKey("bright")) bright = Boolean.parseBoolean(query.getProperty("bright"));
		if (!paths.isEmpty()) {
			final String[] f = paths.last().split("\\.",2);
			if (f.length == 2) {
				paths.setLast(f[1].toLowerCase());
				paths.getAll()[0] = f[0];
				if (!Arrays.asList(supportedTypes).contains(format)) format = supportedTypes[defaultType];
			}
		}
		response.setRequestProperty(HttpField.CONTENT_TYPE, "image/"+format);
		if (query.containsKey("sort")) sortBy = Sort.SortType._valueOf(query.getProperty("sort"));
		if (query.containsKey("order")) sortOrder = Sort.SortDirection._valueOf(query.getProperty("order"));
		if (query.containsKey("truncate")) {
			String trun = query.getProperty("truncate");
			if (Utils.isInteger(trun)) truncate = Integer.parseInt(trun);
		}
		if (query.containsKey("color")) {
			defColor = ImageUtil.hexToRgb(query.getProperty("color"));
		} else if (query.containsKey("nicecolor") && Boolean.getBoolean(query.getProperty("nicecolor"))) {
			defColor = ImageUtil.getNiceColor();
		}
		if (query.containsKey("size")) {
			String si = query.getProperty("size");
			if (Utils.isInteger(query.getProperty(si))) {
				sizeLimit = Math.min(120, Integer.parseInt(si));
			}
		}
		if (query.containsKey("type")) {
			if (query.getProperty("type").equalsIgnoreCase("flat")) {
				rounded = false;
			}
		}
		if (query.containsKey("rainbow")) {
			rainbow = Boolean.parseBoolean(query.getProperty("rainbow"));
			if (rainbow) response.setRequestProperty(HttpField.CONTENT_TYPE, ContentType.IMAGE_GIF);
		}
		if (paths.is("favicon.ico")) {
			client.send(response);
			client.send(getResource("icon.ico"));
			BannerMaker.done();
			return;
		}
		if (paths.is("marquee")) {
			String text = query.getProperty("text");
			if (!query.containsKey("size")) sizeLimit = 50;
			int speed = 100;
			String sp = query.getProperty("speed");
			if (sp != null && Utils.isInteger(sp)) speed = Integer.parseInt(sp);
			if (text == null) {
				if (paths.has()) text = paths.get();
				else text = "You must define the text query before using this.";
			}
			client.send(response);
			final Marquee mar = new Marquee(text,sizeLimit);
			final GifSequenceWriter writer = new GifSequenceWriter(client.getOutputStream(), BufferedImage.TYPE_INT_ARGB, speed, true);
			Color last = defColor;
			for (int i = 0; i < text.length(); i++) {
				final RectBkg img = new RectBkg(bright);
				img.setRounded(rounded);
				if (last == null) {
					last = img.rate;
				} else {
					img.rate = last;
				}
				img.addText(mar.next().substring(2), new Font(fontName, Font.BOLD, 13), 0, 20);
				img.setBounds(0, 0, sizeLimit * 5, 40);
				img.setSize(sizeLimit * 5, 40);
				writer.writeToSequence(SwingUtil.convert(img, 10, 10));
			}
			writer.close();
			BannerMaker.done();
			return;
		}
		if (sizeLimit > 50) sizeLimit = 50;
		if (paths.is("data")) {
			response.setRequestProperty(HttpField.CONTENT_TYPE, ContentType.APPLICATION_JSON);
			final Map<String, Object> data = new HashMap<>();
			data.put("resources", BannerMaker.getCachedResources());
			data.put("authors", BannerMaker.getCachedAuthors());
			final HashSet<String> fonts = new HashSet<>();
			for (final Font f : SwingUtil.FONTS) {
				fonts.add(f.getName());
			}
			data.put("fonts", fonts);
			response.setContent(BannerMaker.getGson().toJson(data).getBytes());
			BannerMaker.done();
			return;
		}
		if (paths.is("generator")) {
			response.setRequestProperty(HttpField.CONTENT_TYPE, ContentType.TEXT_HTML);
			client.send(response);
			client.send(new FileInputStream(getFile("generator.html")));
			BannerMaker.done();
			return;
		}
		if (paths.is("fonts")) {
			final GridLayout layout = new GridLayout(SwingUtil.FONTS.size(), 1);
			final JPanel panel = new JPanel(layout);
			int mw = 0;
			for (final Font f : SwingUtil.FONTS) {
				final JLabel label = new JLabel(f.getName());
				label.setFont(new Font(f.getName(), f.getStyle(), 30));
				final int w = label.getFontMetrics(label.getFont()).stringWidth(label.getText()) + 50;
				mw = Math.max(mw, w);
				label.setSize(100, 10);
				label.setBounds(0, 0, 100, 10);
				panel.add(label);
			}
			panel.doLayout();
			panel.setSize(mw, SwingUtil.FONTS.size() * 30);
			panel.setOpaque(false);
			client.send(response);
			ImageIO.write(SwingUtil.convert(panel), format, client.getOutputStream());
			BannerMaker.done();
			return;
		}
		if (paths.is("random")) {
			if (paths.has()) {
				final String authorID = paths.next();
				final List<SpigotResource> res = BannerMaker.getSpigotResourceHandler().byAuthor(authorID, 10, sortBy,
						sortOrder);
				final List<RectBkg> imgs = new ArrayList<>();
				if (res.isEmpty()) {
					imgs.add(BannerMaker.noResource(new RectBkg(bright), fontName));
				} else {
					final RectBkg rect = new RectBkg(bright);
					rect.setRounded(rounded);
					imgs.add(BannerMaker.process(rect, res.get(ImageUtil.random.nextInt(res.size())), fontName, subFont,
							defColor));
				}
				if (rainbow) {
					client.send(response);
					final GifSequenceWriter writer = new GifSequenceWriter(client.getOutputStream(), BufferedImage.TYPE_INT_ARGB, 1,
							true);
					for (int i = 0; i < BannerMaker.GIF_FRAMES; i++) {
						writer.writeToSequence(SwingUtil.convert(SwingUtil.collect(imgs, width)));
						imgs.forEach(a -> {
							a.nextHUE();
						});
					}
					writer.close();
				} else {
					final JPanel j = SwingUtil.collect(imgs, width);
					client.send(response);
					ImageIO.write(SwingUtil.convert(j), format, client.getOutputStream());
				}
				BannerMaker.done();
				return;
			}
			response = new Response(ResponseType.NOT_FOUND);
			client.send(response);
			return;
		}
		if (paths.is("author")) {
			if (paths.has()) {
				final String authorID = paths.get();
				if (!Utils.isInteger(authorID)) {
					client.send(response);
					ImageIO.write(MemeGenerator.generate("Please use Author ID instead of Author Name", 13),
							format, client.getOutputStream());
					BannerMaker.done();
					return;
				}
				final List<SpigotResource> res = BannerMaker.getSpigotResourceHandler().byAuthor(authorID, sizeLimit,
						sortBy, sortOrder);
				final List<RectBkg> imgs = new ArrayList<>();
				if (sizeLimit > 0) {
					int resourcesLimit = sizeLimit;
					if (res.size() < resourcesLimit) {
						resourcesLimit = res.size();
					}
					for (final SpigotResource r : res) {
						if (resourcesLimit == 0) {
							break;
						}
						final RectBkg rect = new RectBkg(bright);
						rect.setRounded(rounded);
						imgs.add(BannerMaker.process(rect, r, fontName, subFont, defColor));
						resourcesLimit--;
					}
				}
				if (imgs.isEmpty()) {
					imgs.add(BannerMaker.noResource(new RectBkg(bright), fontName));
				}
				BannerMaker.getCachedAuthors().add(authorID);
				client.send(response);
				if (rainbow) {
					final GifSequenceWriter writer = new GifSequenceWriter(client.getOutputStream(), BufferedImage.TYPE_INT_ARGB, 1,
							true);
					for (int i = 0; i < BannerMaker.GIF_FRAMES; i++) {
						writer.writeToSequence(SwingUtil.convert(SwingUtil.collect(imgs, width)));
						imgs.forEach(a -> {
							a.nextHUE();
						});
					}
					writer.close();
				} else {
					final JPanel j = SwingUtil.collect(imgs, width);
					ImageIO.write(SwingUtil.convert(j), format, client.getOutputStream());
				}
				BannerMaker.done();
				return;
			}
			response = new Response(ResponseType.NOT_FOUND);
			client.send(response);
			return;
		}
		if (paths.is("meme")) {
			final File[] files = getFile("memes").listFiles();
			final File meme = files[ImageUtil.random.nextInt(files.length)];
			final String[] split = meme.getName().split("\\.");
			format = split[split.length - 1];
			if (format.startsWith(".")) format = format.substring(1);
			response.setRequestProperty(HttpField.CONTENT_TYPE, format);
			client.send(response);
			client.send(new FileInputStream(meme));
			BannerMaker.done();
			return;
		}
		if (paths.is("spiget")) {
			final SpigetStatus stats = SpigetStatus.getSpigetStatus();
			final RectBkg img = new RectBkg(bright);
			img.setRounded(rounded);
			if (defColor != null) {
				img.setRate(defColor);
			}
			img.addText("API Status - Spiget", new Font(fontName, Font.BOLD, 13), 15, 20);
			img.addText("Server Name: " + stats.getStatus().getServer().getName(), new Font(subFont, 0, 11), 15,
					35);
			img.addText("Server Mode: " + stats.getStatus().getServer().getMode(), new Font(subFont, 0, 11), 15,
					50);
			img.addText("Resources: " + stats.getStats().getResources(), new Font(subFont, 0, 11), 15, 65);
			img.addText("Authors: " + stats.getStats().getAuthors(), new Font(subFont, 0, 11), 15, 80);
			if (width > 0) {
				img.setSize(width, img.getHeight());
			}
			client.send(response);
			if (rainbow) {
				final GifSequenceWriter writer = new GifSequenceWriter(client.getOutputStream(), BufferedImage.TYPE_INT_ARGB, 1,
						true);
				final int max = HUE.maxHUE(img.rate);
				for (int i = 0; i < max; i++) {
					writer.writeToSequence(SwingUtil.convert(img));
					img.nextHUE();
				}
				writer.close();
			} else {
				ImageIO.write(SwingUtil.convert(img), format, client.getOutputStream());
			}
			BannerMaker.done();
			return;
		}
		if (paths.is("stats","status","stat")) {
			final RectBkg img = new RectBkg(bright);
			BannerMaker.showStatsImage(img, fontName, subFont);
			if (width > 0) {
				img.setSize(width, img.getHeight());
			}
			img.setRounded(rounded);
			client.send(response);
			if (rainbow) {
				final GifSequenceWriter writer = new GifSequenceWriter(client.getOutputStream(), BufferedImage.TYPE_INT_ARGB, 1,
						true);
				final int max = HUE.maxHUE(img.rate);
				for (int i = 0; i < max; i++) {
					writer.writeToSequence(SwingUtil.convert(img));
					img.nextHUE();
				}
				writer.close();
			} else {
				ImageIO.write(SwingUtil.convert(img), format, client.getOutputStream());
			}
			BannerMaker.done();
			return;
		}
		if (paths.is("resource")) {
			if (paths.has()) {
				final String resourceID = paths.get();
				if (!Utils.isInteger(resourceID)) {
					client.send(response);
					ImageIO.write(MemeGenerator.generate("Please use Resource ID instead of Resource Name", 11),
							format, client.getOutputStream());
					BannerMaker.done();
					return;
				}
				final SpigotResource resource = BannerMaker.getSpigotResourceHandler().getResource(resourceID);
				if (truncate > 0) {
					resource.setName(
							resource.getName().substring(0, Math.min(resource.getName().length(), truncate)));
				}
				final RectBkg img = new RectBkg(bright);
				img.setRounded(rounded);
				if (resource != null) {
					BannerMaker.process(img, resource, fontName, subFont, defColor);
					BannerMaker.getCachedResources().add(resourceID);
				} else {
					BannerMaker.addBigText(img, "Not Found :/", fontName);
				}
				if (width > 0) {
					img.setSize(width, img.getHeight());
				}
				client.send(response);
				if (rainbow) {
					final GifSequenceWriter writer = new GifSequenceWriter(client.getOutputStream(), BufferedImage.TYPE_INT_ARGB, 1,
							true);
					final int max = HUE.maxHUE(img.rate);
					for (int i = 0; i < max; i++) {
						writer.writeToSequence(SwingUtil.convert(img));
						img.nextHUE();
					}
					writer.close();
				} else {
					ImageIO.write(SwingUtil.convert(img), format, client.getOutputStream());
				}
				BannerMaker.done();
				return;
			}
			client.send(new Response(ResponseType.NOT_FOUND));
			return;
		}
		response.setContent(("<html><head><title>Unknown Route</title></head><body>Redirecting... <br>if it doesn't go to <br>"
				+ "https://www.spigotmc.org/threads/resource-banner-generate-your-own-banner.346493/"
				+ "</body><script>window.location = '" + "https://www.spigotmc.org/threads/346493/"
				+ "'</script><html>").getBytes());
		response.setRequestProperty(HttpField.CONTENT_TYPE, ContentType.TEXT_HTML);
		response.setRequestProperty(HttpField.LOCATION, "https://www.spigotmc.org/threads/346493/");
		client.send(response);
		BannerMaker.done();
	}
	public File getFile(String name) {
		return BannerMaker.getFile(name);
	}
	public InputStream getResource(String name) {
		return BannerMaker.getResource(name);
	}

}
