package jmodem;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

public class Demodulator {

	private final InputStream sig;
	private final Filter filt;

	public Demodulator(InputStream signal, Filter filter) {
		sig = signal;
		filt = filter;
	}

	private Complex getSymbol() throws IOException {
		double[] frame = new double[Config.Nsym];
		sig.read(frame, 0, frame.length);
		filt.process(frame);

		double real = 0;
		double imag = 0;
		for (int i = 0; i < frame.length; i += 4) {
			real += (frame[i] - frame[i + 2]);
			imag += (frame[i + 1] - frame[i + 3]);
		}
		return new Complex(real, imag);
	}

	private int getByte() throws IOException {
		int result = 0;
		for (int i = 0; i < 8; i++) {
			Complex sym = getSymbol();
			int bit = (sym.imag > 0) ? 1 : 0;
			result += (bit << i);
		}
		return result;
	}

	public void run(OutputStream dst) throws IOException {
		while (true) {
			int len = getByte();
			byte[] buf = new byte[len];
			for (int i = 0; i < len; i++) {
				buf[i] = (byte) getByte();
			}

			CRC32 crc = new CRC32();
			crc.update(buf, 0, len - 4);
			long expected = crc.getValue();
			long checksum = ByteBuffer.wrap(buf, len - 4, 4).getInt();
			if (expected != checksum) {
				throw new IOException("Bad checksum");
			}
			dst.write(buf, 0, len);
		}
	}
}
