package thito.resourcebanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;

import thito.resourcebanner.server.HttpField;

public class SpigetStatus {

	public static class ServerStats {
		private int authors;
		private int resources;

		public int getAuthors() {
			return authors;
		}

		public int getResources() {
			return resources;
		}
	}

	public static class ServerStatus {
		private SpigetServer server;

		public SpigetServer getServer() {
			return server;
		}
	}

	public static class SpigetServer {

		private String mode;
		private String name;

		public String getMode() {
			return mode;
		}

		public String getName() {
			return name;
		}
	}

	public static SpigetStatus getSpigetStatus() {
		try {
			final URL url = new URL("https://api.spiget.org/v2/status");
			final HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.addRequestProperty(HttpField.UserAgent.toString(), "ResourceBanner");
			final BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String l;
			final StringBuilder builder = new StringBuilder();
			while ((l = r.readLine()) != null) {
				builder.append(l);
			}
			return new Gson().fromJson(builder.toString(), SpigetStatus.class);
		} catch (final Throwable t) {
			throw new RuntimeException(t);
		}
	}

	private ServerStats stats;

	private ServerStatus status;

	public ServerStats getStats() {
		return stats;
	}

	public ServerStatus getStatus() {
		return status;
	}
}
