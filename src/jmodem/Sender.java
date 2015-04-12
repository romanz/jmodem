package jmodem;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

public class Sender {

	final static private short[] cos = new short[] { 1, 0, -1, 0, 1, 0, -1, 0 };
	final static private short[] sin = new short[] { 0, 1, 0, -1, 0, 1, 0, -1 };

	private final OutputSampleStream output;

	public Sender(OutputSampleStream o) {
		output = o;
	}

	void writeSymbols(float real, float imag, int count) throws IOException {
		for (int c = 0; c < count; c++) {
			for (int i = 0; i < Config.symbolLength; i++) {
				output.write(real * cos[i] + imag * sin[i]);
			}
		}
	}

	public void writeByte(byte b) throws IOException {
		for (int i = 0; i < 8; i++) {
			int k = (b >> i) & 1;
			writeSymbols(0f, 1f - (2f * k), 1);
		}
	}

	public void writeSilence(int n) throws IOException {
		writeSymbols(0f, 0f, n);
	}

	public void writePrefix() throws IOException {
		writeSilence(1000);
		writeSymbols(0f, -1f, 400);
		writeSilence(50);
	}

	public void writeTraining() throws IOException {
		writeSilence(100);
		for (int register = 0x0001, i = 0; i < 500; i++) {
			int k = (i < 16) ? 0 : (register & 3);
			writeSymbols(cos[k], -sin[k], 1);
			register = register << 1;
			if (register >> 16 != 0) {
				register = register ^ 0x1100b;
			}
		}
		writeSilence(100);
	}

	public void writeData(byte[] data, int length) throws IOException {

		for (int i = 0; i < length; i++) {
			if (i % Config.frameSize == 0) {
				int size = Math.min(Config.frameSize, length - i);
				writeChecksum(data, i, size);
			}
			writeByte(data[i]);
		}
	}

	public void writeChecksum(byte[] data, int offset, int size)
			throws IOException {
		writeByte((byte) (size + 4)); // include CRC32

		CRC32 crc = new CRC32();
		if (data != null) {
			crc.update(data, offset, size);
		}

		ByteBuffer checksum = ByteBuffer.allocate(4);
		checksum.putInt((int) crc.getValue());
		for (byte b : checksum.array()) {
			writeByte(b);
		}
	}

	public void flush() throws IOException {
		writeChecksum(null, 0, 0);
	}

	static class OutputStreamWrapper implements OutputSampleStream {

		OutputStream output;

		public OutputStreamWrapper(OutputStream o) {
			output = o;
		}

		@Override
		public void write(double v) throws IOException {
			short s = (short) (Config.scaling * v);
			output.write(s & 0xFF);
			output.write(s >> 8);
		}
	}

	public static void main(String[] args) throws Exception {
		Sender s = new Sender(new OutputStreamWrapper(System.out));
		s.writePrefix();
		s.writeTraining();
		byte[] buf = new byte[1000];
		while (true) {
			int read = System.in.read(buf);
			if (read == -1) {
				break;
			}
			s.writeData(buf, read);
		}
		s.writeChecksum(null, 0, 0);
		s.writeSilence(1000);
	}

}
