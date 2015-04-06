package jmodem;

import java.io.IOException;

public class Receiver {

	void run(InputSampleStream src, java.io.OutputStream dst) throws IOException {
		Detector d = new Detector(src);

		InputSampleStream trainingSignal = d.run();
		double freq = 1 / (1 + d.frequencyError());

		Equalizer eq = new Equalizer(20, 10);
		InputSampleStream dataSignal = eq.run(trainingSignal);
		Filter filt = eq.train();

		Demodulator r = new Demodulator(new Sampler(dataSignal, freq), filt);
		r.run(dst);
	}
}
