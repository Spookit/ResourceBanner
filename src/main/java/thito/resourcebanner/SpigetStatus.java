package thito.resourcebanner;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.spookit.betty.HttpField;

public class SpigetStatus {

  private ServerStatus status;
  private ServerStats stats;

  public static SpigetStatus getSpigetStatus() {
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
      return new Gson().fromJson(builder.toString(), SpigetStatus.class);
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  public ServerStatus getStatus() {
    return status;
  }

  public ServerStats getStats() {
    return stats;
  }

  public static class ServerStats {
    private int resources;
    private int authors;

    public int getResources() {
      return resources;
    }

    public int getAuthors() {
      return authors;
    }
  }

  public static class ServerStatus {
    private SpigetServer server;

    public SpigetServer getServer() {
      return server;
    }
  }

  public static class SpigetServer {

    private String name;
    private String mode;

    public String getName() {
      return name;
    }

    public String getMode() {
      return mode;
    }
  }
}
