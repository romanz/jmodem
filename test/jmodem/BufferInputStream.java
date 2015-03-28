package jmodem;

import java.io.IOException;

public class BufferInputStream implements InputStream {

	float[] buffer;
	int offset;

	public BufferInputStream(int size) {
		buffer = new float[size];
		offset = 0;
	}
	
	@Override
	public void read(float[] buffer, int offset, int size) throws IOException {
		if (this.offset + size > this.buffer.length) {
			throw new IOException("EOF");
		}
		System.arraycopy(this.buffer, this.offset, buffer, offset, size);
		this.offset += size;
	}
	
}
