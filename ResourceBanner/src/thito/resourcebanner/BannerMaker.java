package thito.resourcebanner;

import org.apache.commons.io.IOUtils;
import org.spookit.betty.ContentType;
import org.spookit.betty.Header;
import org.spookit.betty.HttpField;
import org.spookit.betty.WebServer;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class BannerMaker extends WebServer {

    public static final Properties config = new Properties();
    public static final String defaultFont = "?";
    static final Thread SAVE;
    public static String[] supportedTypes = {"png", "jpg", "jpeg"};
    public static int defaultType = 0;
    public static long REQUESTS = 0;
    static long past = 0;
    static long performance;
    static RuntimeMXBean b = ManagementFactory.getRuntimeMXBean();

    static {
        SAVE = new Thread(new Runnable() {
            public void run() {
                try {
                    config.setProperty("api-requests", REQUESTS + "");
                    config.store(new FileWriter(getFile("/config.properties")), "Resource Banner v1.2 by BlueObsidian");
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    static {

        System.setProperty("http.agent", "");

    }
    BannerMaker() {
        super(4444);
    }
    BannerMaker(int port) {
        super(port);
    }

    public static File getFile(String name) {
        return new File(new File("./").getAbsolutePath(), name);
    }

    public static void main(String[] args) throws Throwable {
        System.out.println("Loading libraries...");
        Class.forName("org.apache.commons.io.IOUtils");
        Class.forName("com.google.gson.Gson");
        try {
            File file = getFile("/config.properties");
            if(!file.exists()) {
                IOUtils.copy(getResource("config.properties"), new FileOutputStream(file));
            }
            config.load(new FileReader(getFile("/config.properties")));
            REQUESTS = Integer.parseInt(config.getProperty("api-requests"));
            Runtime.getRuntime().addShutdownHook(SAVE);
        } catch(Throwable t) {
            System.out.println("Failed to load configuration");
            t.printStackTrace();
        } finally {
        }
        int port = 8080;
        if(args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch(Throwable t) {
            }
        }
        System.out.println("Loading fonts...");
        File fontDir = getFile("fonts");
        if(!fontDir.exists()) {
            fontDir.mkdirs();
        } else if(fontDir.isDirectory()) {
            for(File f : fontDir.listFiles()) {
                if(f.getName().endsWith(".ttf")) {
                    try {
                        String name =
                                SwingUtil.registerFont(f);
                        System.out.println("Font '" + f.getName() + "' has been registered as '" + name + "'");
                    } catch(Throwable t) {
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
        while(Thread.currentThread().isAlive()) {
            String line = scan.nextLine().toLowerCase();
            dispatchCommand(line);
        }
        scan.close();
    }

    public static void dispatchCommand(String cmd) {
        if(cmd.startsWith("exit")) {
            System.exit(0);
        }
        if(cmd.startsWith("save")) {
            System.out.println("Saving configuration...");
            SAVE.run();
            System.out.println("Done!");
        }
    }

    public static InputStream getResource(String name) {
        InputStream x = BannerMaker.class.getResourceAsStream(name);
        if(x != null) return x;
        return BannerMaker.class.getClassLoader().getResourceAsStream(name);
    }

    public static void done() {
        performance = System.currentTimeMillis() - past;
        SAVE.run();
    }

    public static BevelShape process(BevelShape img, String font, Color c) {
        if(c != null) img.rate = c;
        img.addText("And More...", new Font(font, Font.BOLD, 24), 35, 55);
        return img;
    }

    public static String time(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        String b = seconds + " secs";
        if(minutes > 0) {
            b = minutes + " mins " + b;
        }
        if(hours > 0) {
            b = hours + " hours " + b;
        }
        return b;
    }

    public static BevelShape stats(BevelShape img, String font, String subFont) {
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

    public static BevelShape big(BevelShape img, String text, String font) {
        img.addText(text, new Font(font, Font.BOLD, 24), 25, 55);
        return img;
    }

    public static BevelShape noResource(BevelShape img, String font) {
        img.addText("No Resource", new Font(font, Font.BOLD, 24), 25, 55);
        return img;
    }

    public static BevelShape process(BevelShape img, Resource resource, String headerFont, String fontName, Color col) {
        if(col != null) img.rate = col;
        if(fontName == null) fontName = headerFont;
        if(resource.icon.data.isEmpty()) {
            if(resource.premium) {
                img.addText(resource.name, new Font(headerFont, Font.BOLD, 13), 15, 20);
                img.addText("by " + resource.getAuthor().name, new Font(fontName, 0, 11), 15, 35);
                img.addText("★ ", new Font("?", 0, 11), 15, 35);
                img.addText(resource.rating.average + "/" + resource.rating.count + " Ratings", new Font(fontName, 0, 11), 30, 50);
                img.addText("➜ ", new Font("?", 0, 11), 15, 50);
                img.addText(resource.downloads + " Downloads", new Font(fontName, 0, 11), 30, 65);
                img.addText("❖ ", new Font("?", 0, 11), 15, 65);
                img.addText(resource.price + " " + (resource.currency == null ? "USD" : resource.currency), new Font(fontName, Font.BOLD, 13), 30, 80);
            } else {
                img.addText(resource.name, new Font(headerFont, Font.BOLD, 13), 15, 25);
                img.addText("by " + resource.getAuthor().name, new Font(fontName, 0, 11), 15, 40);
                img.addText(resource.rating.average + "/" + resource.rating.count + " Ratings", new Font(fontName, 0, 11), 30, 55);
                img.addText("★ ", new Font("?", 0, 11), 15, 55);
                img.addText("➜ ", new Font("?", 0, 11), 15, 70);
                img.addText(resource.downloads + " Downloads", new Font(fontName, 0, 11), 30, 70);
            }
        } else {
            img.addImage(resource.icon.get(), 15, 15, 60, 60);
            if(resource.premium) {
                img.addText(resource.name, new Font(headerFont, Font.BOLD, 13), 90, 20);
                img.addText("by " + resource.getAuthor().name, new Font(fontName, 0, 11), 90, 35);
                img.addText(resource.rating.average + "/" + resource.rating.count + " Ratings", new Font(fontName, 0, 11), 105, 50);
                img.addText(resource.downloads + " Downloads", new Font(fontName, 0, 11), 105, 65);
                img.addText(resource.price + " " + (resource.currency == null ? "USD" : resource.currency), new Font(fontName, Font.BOLD, 13), 105, 80);
                img.addText("★ ", new Font("?", 0, 11), 15, 50);
                img.addText("➜ ", new Font("?", 0, 11), 15, 65);
                img.addText("❖ ", new Font("?", 0, 11), 15, 80);
            } else {
                img.addText(resource.name, new Font(headerFont, Font.BOLD, 13), 90, 25);
                img.addText("by " + resource.getAuthor().name, new Font(fontName, 0, 11), 90, 40);
                img.addText(resource.rating.average + "/" + resource.rating.count + " Ratings", new Font(fontName, 0, 11), 105, 55);
                img.addText(resource.downloads + " Downloads", new Font(fontName, 0, 11), 105, 70);
                img.addText("★ ", new Font("?", 0, 11), 90, 55);
                img.addText("➜ ", new Font("?", 0, 11), 90, 70);
            }
        }
        return img;
    }

    @Override
    public void handle(OutputStream out, BufferedReader reader, Socket socket, String[] path, Properties props) throws Throwable {
        Header header = new Header();
        REQUESTS++;
        past = System.currentTimeMillis();
        header.fields.put(HttpField.CacheControl, "private, no-store, no-cache, must-revalidate");
        header.fields.put(HttpField.Pragma, "no-cache");
        header.fields.put(HttpField.Date, new Date());
        header.fields.put(HttpField.Expires, new Date());
        String fontName = defaultFont;
        String subFont = null;
        if(props.containsKey("font")) {
            fontName = props.getProperty("font");
        }
        if(props.containsKey("subfont")) {
            subFont = props.getProperty("subfont");
        }
        try {
            if(path.length > 0 && path[0].equalsIgnoreCase("favicon.ico")) {
                header.send(out);
                IOUtils.copy(getResource("icon.ico"), out);
                done();
                return;
            }
        } catch(Throwable t) {
        }
        Boolean bright = null;
        if(props.containsKey("bright")) {
            bright = Boolean.parseBoolean(props.getProperty("bright"));
        }
        String format = supportedTypes[defaultType];
        if(path.length > 0) {
            String f[] = path[path.length - 1].split("\\.");
            if(f.length == 2) {
                format = f[1].toLowerCase();
                path = Arrays.copyOf(path, path.length - 1);
                if(!Arrays.asList(supportedTypes).contains(format)) {
                    format = supportedTypes[defaultType];
                }
            }
        }
        //System.out.println("Host: "+socket.getInetAddress().getHostName()+"/"+socket.getInetAddress().getHostAddress()+" : "+Arrays.toString(path));
        header.fields.put(HttpField.ContentType, "image/" + format);
        /*
         * Global queries
         */
        int width = -1;
        Color defColor = null;
        if(props.containsKey("width")) {
            try {
                width = Integer.parseInt(props.getProperty("width"));
            } catch(Throwable t) {
            }
        }

        if(props.containsKey("color")) {
            defColor = ImageUtil.hex2Rgb(props.getProperty("color"));
        } else if(props.containsKey("nicecolor") && Boolean.getBoolean(props.getProperty("nicecolor"))) {
            defColor = ImageUtil.niceColor();
        }
        int sizeLimit = 6;
        if(props.containsKey("size")) {
            try {
                sizeLimit = Integer.parseInt(props.getProperty("size"));
            } catch(Throwable t) {
            }
        }
        if(sizeLimit > 50) sizeLimit = 50;
        try {
            if(path.length > 0) {
                if(path[0].equalsIgnoreCase("generator")) {
                    header.fields.put(HttpField.ContentType, ContentType.TextHTML);
                    header.send(out);
                    IOUtils.copy(getResource("generator.html"), out);
                    done();
                    return;
                }
                if(path[0].equalsIgnoreCase("fonts")) {
                    GridLayout layout = (new GridLayout(SwingUtil.FONTS.size(), 1));
                    JPanel panel = new JPanel(layout);
                    int mw = 0;
                    for(Font f : SwingUtil.FONTS) {
                        JLabel label = new JLabel("<html>" + f.getName() + "</html>");
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
                if(path[0].equalsIgnoreCase("author")) {
                    if(path.length > 1) {


                        String authorID = path[1];
                        ArrayList<Resource> res = Resource.byAuthor(authorID, sizeLimit);
                        ArrayList<BevelShape> imgs = new ArrayList<>();
                        if(sizeLimit > 0) if(res.size() > sizeLimit && sizeLimit > 1) {
                            for(int i = 0; i < sizeLimit - 1; i++) {
                                imgs.add(process(new BevelShape(bright), res.get(i), fontName, subFont, defColor));
                            }
                            imgs.add(process(new BevelShape(bright), fontName, defColor));
                        } else if(sizeLimit == 1) {
                            for(Resource r : res) {
                                imgs.add(process(new BevelShape(bright), r, fontName, subFont, defColor));
                                break;
                            }
                        } else {
                            for(Resource r : res) {
                                imgs.add(process(new BevelShape(bright), r, fontName, subFont, defColor));
                            }

                        }
                        if(imgs.isEmpty()) {
                            imgs.add(noResource(new BevelShape(bright), fontName));
                        }
                        JPanel j = SwingUtil.collect(imgs, width);
                        header.send(out);
                        ImageIO.write(SwingUtil.convert(j), format, out);
                        done();
                        return;
                    }
                }
                if(path[0].equalsIgnoreCase("spiget")) {
                    SpigetStatus stats = SpigetStatus.getStatus();
                    BevelShape img = new BevelShape(bright);
                    if(defColor != null) img.rate = defColor;
                    img.addText("API Status - Spiget", new Font(fontName, Font.BOLD, 13), 15, 20);
                    img.addText("Server Name: " + stats.status.server.name, new Font(subFont, 0, 11), 15, 35);
                    img.addText("Server Mode: " + stats.status.server.mode, new Font(subFont, 0, 11), 15, 50);
                    img.addText("Resources: " + stats.stats.resources, new Font(subFont, 0, 11), 15, 65);
                    img.addText("Authors: " + stats.stats.authors, new Font(subFont, 0, 11), 15, 80);
                    if(width > 0) {
                        img.setSize(width, img.getHeight());
                    }
                    header.send(out);
                    ImageIO.write(SwingUtil.convert(img), format, out);
                    done();
                    return;
                }
                if(path[0].equalsIgnoreCase("stats") || path[0].equalsIgnoreCase("status") || path[0].equalsIgnoreCase("stat")) {
                    BevelShape img = new BevelShape(bright);
                    stats(img, fontName, subFont);
                    if(width > 0) {
                        img.setSize(width, img.getHeight());
                    }
                    header.send(out);
                    ImageIO.write(SwingUtil.convert(img), format, out);
                    done();
                    return;
                }
                if(path[0].equalsIgnoreCase("authorcard_maintenance")) {
                    if(path.length > 1) {
                        String resourceID = path[1];
                        IDCard img = new IDCard(resourceID, sizeLimit, defColor, fontName, subFont, width);
                        header.send(out);
                        ImageIO.write(SwingUtil.convert(img), "png", out);
                        done();
                        return;
                    }
                }
                if(path[0].equalsIgnoreCase("resource")) {
                    if(path.length > 1) {
                        String resourceID = path[1];
                        Resource resource = Resource.getResource(resourceID);
                        BevelShape img = new BevelShape(bright);
                        if(resource != null)
                            process(img, resource, fontName, subFont, defColor);
                        else {
                            big(img, "Not Found :/", fontName);
                        }
                        if(width > 0) {
                            img.setSize(width, img.getHeight());
                        }
                        header.send(out);
                        ImageIO.write(SwingUtil.convert(img), format, out);
                        done();
                        return;
                    }
                }
            }
            throw new RuntimeException(new FileNotFoundException());
        } catch(RuntimeException io) {
            if(io.getCause() instanceof FileNotFoundException) {
                BevelShape img = new BevelShape(bright);
                if(defColor != null) img.rate = defColor;
                big(img, "Not Found :/", fontName);
                header.send(out);
                ImageIO.write(SwingUtil.convert(img), format, out);
            } else {
                io.printStackTrace();
                BevelShape img = new BevelShape(bright);
                if(defColor != null) img.rate = defColor;
                big(img, "Server Error :(", fontName);
                header.send(out);
                ImageIO.write(SwingUtil.convert(img), format, out);
            }
        } catch(Throwable t) {
            t.printStackTrace();
            BevelShape img = new BevelShape(bright);
            if(defColor != null) img.rate = defColor;
            big(img, "Server Error :(", "?");
            //img.setBounds(0,0,200,100);
            header.send(out);
            ImageIO.write(SwingUtil.convert(img), format, out);
        }
        done();
    }

}
