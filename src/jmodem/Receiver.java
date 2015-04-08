package jmodem;

import java.io.IOException;
import java.io.OutputStream;

public class Receiver {

	void run(InputSampleStream src, OutputStream dst) throws IOException {
		Detector d = new Detector(src);
		d.run();
		src = new Sampler(src, 1.0 / (1 + d.frequencyError()));
		// TODO: verify prefix after fixing frequency drift
		
		Equalizer eq = new Equalizer(9, 8);
		Filter filt = eq.run(src);
				
		Demodulator r = new Demodulator(src, filt);
		r.run(dst);
	}
	
}
