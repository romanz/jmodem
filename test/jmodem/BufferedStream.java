package jmodem;

import java.io.IOException;

public class BufferedStream implements InputSampleStream, OutputSampleStream {

	double[] buffer;
	int offset;

	public BufferedStream(int size) {
		buffer = new double[size];
		reset();
	}

	public BufferedStream(double[] b) {
		buffer = b.clone();
		reset();
	}

	public void reset() {
		offset = 0;
	}

	@Override
	public void write(double value) throws IOException {
		if (offset >= buffer.length) {
			throw new IOException("EOF");
		}
		buffer[offset] = value;
		offset++;
	}

	@Override
	public double read() throws IOException {
		if (offset >= buffer.length) {
			throw new IOException("EOF");
		}
		double value = buffer[offset];
		this.offset++;
		return value;
	}

}
