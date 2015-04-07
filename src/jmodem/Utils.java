package jmodem;

import java.io.IOException;

public class Utils {

	public static double[] cos(int n) {
		double[] res = zeros(n);
		for (int i = 0; i < n; i++) {
			res[i] = Math.cos(0.5 * Math.PI * i);
		}
		return res;
	}
	public static double[] sin(int n) {
		double[] res = zeros(n);
		for (int i = 0; i < n; i++) {
			res[i] = Math.sin(0.5 * Math.PI * i);
		}
		return res;
	}
	public static double[] zeros(int n) {
		return new double[n];
	}
	public static int argmax(double[] xs) {
		int res = 0;
		for (int i = 1; i < xs.length; i++) {
			if (xs[i] > xs[res]) {
				res = i;
			}
		}
		return res;
	}
	public static double[] take(InputSampleStream s, int n) throws IOException {
		double[] signal = zeros(n);
		for (int i = 0; i < n; i++) {
			signal[i] = s.read();
		}
		return signal;
	}

}
