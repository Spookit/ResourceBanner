package thito.resourcebanner.server;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Header {

	public String content;
	public final Map<HttpField, Object> fields = new HashMap<>();
	public double HTTPVersion = 1.1;
	public Response response = Response.OK;

	public void send(OutputStream wr) throws Throwable {
		wr.write(toString().getBytes(StandardCharsets.UTF_8));
		wr.flush();
	}

	@Override
	public String toString() {
		if (response.bad()) {
			return "HTTP/" + 1.1 + " " + response.code() + " " + response.info() + WebServer.newLine + WebServer.newLine
					+ (content == null ? "" : content);
		}
		final StringBuilder builder = new StringBuilder(
				"HTTP/" + HTTPVersion + " " + response.code() + " " + response.info());
		for (final Entry<HttpField, Object> a : fields.entrySet()) {
			builder.append(WebServer.newLine).append(a.getKey()).append(": ").append(a.getValue());
		}
		builder.append(WebServer.newLine + WebServer.newLine);
		if (content != null) {
			builder.append(content);
		}
		return builder.toString();
	}

}
