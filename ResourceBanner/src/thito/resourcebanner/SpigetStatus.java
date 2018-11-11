package thito.resourcebanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SpigetStatus {
	public static SpigetStatus getStatus() {
		try {
			URL url = new URL("https://api.spiget.org/v2/status");
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			//con.addRequestProperty(HttpField.UserAgent.toString(), "ResourceBanner");
			BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String l;
			String b = new String();
			while ((l = r.readLine()) != null) b += l;
			return Resource.gson.fromJson(b, SpigetStatus.class); 
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	ServerStatus status;
	ServerStats stats;
	public static class SpigetServer {
		String name;
		String mode;
	}
	public static class SpigetFetch {
		long start;
		long end;
		Page page;
		boolean active;
	}
	public static class SpigetExistence {
		long start;
		long end;
		Document document;
		boolean active;
	}
	public static class Document {
		int amount;
		int suspects;
		int index;
		int id;
	}
	public static class Page {
		int amount;
		int index;
		PageItem item;
	}
	public static class PageItem {
		int index;
		String state;
	}
	public static class ServerStatus {
		SpigetServer server;
		SpigetFetch fetch;
		SpigetExistence existence;
	}
	public static class ServerStats {
		int resources;
		int authors;
		int categories;
		int resource_updates;
		int resource_versions;
	}
}
