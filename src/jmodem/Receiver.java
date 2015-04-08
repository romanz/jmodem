package jmodem;

import java.io.IOException;
import java.io.OutputStream;

public class Receiver {

	void run(InputSampleStream src, OutputStream dst) throws IOException {
		Detector d = new Detector(src);
		d.run();
		
		double freq = 1.0 / (1 + d.frequencyError());
		Equalizer eq = new Equalizer(9, 8);
		Filter filt = eq.run(src);
				
		Demodulator r = new Demodulator(new Sampler(src, freq), filt);
		r.run(dst);
	}
	
}
