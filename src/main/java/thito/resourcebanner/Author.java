package thito.resourcebanner;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.spookit.betty.HttpField;

public class Author {
  public String name;
  public long id;

  public static Author getAuthor(String id) {
    try {
      URL url = new URL("https://api.spiget.org/v2/authors/" + id + "?fields=name");
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.addRequestProperty(HttpField.UserAgent.toString(), "ResourceBanner");
      BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String l;
      StringBuilder builder = new StringBuilder();
      while ((l = r.readLine()) != null) {
        builder.append(l);
      }
      return new Gson().fromJson(builder.toString(), Author.class);
    } catch (Throwable t) {
      t.printStackTrace();
    }
    return null;
  }

  public static void main(String[] args) {
    System.out.println(getAuthor("1").id);
  }

}
