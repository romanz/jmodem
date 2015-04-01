package jmodem;

import java.io.IOException;

public class BufferInputStream implements InputStream {

	double[] buffer;
	int offset;

	public BufferInputStream(int size) {
		buffer = new double[size];
		offset = 0;
	}

	@Override
	public void read(double[] buffer, int offset, int size) throws IOException {
		if (this.offset + size > this.buffer.length) {
			throw new IOException("EOF");
		}
		System.arraycopy(this.buffer, this.offset, buffer, offset, size);
		this.offset += size;
	}

}
