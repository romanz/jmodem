package jmodem;

import java.io.IOException;

public class Equalizer {

	private double[] signal;
	private double[] expected;
	private final int order;
	private final int lookahead;

	public Equalizer(int order, int lookahead) {
		this.order = order;
		this.lookahead = lookahead;
	}

	public Filter run(InputSampleStream s) throws IOException {
		int size = Config.Nsym * Sender.trainingLength;
		signal = Utils.take(s, size);
		
		expected = Utils.zeros(size); 
		BufferedStream stream = new BufferedStream(expected);
		Sender sender = new Sender(stream);
		sender.writeTraining();
		
		return train();
	}

	public Filter train() {
		final int L = signal.length;
		double[] padding = Utils.zeros(lookahead);
		Vector x = Vector.concat(signal, padding);
		Vector y = Vector.concat(padding, expected);

		final int N = order + lookahead;
		double[] Rxx = Utils.zeros(N);
		double[] Rxy = Utils.zeros(N);
		for (int i = 0; i < N; i++) {
			Vector x_ = x.slice(0, L-i);
			Rxx[i] = x.slice(i, L).dot(x_);
			Rxy[i] = y.slice(i, L).dot(x_);
		}
		
		double[] coeffs = Levinson.solver(Rxx, Rxy);
		Filter filter = new Filter(coeffs);
		return filter;
	}

}
