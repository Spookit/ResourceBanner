package thito.resourcebanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.spookit.betty.HttpField;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
            return res;
        } catch(Throwable t) {
        }
        return null;
    }


    public static ArrayList<Resource> byAuthor(String id, int limit,Sort.SortType type,Sort.SortDirection order) {
        try {
            limit++;
            URL url = new URL("https://api.spiget.org/v2/authors/" + id + "/resources?fields=id&size=" + limit+"&sort="+Sort.toString(type, order));
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

    public static ArrayList<Resource> byAuthor(String id, Sort.SortDirection order, Sort.SortType type) {
        try {
        	 URL url = new URL("https://api.spiget.org/v2/authors/" + id + "/resources?fields=id&sort="+Sort.toString(type, order));
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
