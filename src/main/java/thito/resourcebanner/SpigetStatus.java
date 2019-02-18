package thito.resourcebanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.spookit.betty.HttpField;

public class SpigetStatus {
  ServerStatus status;
  ServerStats stats;

  public static SpigetStatus getStatus() {
    try {
      URL url = new URL("https://api.spiget.org/v2/status");
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.addRequestProperty(HttpField.UserAgent.toString(), "ResourceBanner");
      BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String l;
      StringBuilder builder = new StringBuilder();
      while ((l = r.readLine()) != null) {
        builder.append(l);
      }
      return SpigotResource.gson.fromJson(builder.toString(), SpigetStatus.class);
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  public static class Document {
    public int amount;
    public int suspects;
    public int index;
    public int id;
  }

  public static class Page {
    public int amount;
    public int index;
    public PageItem item;
  }

  public static class PageItem {
    public int index;
    public String state;
  }

  public static class ServerStats {
    public int resources;
    public int authors;
    public int categories;
    public int resource_updates;
    public int resource_versions;
  }

  public static class ServerStatus {
    public SpigetServer server;
    public SpigetFetch fetch;
    public SpigetExistence existence;
  }

  public static class SpigetExistence {
    public long start;
    public long end;
    public Document document;
    public boolean active;
  }

  public static class SpigetFetch {
    public long start;
    public long end;
    public Page page;
    public boolean active;
  }

  public static class SpigetServer {
    public String name;
    public String mode;
  }
}
