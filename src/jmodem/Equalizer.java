package jmodem;

import java.io.IOException;

public class Equalizer {

	private final int order;
	private final int lookahead;

	public Equalizer(int order, int lookahead) {
		this.order = order;
		this.lookahead = lookahead;
	}

	public Filter run(InputSampleStream s) throws IOException {
		int size = Config.Nsym * Sender.trainingLength;
		double[] signal = Utils.take(s, size);
		
		double[] expected = Utils.zeros(size); 
		BufferedStream stream = new BufferedStream(expected);
		Sender sender = new Sender(stream);
		sender.writeTraining();
		
		Filter filt = train(signal, expected);
		for (double sample : signal) {
			filt.process(sample);
		}
		for (int i = 0; i < lookahead; i++) {
			filt.process(s.read());
		}
		return filt;
	}

	public Filter train(double[] signal, double[] expected) {
		double[] padding = Utils.zeros(lookahead);
		Vector x = Vector.concat(signal, padding);
		Vector y = Vector.concat(padding, expected);
		final int L = x.size;

		final int N = order + lookahead;
		double[] Rxx = Utils.zeros(N);
		double[] Rxy = Utils.zeros(N);
		
		for (int i = 0; i < N; i++) {
			Vector x_ = x.slice(0, L-i);
			Rxx[i] = x.slice(i, L).dot(x_);
			Rxy[i] = y.slice(i, L).dot(x_);
		}
		
		return new Filter(Levinson.solver(Rxx, Rxy));
	}

}
