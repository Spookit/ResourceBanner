package thito.resourcebanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.spookit.betty.HttpField;

public class SpigotResource {
  static final Gson gson = new Gson();
  static final Map<String, String> cookies = new HashMap<>();
  public int existenceStatus;
  public boolean external;
  public String description;
  public String contributors;
  public int likes;
  public String donationLink;
  public String supportedLanguages;
  public String[] testedVersions;
  public IDContainer[] versions;
  public IDContainer[] updates;
  public IDContainer[] reviews;
  public JsonObject links;
  public String name;
  public String tag;
  public IDContainer version;
  public IDContainer author;
  public IDContainer category;
  public ResourceRating rating;
  public long releaseDate;
  public long updateDate;
  public int downloads;
  public boolean premium;
  public double price;
  public String currency;
  public int id;
  public IconContainer icon;
  Author au = null;

  public static ArrayList<SpigotResource> byAuthor(String id, int limit, Sort.SortType type, Sort.SortDirection order) {
    try {
      limit++;
      URL url = new URL("https://api.spiget.org/v2/authors/" + id + "/resources?fields=id&size=" + limit
          + "&sort=" + Sort.toString(type, order));
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.addRequestProperty(HttpField.UserAgent.toString(), "ResourceBanner");
      BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String l;
      StringBuilder builder = new StringBuilder();
      while ((l = r.readLine()) != null) {
        builder.append(l);
      }
      ArrayList<SpigotResource> res = new ArrayList<>();
      for (SpigotResource rx : gson.fromJson(builder.toString(), SpigotResource[].class)) {
        rx = getResource(rx.id + "");
        if (rx != null) {
          res.add(rx);
        }
      }
      return res;
    } catch (Throwable t) {
      return new ArrayList<>();
    }
  }

  public static ArrayList<SpigotResource> byAuthor(String id, Sort.SortDirection order, Sort.SortType type) {
    try {
      URL url = new URL("https://api.spiget.org/v2/authors/" + id + "/resources?fields=id&sort="
          + Sort.toString(type, order));
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.addRequestProperty(HttpField.UserAgent.toString(), "ResourceBanner");
      BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String l;
      StringBuilder builder = new StringBuilder();
      while ((l = r.readLine()) != null) {
        builder.append(l);
      }
      ArrayList<SpigotResource> res = new ArrayList<>();
      for (SpigotResource rx : gson.fromJson(builder.toString(), SpigotResource[].class)) {
        rx = getResource(rx.id + "");
        if (rx != null) {
          res.add(rx);
        }
      }
      return res;
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  public static SpigotResource getResource(String id) {
    try {
      URL url = new URL("https://api.spiget.org/v2/resources/" + id
          + "?fields=name,rating,downloads,icon,author,premium,currency,price");
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.addRequestProperty(HttpField.UserAgent.toString(), "ResourceBanner");
      BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String l;
      StringBuilder builder = new StringBuilder();
      while ((l = r.readLine()) != null) {
        builder.append(l);
      }
      SpigotResource res = gson.fromJson(builder.toString(), SpigotResource.class);
      return confirmDeletion(res);
    } catch (Throwable t) {

    }
    return null;
  }

  public static SpigotResource confirmDeletion(SpigotResource res) {
    try {
      URL url = new URL("https://nougat.api.spiget.org/v2/resources/" + res.id);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.addRequestProperty(HttpField.UserAgent.toString(), "ResourceBanner");
      BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String l;
      StringBuilder builder = new StringBuilder();
      while ((l = r.readLine()) != null) {
        builder.append(l);
      }
      SpigotResource another = gson.fromJson(builder.toString(), SpigotResource.class);
      if (another.existenceStatus != 0) {
        return null;
      }
      return res;
    } catch (Throwable t) {

    }
    return res;
  }

  public Author getAuthor() {
    return au == null ? au = Author.getAuthor(author.id + "") : au;
  }
}
