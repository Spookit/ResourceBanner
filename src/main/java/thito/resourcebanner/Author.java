package thito.resourcebanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;

import thito.septo.io.HttpField;

public class Author {
	public static Author getAuthor(String id) {
		try {
			final URL url = new URL("https://api.spiget.org/v2/authors/" + id + "?fields=name");
			final HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.addRequestProperty(HttpField.USER_AGENT.toString(), "ResourceBanner");
			try (final BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
				String l;
				final StringBuilder builder = new StringBuilder();
				while ((l = r.readLine()) != null) {
					builder.append(l);
				}
				con.disconnect();
				return new Gson().fromJson(builder.toString(), Author.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
			con.disconnect();
		} catch (IOException t) {
			t.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println(getAuthor("1").id);
	}

	public long id;

	public String name;

}
