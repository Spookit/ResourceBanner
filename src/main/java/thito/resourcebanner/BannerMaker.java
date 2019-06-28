package thito.resourcebanner;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.HashSet;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import thito.resourcebanner.handlers.SpigotResourceHandler;
import thito.resourcebanner.resource.SpigotResource;
import thito.resourcebanner.utils.Utils;

public class BannerMaker /*extends WebServer*/ {

	private static Set<String> cachedAuthors = new HashSet<>();
	private static Set<String> cachedResources = new HashSet<>();
	private static Properties config = new Properties();
	private static Thread configSaveThread;
	private static long fetchTime;
	public static int GIF_FRAMES = 120;
	private static long lastConnection = 0;
	private static RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
	private static long totalRequests = 0;

	public static void increaseTotalRequests() {
		totalRequests++;
	}
	public static Set<String> getCachedAuthors() {
		return cachedAuthors;
	}
	public static Set<String> getCachedResources() {
		return cachedResources;
	}
	
	public static void dispatchCommand(String cmd) {
		if (cmd.startsWith("exit")) {
			System.exit(0);
			return;
		}
		if (cmd.startsWith("save")) {
			System.out.println("Saving configuration...");
			configSaveThread.run();
			System.out.println("Done!");
			return;
		}
		if (cmd.startsWith("help")) {
			System.out.println("Commands:");
			System.out.println(" help - Shows this page");
			System.out.println(" save - Save the config");
			System.out.println(" exit - Stop the app");
			return;
		}
		System.out.println("Unknown Command! Type 'help' for help");
	}

	public static void done() {
		fetchTime = System.currentTimeMillis() - lastConnection;
		configSaveThread.run();
	}

	public static File getFile(String name) {
		return new File(new File("./").getAbsolutePath(), name);
	}

	public static InputStream getResource(String name) {
		final InputStream x = BannerMaker.class.getResourceAsStream(name);
		if (x != null) {
			return x;
		}
		return BannerMaker.class.getClassLoader().getResourceAsStream(name);
	}

	private static void loadFonts() {
		System.out.println("Loading fonts...");
		final File fontDir = getFile("fonts");
		if (!fontDir.exists()) {
			fontDir.mkdirs();
		} else if (fontDir.isDirectory()) {
			for (final File file : fontDir.listFiles()) {
				if (!file.getName().endsWith(".ttf")) {
					continue;
				}
				try {
					final String name = SwingUtil.registerFont(file);
					System.out.println("Font '" + file.getName() + "' has been registered as '" + name + "'");
				} catch (final Throwable t) {
					System.out.println("Failed to register font '" + file.getName() + "'!");
					t.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) throws Throwable {
		System.out.println("Loading libraries...");
		Class.forName("org.apache.commons.io.IOUtils");
		Class.forName("com.google.gson.Gson");
		setupConfigThread();
		try {
			final File file = getFile("/config.properties");
			if (!file.exists()) {
				IOUtils.copy(getResource("config.properties"), new FileOutputStream(file));
			}
			config.load(new FileReader(getFile("/config.properties")));
			totalRequests = Integer.parseInt(config.getProperty("api-requests"));
			Runtime.getRuntime().addShutdownHook(configSaveThread);
			Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Shutting down server...")));
			Thread main = Thread.currentThread();
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					main.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}));
		} catch (final Throwable t) {
			System.out.println("Failed to load configuration");
			t.printStackTrace();
		}
		int port = 8080;
		if (args.length > 0 && Utils.isInteger(args[0])) {
			port = Integer.parseInt(args[0]);
			System.out.println("Using custom port " + args[0]);
		}
		loadFonts();
		System.out.println("Starting server on port " + port + "...");
//		new BannerMaker(port).disableLogging().start();
		BannerMakerServer server = new BannerMakerServer(port);
		/*
		 * Disable errors on console
		 */
		server.setUncaughtExceptionHandler((a,b)->b.printStackTrace());
		
		server.startAsynchronously();
		System.out.println("Server has been started");
		final Scanner scan = new Scanner(System.in);
		while (Thread.currentThread().isAlive()) {
			final String line = scan.nextLine().toLowerCase();
			dispatchCommand(line);
		}
		scan.close();
	}

	public static RectBkg noResource(RectBkg img, String font) {
		img.addText("No Resource", new Font(font, Font.BOLD, 24), 25, 55);
		return img;
	}

	public static RectBkg process(RectBkg img, SpigotResource resource, String headerFont, String fontName,
			Color color) {
		resource.getRating().average = roundToHalf(resource.getRating().average);
		img.setCountBoth(false);
		if (color != null) {
			img.setRate(color);
		}
		if (fontName == null) {
			fontName = headerFont;
		}
		if (resource.getIcon().data.isEmpty()) {
			if (resource.isPremium()) {
				img.addText(resource.getName(), new Font(headerFont, Font.BOLD, 13), 15, 20);
				img.addText("by " + resource.getAuthor().name, new Font(fontName, 0, 11), 15, 35);
				img.addText(resource.getRating().average + "/" + resource.getRating().count + " Ratings",
						new Font(fontName, 0, 11), 95, 50);
				// RATINGS
				img.setRatings(15, 39, 75, 13, resource.getRating().average);
				img.addText("➜ ", new Font("?", 0, 11), 15, 65);
				img.addText(resource.getDownloads() + " Downloads", new Font(fontName, 0, 11), 30, 65);
				img.addText("❖ ", new Font("?", 0, 11), 15, 80);
				img.addText(
						resource.getPrice() + " " + (resource.getCurrency() == null ? "USD" : resource.getCurrency()),
						new Font(fontName, Font.BOLD, 13), 30, 80);
			} else {
				img.addText(resource.getName(), new Font(headerFont, Font.BOLD, 13), 15, 25);
				img.addText("by " + resource.getAuthor().name, new Font(fontName, 0, 11), 15, 40);
				img.addText(resource.getRating().average + "/" + resource.getRating().count + " Ratings",
						new Font(fontName, 0, 11), 95, 55);
				// RATINGS
				img.setRatings(15, 44, 75, 13, resource.getRating().average);
				img.addText("➜ ", new Font("?", 0, 11), 15, 70);
				img.addText(resource.getDownloads() + " Downloads", new Font(fontName, 0, 11), 30, 70);
			}
		} else {
			img.addImage(resource.getIcon().getResourceIcon(), 15, 15, 60, 60);
			if (resource.isPremium()) {
				img.addText(resource.getName(), new Font(headerFont, Font.BOLD, 13), 90, 20);
				img.addText("by " + resource.getAuthor().name, new Font(fontName, 0, 11), 90, 35);
				img.addText(resource.getRating().average + "/" + resource.getRating().count + " Ratings",
						new Font(fontName, 0, 11), 170, 50);
				img.addText(resource.getDownloads() + " Downloads", new Font(fontName, 0, 11), 105, 65);
				img.addText(
						resource.getPrice() + " " + (resource.getCurrency() == null ? "USD" : resource.getCurrency()),
						new Font(fontName, Font.BOLD, 13), 105, 80);
				// RATINGS
				img.setRatings(90, 39, 75, 13, resource.getRating().average);
				img.addText("➜ ", new Font("?", 0, 11), 90, 65);
				img.addText("❖ ", new Font("?", 0, 11), 90, 80);
			} else {
				img.addText(resource.getName(), new Font(headerFont, Font.BOLD, 13), 90, 25);
				img.addText("by " + resource.getAuthor().name, new Font(fontName, 0, 11), 90, 40);
				img.addText(resource.getRating().average + "/" + resource.getRating().count + " Ratings",
						new Font(fontName, 0, 11), 170, 55);
				img.addText(resource.getDownloads() + " Downloads", new Font(fontName, 0, 11), 105, 70);
				// RATINGS
				img.setRatings(90, 44, 75, 13, resource.getRating().average);
				img.addText("➜ ", new Font("?", 0, 11), 90, 70);
			}
		}
		return img;
	}

	public static RectBkg process(RectBkg img, String font, Color color) {
		if (color != null) {
			img.setRate(color);
		}
		img.addText("And More...", new Font(font, Font.BOLD, 24), 35, 55);
		return img;
	}

	public static double roundToHalf(double d) {
		return Math.round(d * 2) / 2.0;
	}

	private static void setupConfigThread() {
		configSaveThread = new Thread(() -> {
			final Gson gson = new Gson();
			try {
				config.setProperty("api-requests", totalRequests + "");
				config.setProperty("resources-requests", gson.toJson(cachedResources));
				config.setProperty("authors-requests", gson.toJson(cachedAuthors));
				config.store(new FileWriter(getFile("/config.properties")), "Resource Banner v1.6.7 by BlueObsidian");
			} catch (final IOException e) {
				e.printStackTrace();
			}
		});
		System.setProperty("http.agent", "");
	}

	public static RectBkg showStatsImage(RectBkg img, String font, String subFont) {
		img.setSize(img.getWidth(), 160);
		final Runtime runtime = Runtime.getRuntime();
		double maxMem = runtime.maxMemory() / (1024.0 * 1024.0);
		double totalMem = runtime.totalMemory() / (1024.0 * 1024.0);
		double usedMem = (runtime.totalMemory() - runtime.freeMemory()) / (1024.0 * 1024.0);
		double freeMem = runtime.freeMemory() / (1024.0 * 1024.0);
		totalMem = Math.floor(totalMem * 100) / 100;
		usedMem = Math.floor(usedMem * 100) / 100;
		maxMem = Math.floor(maxMem * 100) / 100;
		freeMem = Math.floor(freeMem * 100) / 100;
		img.addText("Resource Banner Status", new Font(font, Font.BOLD, 13), 15, 20);
		img.addText("Created by BlueObsidian", new Font(subFont, Font.ITALIC, 12), 15, 35);
		img.addText("Fetch Time: " + fetchTime + " ms", new Font(subFont, 0, 11), 15, 50);
		img.addText("Uptime: " + time(BannerMaker.runtime.getUptime()), new Font(subFont, 0, 11), 15, 65);
		img.addText("Max Memory: " + maxMem + "MB", new Font(subFont, 0, 11), 15, 80);
		img.addText("Total Memory: " + totalMem + "MB", new Font(subFont, 0, 11), 15, 95);
		img.addText("Free Memory: " + freeMem + "MB", new Font(subFont, 0, 11), 15, 110);
		img.addText("Used Memory: " + usedMem + "MB", new Font(subFont, 0, 11), 15, 125);
		img.addText("API requests: " + totalRequests, new Font(subFont, 0, 11), 15, 140);
		return img;
	}

	public static String time(long millis) {
		final long hours = TimeUnit.MILLISECONDS.toHours(millis);
		final long minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
				- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
		final long seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
				- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
		String b = seconds + " secs";
		if (minutes > 0) {
			b = minutes + " mins " + b;
		}
		if (hours > 0) {
			b = hours + " hours " + b;
		}
		return b;
	}

	private static final Gson prettyGsonBuilder = new GsonBuilder().setPrettyPrinting().create();

	private static final SpigotResourceHandler spigotResourceHandler = new SpigotResourceHandler();

//	private final String[] supportedTypes = { "png", "jpg", "jpeg", "webp" };

	public BannerMaker() {
	}
	
	public static Gson getGson() {
		return prettyGsonBuilder;
	}

	public static void addBigText(RectBkg img, String text, String font) {
		img.addText(text, new Font(font, Font.BOLD, 24), 25, 55);
	}

	public static SpigotResourceHandler getSpigotResourceHandler() {
		return spigotResourceHandler;
	}
	public static void setLastConnection(long con) {
		lastConnection = con;
	}
	/*@Override
	public void handle(OutputStream out, BufferedReader reader, Socket socket, String[] path, Properties props,
			Properties browser) throws Throwable {
		final Header header = new Header();
		totalRequests++;
		lastConnection = System.currentTimeMillis();
		header.fields.put(HttpField.CacheControl, "private, no-store, no-cache, must-revalidate");
		header.fields.put(HttpField.Pragma, "no-cache");
		header.fields.put(HttpField.Date, new Date());
		header.fields.put(HttpField.Expires, new Date());
		String fontName = "?";
		String subFont = null;
		if (props.containsKey("font")) {
			fontName = props.getProperty("font");
		}
		if (props.containsKey("subfont")) {
			subFont = props.getProperty("subfont");
		}
		Boolean bright = null;
		if (props.containsKey("bright")) {
			bright = Boolean.parseBoolean(props.getProperty("bright"));
		}
		final int defaultType = 0;
		String format = supportedTypes[defaultType];
		if (path.length > 0) {
			final String[] f = path[path.length - 1].split("\\.", 2);
			if (f.length >= 2) {
				format = f[1].toLowerCase();
				path[path.length - 1] = f[0];
				if (!Arrays.asList(supportedTypes).contains(format)) {
					format = supportedTypes[defaultType];
				}
			}
		}
		header.fields.put(HttpField.ContentType, "image/" + format);
		/*
		 * Global queries
		int width = -1;
		int sizeLimit = 6;
		Sort.SortType sortBy = null;
		int truncate = -1;
		Sort.SortDirection sortOrder = null;
		if (props.containsKey("sort")) {
			sortBy = Sort.SortType._valueOf(props.getProperty("sort"));
		}
		if (props.containsKey("order")) {
			sortOrder = Sort.SortDirection._valueOf(props.getProperty("order"));
		}
		if (props.containsKey("width") && Utils.isInteger(props.getProperty("width"))) {
			width = Integer.parseInt(props.getProperty("width"));
		}
		if (props.containsKey("truncate") && Utils.isInteger(props.getProperty("truncate"))) {
			truncate = Integer.parseInt(props.getProperty("truncate"));
		}
		Color defColor = null;
		if (props.containsKey("color")) {
			defColor = ImageUtil.hexToRgb(props.getProperty("color"));
		} else if (props.containsKey("nicecolor") && Boolean.getBoolean(props.getProperty("nicecolor"))) {
			defColor = ImageUtil.getNiceColor();
		}
		if (props.containsKey("size") && Utils.isInteger(props.getProperty("size"))) {
			sizeLimit = Integer.parseInt(props.getProperty("size"));
		}
		boolean rounded = true;
		if (props.containsKey("type")) {
			if (props.getProperty("type").equalsIgnoreCase("flat")) {
				rounded = false;
			}
		}
		boolean rainbow = false;
		if (props.containsKey("rainbow")) {
			rainbow = Boolean.parseBoolean(props.getProperty("rainbow"));
			if (rainbow) {
				header.fields.put(HttpField.ContentType, ContentType.ImageGIF);
			}
		}
		// TAK SEMUDAH ITU FERGUSO >:)
		if (sizeLimit > 120) {
			sizeLimit = 120;
		}
		//
		try {
			if (path.length > 0) {
				if (path[0].equalsIgnoreCase("favicon")) {
					header.send(out);
					IOUtils.copy(getResource("icon.ico"), out);
					done();
					return;
				}
				if (path[0].equalsIgnoreCase("marquee")) {
					String text = props.getProperty("text");
					// int size = sizeLimit;
					if (!props.containsKey("size"))
						sizeLimit = 50;
					int speed = 100;
					try {
						speed = Integer.parseInt(props.getProperty("speed"));
					} catch (final Exception e) {
					}
					if (text == null) {
						if (path.length > 1) {
							text = path[1];
						} else {
							text = "You must define the text query before using this. ";
						}
					}
					header.send(out);
					final Marquee mar = new Marquee(text, sizeLimit);
					final GifSequenceWriter writer = new GifSequenceWriter(out, BufferedImage.TYPE_INT_ARGB, speed,
							true);
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
					out.close();
					done();
					return;
				}
				if (sizeLimit > 50)
					sizeLimit = 50;
				if (path[0].equalsIgnoreCase("data")) {
					header.fields.put(HttpField.ContentType, ContentType.ApplicationJSON);
					final Map<String, Object> data = new HashMap<>();
					data.put("resources", cachedResources);
					data.put("authors", cachedAuthors);
					final HashSet<String> fonts = new HashSet<>();
					for (final Font f : SwingUtil.FONTS) {
						fonts.add(f.getName());
					}
					data.put("fonts", fonts);
					header.content = prettyGsonBuilder.toJson(data);
					header.send(out);
					done();
					return;
				}
				if (path[0].equalsIgnoreCase("generator")) {
					header.fields.put(HttpField.ContentType, ContentType.TextHTML);
					header.send(out);
					// Was thrown out of the jar
					// IOUtils.copy(getResource("generator.html"), out);
					IOUtils.copy(new FileInputStream(getFile("generator.html")), out);
					done();
					return;
				}
				if (path[0].equalsIgnoreCase("fonts")) {
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
					header.send(out);
					ImageIO.write(SwingUtil.convert(panel), format, out);
					done();
					return;
				}
				if (path[0].equalsIgnoreCase("random")) {
					if (path.length > 1) {
						final String authorID = path[1];
						final List<SpigotResource> res = getSpigotResourceHandler().byAuthor(authorID, 10, sortBy,
								sortOrder);
						final List<RectBkg> imgs = new ArrayList<>();
						if (res.isEmpty()) {
							imgs.add(noResource(new RectBkg(bright), fontName));
						} else {
							final RectBkg rect = new RectBkg(bright);
							rect.setRounded(rounded);
							imgs.add(process(rect, res.get(ImageUtil.random.nextInt(res.size())), fontName, subFont,
									defColor));
						}
						if (rainbow) {
							header.send(out);
							final GifSequenceWriter writer = new GifSequenceWriter(out, BufferedImage.TYPE_INT_ARGB, 1,
									true);
							for (int i = 0; i < GIF_FRAMES; i++) {
								writer.writeToSequence(SwingUtil.convert(SwingUtil.collect(imgs, width)));
								imgs.forEach(a -> {
									a.nextHUE();
								});
							}
							writer.close();
							out.close();
						} else {
							final JPanel j = SwingUtil.collect(imgs, width);
							header.send(out);
							ImageIO.write(SwingUtil.convert(j), format, out);
						}
						done();
						return;
					}
				}
				if (path[0].equalsIgnoreCase("author")) {
					if (path.length > 1) {
						final String authorID = path[1];
						if (!Utils.isInteger(authorID)) {
							header.send(out);
							ImageIO.write(MemeGenerator.generate("Please use Author ID instead of Author Name", 13),
									format, out);
							done();
							return;
						}
						final List<SpigotResource> res = getSpigotResourceHandler().byAuthor(authorID, sizeLimit,
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
								imgs.add(process(rect, r, fontName, subFont, defColor));
								resourcesLimit--;
							}
						}
						if (imgs.isEmpty()) {
							imgs.add(noResource(new RectBkg(bright), fontName));
						}
						cachedAuthors.add(authorID);
						if (rainbow) {
							header.send(out);
							final GifSequenceWriter writer = new GifSequenceWriter(out, BufferedImage.TYPE_INT_ARGB, 1,
									true);
							for (int i = 0; i < GIF_FRAMES; i++) {
								writer.writeToSequence(SwingUtil.convert(SwingUtil.collect(imgs, width)));
								imgs.forEach(a -> {
									a.nextHUE();
								});
							}
							writer.close();
							out.close();
						} else {
							final JPanel j = SwingUtil.collect(imgs, width);
							header.send(out);
							ImageIO.write(SwingUtil.convert(j), format, out);
						}
						done();
						return;
					}
				}
				if (path[0].equalsIgnoreCase("meme")) {
					final File[] files = getFile("memes").listFiles();
					final File meme = files[ImageUtil.random.nextInt(files.length)];
					final String[] split = meme.getName().split("\\.");
					format = split[split.length - 1];
					header.fields.put(HttpField.ContentType, "img/" + format);
					header.send(out);
					IOUtils.copy(new FileInputStream(meme), out);
					done();
					return;
				}
				if (path[0].equalsIgnoreCase("spiget")) {
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
					header.send(out);
					if (rainbow) {
						final GifSequenceWriter writer = new GifSequenceWriter(out, BufferedImage.TYPE_INT_ARGB, 1,
								true);
						final int max = HUE.maxHUE(img.rate);
						for (int i = 0; i < max; i++) {
							writer.writeToSequence(SwingUtil.convert(img));
							img.nextHUE();
						}
						writer.close();
						out.close();
					} else {
						ImageIO.write(SwingUtil.convert(img), format, out);
					}
					done();
					return;
				}
				if (path[0].equalsIgnoreCase("stats") || path[0].equalsIgnoreCase("status")
						|| path[0].equalsIgnoreCase("stat")) {
					final RectBkg img = new RectBkg(bright);
					showStatsImage(img, fontName, subFont);
					if (width > 0) {
						img.setSize(width, img.getHeight());
					}
					img.setRounded(rounded);
					header.send(out);
					if (rainbow) {
						final GifSequenceWriter writer = new GifSequenceWriter(out, BufferedImage.TYPE_INT_ARGB, 1,
								true);
						final int max = HUE.maxHUE(img.rate);
						for (int i = 0; i < max; i++) {
							writer.writeToSequence(SwingUtil.convert(img));
							img.nextHUE();
						}
						writer.close();
						out.close();
					} else {
						ImageIO.write(SwingUtil.convert(img), format, out);
					}
					done();
					return;
				}
				if (path[0].equalsIgnoreCase("resource")) {
					if (path.length > 1) {
						final String resourceID = path[1];
						if (!Utils.isInteger(resourceID)) {
							header.send(out);
							ImageIO.write(MemeGenerator.generate("Please use Resource ID instead of Resource Name", 11),
									format, out);
							done();
							return;
						}
						final SpigotResource resource = getSpigotResourceHandler().getResource(resourceID);
						if (truncate > 0) {
							resource.setName(
									resource.getName().substring(0, Math.min(resource.getName().length(), truncate)));
						}
						final RectBkg img = new RectBkg(bright);
						img.setRounded(rounded);
						if (resource != null) {
							process(img, resource, fontName, subFont, defColor);
							cachedResources.add(resourceID);
						} else {
							addBigText(img, "Not Found :/", fontName);
						}
						if (width > 0) {
							img.setSize(width, img.getHeight());
						}
						header.send(out);
						if (rainbow) {
							final GifSequenceWriter writer = new GifSequenceWriter(out, BufferedImage.TYPE_INT_ARGB, 1,
									true);
							final int max = HUE.maxHUE(img.rate);
							for (int i = 0; i < max; i++) {
								writer.writeToSequence(SwingUtil.convert(img));
								img.nextHUE();
							}
							writer.close();
							out.close();
						} else {
							ImageIO.write(SwingUtil.convert(img), format, out);
						}
						done();
						return;
					}
				}
			}
		} catch (final RuntimeException io) {
			final RectBkg img = new RectBkg(bright);
			if (defColor != null) {
				img.setRate(defColor);
			}
			addBigText(img, "Not Found :/", fontName);
			header.send(out);
			ImageIO.write(SwingUtil.convert(img), format, out);
			done();
			throw io;
		} catch (final Throwable t) {
			t.printStackTrace();
			final RectBkg img = new RectBkg(bright);
			if (defColor != null) {
				img.setRate(defColor);
			}
			addBigText(img, t.toString(), fontName);
			header.send(out);
			ImageIO.write(SwingUtil.convert(img), format, out);
			done();
			throw new RuntimeException(t);
		}
		header.content = "<html><head><title>Unknown Route</title></head><body>Redirecting... <br>if it doesn't go to <br>"
				+ "https://www.spigotmc.org/threads/resource-banner-generate-your-own-banner.346493/"
				+ "</body><script>window.location = '" + "https://www.spigotmc.org/threads/346493/"
				+ "'</script><html>";
		header.fields.put(HttpField.ContentType, ContentType.TextHTML);
		header.fields.put(HttpField.Location, "https://www.spigotmc.org/threads/346493/");
		header.send(out);
		done();
	}
*/
}
