package thito.resourcebanner;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.io.IOUtils;
import org.spookit.betty.ContentType;
import org.spookit.betty.Header;
import org.spookit.betty.HttpField;
import org.spookit.betty.WebServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BannerMaker extends WebServer {

	public static final String HELP_THREAD = "https://www.spigotmc.org/threads/resource-banner-generate-your-own-banner.346493/"; 
	static final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
	static final Set<String> resources = new HashSet<>();
	static final Set<String> authors = new HashSet<>();
	static final Properties config = new Properties();
	static final String defaultFont = "?";
	static final Thread SAVE;
	static String[] supportedTypes = { "png", "jpg", "jpeg", "webp" };
	static int defaultType = 0;
	static long REQUESTS = 0;
	static long past = 0;
	static long performance;
	static RuntimeMXBean b = ManagementFactory.getRuntimeMXBean();

	static {
		SAVE = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					config.setProperty("api-requests", REQUESTS + "");
					config.setProperty("resources-requests", SpigotResource.gson.toJson(resources));
					config.setProperty("authors-requests", SpigotResource.gson.toJson(authors));
					config.store(new FileWriter(getFile("/config.properties")), "Resource Banner v1.6.7 by BlueObsidian");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		System.setProperty("http.agent", "");
	}

	public static RoundRectBkg big(RoundRectBkg img, String text, String font) {
		img.addText(text, new Font(font, Font.BOLD, 24), 25, 55);
		return img;
	}

	public static void dispatchCommand(String cmd) {
		if (cmd.startsWith("exit")) {
			System.exit(0);
			return;
		}
		if (cmd.startsWith("save")) {
			System.out.println("Saving configuration...");
			SAVE.run();
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
		performance = System.currentTimeMillis() - past;
		SAVE.run();
	}

	public static File getFile(String name) {
		return new File(new File("./").getAbsolutePath(), name);
	}

	public static InputStream getResource(String name) {
		InputStream x = BannerMaker.class.getResourceAsStream(name);
		if (x != null)
			return x;
		return BannerMaker.class.getClassLoader().getResourceAsStream(name);
	}

	public static void main(String[] args) throws Throwable {
		System.out.println("Loading libraries...");
		Class.forName("org.apache.commons.io.IOUtils");
		Class.forName("com.google.gson.Gson");
		try {
			File file = getFile("/config.properties");
			if (!file.exists()) {
				IOUtils.copy(getResource("config.properties"), new FileOutputStream(file));
			}
			config.load(new FileReader(getFile("/config.properties")));
			REQUESTS = Integer.parseInt(config.getProperty("api-requests"));
			Runtime.getRuntime().addShutdownHook(SAVE);
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					System.out.println("Shutting down server...");
				}
			});
		} catch (Throwable t) {
			System.out.println("Failed to load configuration");
			t.printStackTrace();
		} finally {
		}
		int port = 8080;
		if (args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (Throwable t) {
			}
		}
		System.out.println("Loading fonts...");
		File fontDir = getFile("fonts");
		if (!fontDir.exists()) {
			fontDir.mkdirs();
		} else if (fontDir.isDirectory()) {
			for (File f : fontDir.listFiles()) {
				if (f.getName().endsWith(".ttf")) {
					try {
						String name = SwingUtil.registerFont(f);
						System.out.println("Font '" + f.getName() + "' has been registered as '" + name + "'");
					} catch (Throwable t) {
						System.out.println("Failed to register font '" + f.getName() + "'!");
						t.printStackTrace();
					}
				}
			}
		}
		System.out.println("Starting server on port " + port + "...");
		new BannerMaker(port).disableLogging().start();
		System.out.println("Server has been started");
		Scanner scan = new Scanner(System.in);
		while (Thread.currentThread().isAlive()) {
			String line = scan.nextLine().toLowerCase();
			dispatchCommand(line);
		}
		scan.close();
	}

	public static RoundRectBkg noResource(RoundRectBkg img, String font) {
		img.addText("No Resource", new Font(font, Font.BOLD, 24), 25, 55);
		return img;
	}

	public static RoundRectBkg process(RoundRectBkg img, SpigotResource resource, String headerFont, String fontName,
			Color col) {
		resource.rating.average = roundToHalf(resource.rating.average);
		img.countBoth = false;
		if (col != null)
			img.rate = col;
		if (fontName == null)
			fontName = headerFont;
		if (resource.icon.data.isEmpty()) {
			if (resource.premium) {
				img.addText(resource.name, new Font(headerFont, Font.BOLD, 13), 15, 20);
				img.addText("by " + resource.getAuthor().name, new Font(fontName, 0, 11), 15, 35);
				img.addText(resource.rating.average + "/" + resource.rating.count + " Ratings",
						new Font(fontName, 0, 11), 95, 50);
				// RATINGS
				img.setRatings(15, 39, 75, 13, resource.rating.average);
				img.addText("➜ ", new Font("?", 0, 11), 15, 65);
				img.addText(resource.downloads + " Downloads", new Font(fontName, 0, 11), 30, 65);
				img.addText("❖ ", new Font("?", 0, 11), 15, 80);
				img.addText(resource.price + " " + (resource.currency == null ? "USD" : resource.currency),
						new Font(fontName, Font.BOLD, 13), 30, 80);
			} else {
				img.addText(resource.name, new Font(headerFont, Font.BOLD, 13), 15, 25);
				img.addText("by " + resource.getAuthor().name, new Font(fontName, 0, 11), 15, 40);
				img.addText(resource.rating.average + "/" + resource.rating.count + " Ratings",
						new Font(fontName, 0, 11), 95, 55);
				// RATINGS
				img.setRatings(15, 44, 75, 13, resource.rating.average);
				img.addText("➜ ", new Font("?", 0, 11), 15, 70);
				img.addText(resource.downloads + " Downloads", new Font(fontName, 0, 11), 30, 70);
			}
		} else {

			img.addImage(resource.icon.get(), 15, 15, 60, 60);
			if (resource.premium) {
				img.addText(resource.name, new Font(headerFont, Font.BOLD, 13), 90, 20);
				img.addText("by " + resource.getAuthor().name, new Font(fontName, 0, 11), 90, 35);
				img.addText(resource.rating.average + "/" + resource.rating.count + " Ratings",
						new Font(fontName, 0, 11), 170, 50);
				img.addText(resource.downloads + " Downloads", new Font(fontName, 0, 11), 105, 65);
				img.addText(resource.price + " " + (resource.currency == null ? "USD" : resource.currency),
						new Font(fontName, Font.BOLD, 13), 105, 80);
				// RATINGS
				img.setRatings(90, 39, 75, 13, resource.rating.average);
				img.addText("➜ ", new Font("?", 0, 11), 90, 65);
				img.addText("❖ ", new Font("?", 0, 11), 90, 80);
			} else {
				img.addText(resource.name, new Font(headerFont, Font.BOLD, 13), 90, 25);
				img.addText("by " + resource.getAuthor().name, new Font(fontName, 0, 11), 90, 40);
				img.addText(resource.rating.average + "/" + resource.rating.count + " Ratings",
						new Font(fontName, 0, 11), 170, 55);
				img.addText(resource.downloads + " Downloads", new Font(fontName, 0, 11), 105, 70);
				// RATINGS
				img.setRatings(90, 44, 75, 13, resource.rating.average);
				img.addText("➜ ", new Font("?", 0, 11), 90, 70);
			}
		}
		return img;
	}

	public static RoundRectBkg process(RoundRectBkg img, String font, Color c) {
		if (c != null)
			img.rate = c;
		img.addText("And More...", new Font(font, Font.BOLD, 24), 35, 55);
		return img;
	}

	public static <T extends Enum<T>> T random(Class<T> e) {
		return e.getEnumConstants()[ImageUtil.random.nextInt(e.getEnumConstants().length)];
	}

	public static double roundToHalf(double d) {
		return Math.round(d * 2) / 2.0;
	}

	public static RoundRectBkg stats(RoundRectBkg img, String font, String subFont) {
		img.setSize(img.getWidth(), 160);
		Runtime runtime = Runtime.getRuntime();
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
		img.addText("Fetch Time: " + performance + " ms", new Font(subFont, 0, 11), 15, 50);
		img.addText("Uptime: " + time(b.getUptime()), new Font(subFont, 0, 11), 15, 65);
		img.addText("Max Memory: " + maxMem + "MB", new Font(subFont, 0, 11), 15, 80);
		img.addText("Total Memory: " + totalMem + "MB", new Font(subFont, 0, 11), 15, 95);
		img.addText("Free Memory: " + freeMem + "MB", new Font(subFont, 0, 11), 15, 110);
		img.addText("Used Memory: " + usedMem + "MB", new Font(subFont, 0, 11), 15, 125);
		img.addText("API requests: " + REQUESTS, new Font(subFont, 0, 11), 15, 140);
		return img;
	}

	public static String time(long millis) {
		long hours = TimeUnit.MILLISECONDS.toHours(millis);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
				- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
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

	public BannerMaker(int port) {
		super(port);
	}

	@Override
	public void handle(OutputStream out, BufferedReader reader, Socket socket, String[] path, Properties props,
			Properties browser) throws Throwable {
		/*
		 * API handler
		 */
		Header header = new Header();
		REQUESTS++;
		past = System.currentTimeMillis();
		header.fields.put(HttpField.CacheControl, "private, no-store, no-cache, must-revalidate");
		header.fields.put(HttpField.Pragma, "no-cache");
		header.fields.put(HttpField.Date, new Date());
		header.fields.put(HttpField.Expires, new Date());
		String fontName = defaultFont;
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
		String format = supportedTypes[defaultType];
		if (path.length > 0) {
			String f[] = path[path.length - 1].split("\\.", 2);
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
		 */
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
		if (props.containsKey("width")) {
			try {
				width = Integer.parseInt(props.getProperty("width"));
			} catch (Throwable t) {
			}
		}
		if (props.containsKey("truncate")) {
			try {
				truncate = Integer.parseInt(props.getProperty("truncate"));
			} catch (Throwable t) {
			}
		}
		Color defColor = null;
		if (props.containsKey("color")) {
			defColor = ImageUtil.hex2Rgb(props.getProperty("color"));
		} else if (props.containsKey("nicecolor") && Boolean.getBoolean(props.getProperty("nicecolor"))) {
			defColor = ImageUtil.niceColor();
		}
		if (props.containsKey("size")) {
			try {
				sizeLimit = Integer.parseInt(props.getProperty("size"));
			} catch (Throwable t) {
			}
		}
		// TAK SEMUDAH ITU FERGUSO >:)
		if (sizeLimit > 50)
			sizeLimit = 50;
		//
		try {
			if (path.length > 0) {
				if (path[0].equalsIgnoreCase("favicon")) {
					header.send(out);
					IOUtils.copy(getResource("icon.ico"), out);
					done();
					return;
				}
				if (path[0].equalsIgnoreCase("data")) {
					header.fields.put(HttpField.ContentType, ContentType.ApplicationJSON);
					Map<String,Object> data = new HashMap<>();
					data.put("resources", resources);
					data.put("authors", authors);
					HashSet<String> fonts = new HashSet<>();
					for (Font f : SwingUtil.FONTS) {
						fonts.add(f.getName());
					}
					data.put("fonts", fonts);
					header.content = prettyGson.toJson(data);
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
					GridLayout layout = (new GridLayout(SwingUtil.FONTS.size(), 1));
					JPanel panel = new JPanel(layout);
					int mw = 0;
					for (Font f : SwingUtil.FONTS) {
						JLabel label = new JLabel(f.getName());
						label.setFont(new Font(f.getName(), f.getStyle(), 30));
						int w = label.getFontMetrics(label.getFont()).stringWidth(label.getText()) + 50;
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
						String authorID = path[1];
						ArrayList<SpigotResource> res = SpigotResource.byAuthor(authorID, 10, sortBy,sortOrder);
						ArrayList<RoundRectBkg> imgs = new ArrayList<>();
						if (res.isEmpty()) {
							imgs.add(noResource(new RoundRectBkg(bright), fontName));
						} else
							imgs.add(process(new RoundRectBkg(bright), res.get(ImageUtil.random.nextInt(res.size())),
									fontName, subFont, defColor));
						JPanel j = SwingUtil.collect(imgs, width);
						header.send(out);
						ImageIO.write(SwingUtil.convert(j), format, out);
						done();
						return;
					}
				}
				if (path[0].equalsIgnoreCase("author")) {
					if (path.length > 1) {
						String authorID = path[1];
						if (MemeGenerator.areYouKiddingMe(authorID)) {
							header.send(out);
							ImageIO.write(MemeGenerator.generate("Please use Author ID instead of Author Name",13), format, out);
							done();
							return;
						}
						ArrayList<SpigotResource> res = SpigotResource.byAuthor(authorID, sizeLimit, sortBy, sortOrder);
						ArrayList<RoundRectBkg> imgs = new ArrayList<>();
						if (sizeLimit > 0)
							if (res.size() > sizeLimit && sizeLimit > 1) {
								for (int i = 0; i < sizeLimit - 1; i++) {
									imgs.add(
											process(new RoundRectBkg(bright), res.get(i), fontName, subFont, defColor));
								}
								imgs.add(process(new RoundRectBkg(bright), fontName, defColor));
							} else if (sizeLimit == 1) {
								for (SpigotResource r : res) {
									imgs.add(process(new RoundRectBkg(bright), r, fontName, subFont, defColor));
									break;
								}
							} else {
								for (SpigotResource r : res) {
									imgs.add(process(new RoundRectBkg(bright), r, fontName, subFont, defColor));
								}

							}
						if (imgs.isEmpty()) {
							imgs.add(noResource(new RoundRectBkg(bright), fontName));
						}
						authors.add(authorID);
						JPanel j = SwingUtil.collect(imgs, width);
						header.send(out);
						ImageIO.write(SwingUtil.convert(j), format, out);
						done();
						return;
					}
				}
				if (path[0].equalsIgnoreCase("meme")) {
					File[] files = getFile("memes").listFiles();
					File meme = files[ImageUtil.random.nextInt(files.length)];
					String[] split = meme.getName().split("\\.");
					format = split[split.length-1];
					header.fields.put(HttpField.ContentType, "img/"+format);
					header.send(out);
					IOUtils.copy(new FileInputStream(meme), out);
					done();
					return;
				}
				if (path[0].equalsIgnoreCase("spiget")) {
					SpigetStatus stats = SpigetStatus.getStatus();
					RoundRectBkg img = new RoundRectBkg(bright);
					if (defColor != null)
						img.rate = defColor;
					img.addText("API Status - Spiget", new Font(fontName, Font.BOLD, 13), 15, 20);
					img.addText("Server Name: " + stats.status.server.name, new Font(subFont, 0, 11), 15, 35);
					img.addText("Server Mode: " + stats.status.server.mode, new Font(subFont, 0, 11), 15, 50);
					img.addText("Resources: " + stats.stats.resources, new Font(subFont, 0, 11), 15, 65);
					img.addText("Authors: " + stats.stats.authors, new Font(subFont, 0, 11), 15, 80);
					if (width > 0) {
						img.setSize(width, img.getHeight());
					}
					header.send(out);
					ImageIO.write(SwingUtil.convert(img), format, out);
					done();
					return;
				}
				if (path[0].equalsIgnoreCase("stats") || path[0].equalsIgnoreCase("status")
						|| path[0].equalsIgnoreCase("stat")) {
					RoundRectBkg img = new RoundRectBkg(bright);
					stats(img, fontName, subFont);
					if (width > 0) {
						img.setSize(width, img.getHeight());
					}
					header.send(out);
					ImageIO.write(SwingUtil.convert(img), format, out);
					done();
					return;
				}
				if (path[0].equalsIgnoreCase("debug")) {
					if (path.length > 1) {
						Object throwable = null;
						try {
							throwable = Class.forName(path[1]).newInstance();
						} catch (Throwable t) {
						}
						throw (Throwable)throwable;
					}
					throw new Error("Debug");
				}
				if (path[0].equalsIgnoreCase("resource")) {
					if (path.length > 1) {
						String resourceID = path[1];
						if (MemeGenerator.areYouKiddingMe(resourceID)) {
							header.send(out);
							ImageIO.write(MemeGenerator.generate("Please use Resource ID instead of Resource Name",11), format, out);
							done();
							return;
						}
						SpigotResource resource = SpigotResource.getResource(resourceID);
						if (truncate > 0) {
							resource.name = resource.name.substring(0, Math.min(resource.name.length(), truncate));
						}
						RoundRectBkg img = new RoundRectBkg(bright);
						if (resource != null) {
							process(img, resource, fontName, subFont, defColor);
							resources.add(resourceID);
						} else {
							big(img, "Not Found :/", fontName);
						}
						if (width > 0) {
							img.setSize(width, img.getHeight());
						}
						header.send(out);
						ImageIO.write(SwingUtil.convert(img), format, out);
						done();
						return;
					}
				}
			}
		} catch (RuntimeException io) {
			io.printStackTrace();
			RoundRectBkg img = new RoundRectBkg(bright);
			if (defColor != null)
				img.rate = defColor;
			big(img, "Not Found :/", fontName);
			header.send(out);
			ImageIO.write(SwingUtil.convert(img), format, out);
			done();
			return;
		} catch (Throwable t) {
			t.printStackTrace();
			RoundRectBkg img = new RoundRectBkg(bright);
			if (defColor != null)
				img.rate = defColor;
			big(img, t.toString(), fontName);
			header.send(out);
			ImageIO.write(SwingUtil.convert(img), format, out);
			done();
			return;
		}
		header.content = "<html><head><title>Unknown Route</title></head><body>Redirecting... <br>if it doesn't go to <br>"+HELP_THREAD+"</body><script>window.location = '"+HELP_THREAD+"'</script><html>";
		header.fields.put(HttpField.ContentType, ContentType.TextHTML);
		header.fields.put(HttpField.Location, HELP_THREAD);
		header.send(out);
		done();
	}

}
