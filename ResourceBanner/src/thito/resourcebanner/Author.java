package thito.resourcebanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.spookit.betty.HttpField;

public class Author {
	public static Author getAuthor(String id) {
		try {
			URL url = new URL("https://api.spiget.org/v2/authors/" + id + "?fields=name");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.addRequestProperty(HttpField.UserAgent.toString(), "ResourceBanner");
			BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String l;
			String b = new String();
			while ((l = r.readLine()) != null)
				b += l;
			return SpigotResource.gson.fromJson(b, Author.class);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws Throwable {
		System.out.println(getAuthor("1").id);
	}

	public String name;

	public IconContainer icon;

	public long id;

}
