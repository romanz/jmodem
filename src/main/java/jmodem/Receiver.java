package jmodem;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Receiver {

	static void run(InputSampleStream src, OutputStream dst) throws IOException {
		Detector d = new Detector(src);
		d.run();
		double freq = 1.0 / (1 + d.frequencyDrift());
		src = new Sampler(src, freq);
		// TODO: verify prefix after fixing frequency drift

		Equalizer eq = new Equalizer(9, 8);
		Filter filt = eq.run(src);

		Demodulator r = new Demodulator(src, filt);
		r.run(dst);
	}

	static class InputStreamWrapper implements InputSampleStream {

		DataInputStream input;
		byte[] blob = new byte[Config.fileBufferSize];
		ByteBuffer buf;

		public InputStreamWrapper(InputStream i) {
			input = new DataInputStream(i);
			buf = ByteBuffer.wrap(blob).order(ByteOrder.LITTLE_ENDIAN);
		}

		@Override
		public double read() throws IOException {
			if (buf.hasRemaining() == false) {
				buf.clear();
				input.readFully(blob);
			}
			return buf.getShort() / Config.scalingFactor;
		}
	}

	public static void main(String[] args) throws Exception {
		InputSampleStream src = new InputStreamWrapper(System.in);
		run(src, System.out);
	}

}
