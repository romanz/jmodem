package jmodem;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

public class Sender {

	private static final int FRAME_SIZE = 250;

	final static int symbolLength = 8;
	final static int sampleRate = 8000;
	final static int duration = 10; // in seconds
	final static float scaling = 30e3f;

	private final OutputSampleStream output;
	final private short[] cos = new short[] { 1, 0, -1, 0, 1, 0, -1, 0 };
	final private short[] sin = new short[] { 0, 1, 0, -1, 0, 1, 0, -1 };

	public Sender(OutputSampleStream o) {
		output = o;
	}

	void writeSymbols(float real, float imag, int count) throws IOException {
		for (int c = 0; c < count; c++) {
			for (int i = 0; i < symbolLength; i++) {
				output.write(real * cos[i] - imag * sin[i]);
			}
		}
	}

	void writeByte(byte b) throws IOException {
		for (int i = 0; i < 8; i++) {
			int k = (b >> i) & 1;
			writeSymbols(0f, (2f * k) - 1f, 1);
		}
	}

	public void writePrefix() throws IOException {
		writeSymbols(0f, 0f, 1000);
		writeSymbols(0f, 1f, 400);
		writeSymbols(0f, 0f, 150);

		for (int register = 0x0001, i = 0; i < 500; i++) {
			int k = (i < 16) ? 0 : (register & 3);
			writeSymbols(cos[k], sin[k], 1);
			register = register << 1;
			if (register >> 16 != 0) {
				register = register ^ 0x1100b;
			}
		}
		writeSymbols(0f, 0f, 100);
	}

	public void writeData(byte[] data) throws IOException {

		for (int i = 0; i <= data.length; i++) {
			if (i % FRAME_SIZE == 0 || i == data.length) {
				int size = Math.min(FRAME_SIZE, data.length - i);
				writeByte((byte) (size + 4)); // include CRC32

				CRC32 crc = new CRC32();
				crc.update(data, i, size);

				ByteBuffer checksum = ByteBuffer.allocate(4);
				checksum.putInt((int) crc.getValue());
				for (byte b : checksum.array()) {
					writeByte(b);
				}
				if (i == data.length) {
					break;
				}
			}
			writeByte(data[i]);
		}
		writeSymbols(0f, 0f, 1000);
	}

	static class OutputStreamWrapper implements OutputSampleStream {

		OutputStream output;

		public OutputStreamWrapper(OutputStream o) {
			output = o;
		}

		@Override
		public void write(double v) throws IOException {
			short s = (short) (scaling * v);
			output.write(s & 0xFF);
			output.write(s >> 8);
		}
	}

	public static void main(String[] args) throws Exception {
		Sender s = new Sender(new OutputStreamWrapper(System.out));
		s.writePrefix();
		s.writeData("foo bax\nnoop\n".getBytes());
	}
}
