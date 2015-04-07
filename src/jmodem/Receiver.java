package jmodem;

import java.io.IOException;
import java.io.OutputStream;

public class Receiver {

	void run(InputSampleStream src, OutputStream dst) throws IOException {
		Detector d = new Detector(src);
		d.run();
		
		double freq = 1 / (1 + d.frequencyError());
		Equalizer eq = new Equalizer(20, 10);
		eq.run(src);
		
		Filter filt = eq.train();
		Demodulator r = new Demodulator(new Sampler(src, freq), filt);
		r.run(dst);
	}
}
