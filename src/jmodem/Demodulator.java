package jmodem;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

public class Demodulator {

	private final InputSampleStream sig;
	private final Filter filt;
	private final double scaling;

	public Demodulator(InputSampleStream signal, Filter filter) {
		sig = signal;
		filt = filter;
		scaling = 2.0 / Config.Nsym;
	}

	public Complex getSymbol() throws IOException {
		double[] frame = new double[Config.Nsym];
		for (int i = 0; i < frame.length; i++) {
			frame[i] = sig.read();
			if (filt != null) {
				frame[i] = filt.process(frame[i]);
			}
		}

		double real = 0;
		double imag = 0;
		for (int i = 0; i < frame.length; i += 4) {
			real += (frame[i] - frame[i + 2]);
			imag += (frame[i + 1] - frame[i + 3]);
		}
		return new Complex(real * scaling, imag * scaling);
	}
	
	public Complex[] getSymbols(int n) throws IOException {
		Complex[] symbols = new Complex[n];
		for (int i = 0; i < n; i++) {
			symbols[i] = getSymbol();
		}
		return symbols;
	}

	public int getByte() throws IOException {
		int result = 0;
		int i = 0;
		for (Complex symbol : getSymbols(8)) {
			int bit = (symbol.imag > 0) ? 0 : 1;
			result += (bit << i);
			i++;
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
			len = len - Config.CHECKSUM_SIZE; // first 4 bytes are CRC
			CRC32 crc = new CRC32();
			crc.update(buf, Config.CHECKSUM_SIZE, len);
			int expected = (int) crc.getValue();
			int got = ByteBuffer.wrap(buf, 0, Config.CHECKSUM_SIZE).getInt();
			if (expected != got) {
				throw new IOException("Bad checksum");
			}
			if (len == 0) {
				return; // EOF
			}
			dst.write(buf, Config.CHECKSUM_SIZE, len);
		}
	}
}
