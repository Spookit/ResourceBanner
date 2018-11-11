package org.spookit.betty;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

public class Handle {

	static void Throw(Throwable t) {
		throw (RuntimeException) reproduce(t);
	}

	@SuppressWarnings("unchecked")
	static <T extends Throwable> T reproduce(Throwable t) throws T {
		throw (T)t;
	}
	
	public static void redirect(Header header,String name) {
		header.response = Response.PERMANENT_REDIRECT;
		header.fields.put(HttpField.Location, name);
	}
	
	public static void postFile(Header header,OutputStream wr,File file) throws Throwable {
		header.fields.put(HttpField.ContentType, ContentType.Unknown);
		header.send(wr);
		IOUtils.copy(new FileInputStream(file), wr);
	}
	public static void postFile(Header header,OutputStream wr,InputStream file) throws Throwable {
		header.fields.put(HttpField.ContentType, ContentType.Unknown);
		header.send(wr);
		IOUtils.copy(file, wr);
	}
	
	public static void postString(Header header,OutputStream wr,String string) throws Throwable {
		header.fields.put(HttpField.Connection, ContentType.Unknown);
		header.send(wr);
		wr.write(string.getBytes());
	}
	public static void sendError(Header header,int code) {
		header.fields.clear();
		header.response = Response.valueOf(code);
		header.content = "<html><h1>"+code+"</h1><h2>"+Response.valueOf(code).info()+"</h2></html>";
		header.fields.put(HttpField.ContentType, ContentType.TextHTML);
	}
	
}
