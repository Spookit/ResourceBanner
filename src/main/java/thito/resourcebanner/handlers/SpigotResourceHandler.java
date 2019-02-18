package thito.resourcebanner.handlers;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.spookit.betty.HttpField;

import thito.resourcebanner.Sort;
import thito.resourcebanner.resource.SpigotResource;

/**
 * @author Plajer
 * <p>
 * Created at 18.02.2019
 */
public class SpigotResourceHandler {

  public List<SpigotResource> byAuthor(String id, int limit, Sort.SortType type, Sort.SortDirection order) {
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
      List<SpigotResource> res = new ArrayList<>();
      for (SpigotResource rx : new Gson().fromJson(builder.toString(), SpigotResource[].class)) {
        rx = getResource(rx.getId() + "");
        if (rx != null) {
          res.add(rx);
        }
      }
      return res;
    } catch (Throwable t) {
      return new ArrayList<>();
    }
  }

  public SpigotResource getResource(String id) {
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
      return new Gson().fromJson(builder.toString(), SpigotResource.class);
    } catch (Throwable t) {

    }
    return null;
  }

}
