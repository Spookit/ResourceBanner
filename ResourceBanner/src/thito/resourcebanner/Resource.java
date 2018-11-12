package thito.resourcebanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.spookit.betty.HttpField;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Resource {
    static final Gson gson = new Gson();

    static {

    }

    public boolean external;
    public ResourceFile file;
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

    public static Resource getResource(String id) {
        try {
            URL url = new URL("https://api.spiget.org/v2/resources/" + id + "?fields=name,rating,downloads,icon,author,premium,currency,price");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.addRequestProperty(HttpField.UserAgent.toString(), "ResourceBanner");
            BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String l;
            String b = new String();
            while((l = r.readLine()) != null) b += l;
            Resource res = gson.fromJson(b, Resource.class);
            if(isResourceExists(res)) return res;
        } catch(Throwable t) {
        }
        return null;
    }

    public static boolean isResourceExists(Resource res) {
        try {
            if(res == null) return false;
            return true;
        } catch(Throwable t) {
        }
        return false;
    }

    public static ArrayList<Resource> byAuthor(String id, int limit) {
        try {
            limit++;
            URL url = new URL("https://api.spiget.org/v2/authors/" + id + "/resources?sort=-downloads&fields=id&size=" + limit);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.addRequestProperty(HttpField.UserAgent.toString(), "ResourceBanner");
            BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String l;
            String b = new String();
            while((l = r.readLine()) != null) b += l;
            ArrayList<Resource> res = new ArrayList<>();
            for(Resource rx : gson.fromJson(b, Resource[].class)) {
                rx = getResource(rx.id + "");
                if(rx != null) res.add(rx);
            }
            return res;
        } catch(Throwable t) {
            return new ArrayList<>();
        }
    }

    public static ArrayList<Resource> byAuthor(String id) {
        try {
            URL url = new URL("https://api.spiget.org/v2/authors/" + id + "/resources?size=7&sort=-downloads&fields=id");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.addRequestProperty(HttpField.UserAgent.toString(), "ResourceBanner");
            BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String l;
            String b = new String();
            while((l = r.readLine()) != null) b += l;
            ArrayList<Resource> res = new ArrayList<>();
            for(Resource rx : gson.fromJson(b, Resource[].class)) {
                rx = getResource(rx.id + "");
                if(rx != null) res.add(rx);
            }
            return res;
        } catch(Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public Author getAuthor() {
        return au == null ? au = Author.getAuthor(author.id + "") : au;
    }
}
