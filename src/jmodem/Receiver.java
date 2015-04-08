package jmodem;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Receiver {

	static void run(InputSampleStream src, OutputStream dst) throws IOException {
		Detector d = new Detector(src);
		d.run();
		src = new Sampler(src, 1.0 / (1 + d.frequencyError()));
		// TODO: verify prefix after fixing frequency drift
		
		Equalizer eq = new Equalizer(9, 8);
		Filter filt = eq.run(src);
				
		Demodulator r = new Demodulator(src, filt);
		r.run(dst);
	}
	
	static class InputStreamWrapper implements InputSampleStream {

		InputStream input;

		public InputStreamWrapper(InputStream i) {
			input = i;
		}

		@Override
		public double read() throws IOException {
			int lsb = input.read();
			int msb = input.read();
			if (lsb == -1 || msb == -1) {
				throw new EOFException();
			}
			int s = lsb + msb << 8;
			return s / (double)Sender.scaling;
		}
	}

	
	public static void main(String[] args) throws Exception {
		InputSampleStream src = new InputStreamWrapper(System.in);
		run(src, System.out);
	}
	
}
