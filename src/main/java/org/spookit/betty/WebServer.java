package org.spookit.betty;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Properties;

public abstract class WebServer {

	static final String newLine = "\r\n";
	boolean log = true;
	int port;
	boolean running = false;
	ServerSocket socket;

	public WebServer(int port) {
		this.port = port;
	}

	public WebServer disableLogging() {
		log = false;
		return this;
	}

	public WebServer enableLogging() {
		log = true;
		return this;
	}

	public int getPort() {
		return port;
	}

	public abstract void handle(OutputStream out, BufferedReader reader, Socket socket, String[] path, Properties props,
			Properties browser) throws Throwable;

	public void start() {
		if (running)
			return;
		running = true;
		new Thread() {
			@Override
			public void run() {
				start0();
			}
		}.start();
	}

	void start0() {
		try {
			socket = new ServerSocket(port);
			while (running) {
				try {
					Socket sock = socket.accept();
					new Thread() {
						@Override
						public void run() {
							try {
								BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
								OutputStream out = sock.getOutputStream();
								String request = in.readLine();
								if (request == null)
									return;
								String[] x1 = request.split(" ", 2);
								String path = "/";
								Properties props = new Properties();
								Properties bro = new Properties();
								if (x1.length == 2) {
									path = x1[1];
									int lastIndex = path.lastIndexOf("HTTP/");
									path = path.substring(0, lastIndex - 1);
								}
								path = URLDecoder.decode(path, "UTF-8");
								String[] paths = path.substring(1).split("/");
								if (paths.length > 0) {
									String query = paths[paths.length - 1];
									if (query.contains("?")) {
										String[] spl = query.split("\\?", 2);
										query = spl[1];
										for (String q : query.split("&")) {
											String[] qs = q.split("=", 2);
											props.put(qs[0].trim(), qs.length == 2 ? qs[1].trim() : null);
										}
										paths[paths.length - 1] = spl[0];
									}
								}
								while (true) {
									String ignore = in.readLine();
									if (ignore == null || ignore.replace(" ", "").replace(newLine, "").length() == 0)
										break;
									String[] i = ignore.split(":");
									if (i.length == 2) {
										bro.put(i[0].trim(), i[1].trim());
									}
								}
								if (!request.startsWith("GET ")
										|| !(request.endsWith(" HTTP/1.0") || request.endsWith(" HTTP/1.1"))) {
									out.write(("HTTP/1.1 400 Bad Request" + newLine + newLine).getBytes("UTF-8"));
								} else {
									handle(out, in, sock, paths, props, bro);
								}
								sock.close();
							} catch (Throwable t) {
								if (log)
									Handle.Throw(t);
							}
						}
					}.start();
				} catch (Throwable t) {
					if (log)
						t.printStackTrace();
				}
			}
		} catch (Throwable t) {
			throw new RuntimeException("failed to start server", t);
		}
	}

	public void stop() {
		if (socket != null && running) {
			try {
				running = false;
				socket.close();
			} catch (Throwable t) {
			}
		}
	}
}
