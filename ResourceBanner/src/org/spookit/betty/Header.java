package org.spookit.betty;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Header {

	public double HTTPVersion = 1.1;
	public Response response = Response.OK;
	public final Map<HttpField, Object> fields = new HashMap<>();
	public String content;

	public void send(OutputStream wr) throws Throwable {
		wr.write(toString().getBytes("UTF-8"));
		wr.flush();
	}

	@Override
	public String toString() {
		if (response.bad()) {
			return "HTTP/" + 1.1 + " " + response.code() + " " + response.info() + WebServer.newLine + WebServer.newLine
					+ (content == null ? "" : content);
		}
		String x = "HTTP/" + HTTPVersion + " " + response.code() + " " + response.info();
		for (Entry<HttpField, Object> a : fields.entrySet()) {
			x += WebServer.newLine + a.getKey() + ": " + a.getValue();
		}
		x += WebServer.newLine + WebServer.newLine;
		if (content != null)
			x += content;
		return x;
	}

}
