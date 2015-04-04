package jmodem;

import java.io.IOException;

public class Receiver {

	void run(InputStream src, java.io.OutputStream dst) throws IOException {
		Detector d = new Detector(src);

		InputStream trainingSignal = d.run();
		double freq = 1 / (1 + d.frequencyError());

		Equalizer eq = new Equalizer(20, 10);
		InputStream dataSignal = eq.run(trainingSignal);
		Filter filt = eq.train();

		Demodulator r = new Demodulator(new Sampler(dataSignal, freq), filt);
		r.run(dst);
	}
}
