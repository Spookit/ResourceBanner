package thito.resourcebanner.handlers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import thito.resourcebanner.Sort;
import thito.resourcebanner.resource.SpigotResource;
import thito.septo.io.HttpField;

/**
 * @author Plajer
 *         <p>
 *         Created at 18.02.2019
 */
public class SpigotResourceHandler {

	public List<SpigotResource> byAuthor(String id, int limit, Sort.SortType type, Sort.SortDirection order) {
		try {
			limit++;
			final URL url = new URL("https://api.spiget.org/v2/authors/" + id + "/resources?fields=id&size=" + limit
					+ "&sort=" + Sort.toString(type, order));
			final HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.addRequestProperty(HttpField.USER_AGENT.toString(), "ResourceBanner");
			try (final BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
				String l;
				final StringBuilder builder = new StringBuilder();
				while ((l = r.readLine()) != null) {
					builder.append(l);
				}
				con.disconnect();
				final List<SpigotResource> res = new ArrayList<>();
				for (SpigotResource rx : new Gson().fromJson(builder.toString(), SpigotResource[].class)) {
					rx = getResource(rx.getId() + "");
					if (rx != null) {
						res.add(rx);
					}
				}
				return res;
			}
		} catch (final Throwable t) {
			return new ArrayList<>();
		}
	}

	public SpigotResource getResource(String id) {
		try {
			final URL url = new URL("https://api.spiget.org/v2/resources/" + id
					+ "?fields=name,rating,downloads,icon,author,premium,currency,price");
			final HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.addRequestProperty(HttpField.USER_AGENT.toString(), "ResourceBanner");
			try (final BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
				String l;
				final StringBuilder builder = new StringBuilder();
				while ((l = r.readLine()) != null) {
					builder.append(l);
				}
				con.disconnect();
				return new Gson().fromJson(builder.toString(), SpigotResource.class);
			}
		} catch (final Throwable t) {

		}
		return null;
	}

}
