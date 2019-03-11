package thito.resourcebanner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.stream.ImageOutputStreamImpl;

public class ByteArrayImageOutputStream extends ImageOutputStreamImpl {

	private ByteArrayInputStream input;
	private final ByteArrayOutputStream output = new ByteArrayOutputStream();
	private final OutputStream wrapped;

	public ByteArrayImageOutputStream(OutputStream o) {
		wrapped = o;
	}

	@Override
	public void close() throws IOException {
		wrapped.write(output.toByteArray());
	}

	@Override
	public int read() throws IOException {
		if (input != null)
			input = new ByteArrayInputStream(output.toByteArray());
		return input.read();
	}

	@Override
	public int read(byte[] arg0, int arg1, int arg2) throws IOException {
		if (input != null)
			input = new ByteArrayInputStream(output.toByteArray());
		return input.read(arg0, arg1, arg2);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		output.write(b, off, len);
		input = null;
	}

	@Override
	public void write(int arg0) throws IOException {
		output.write(arg0);
		input = null;
	}

}
