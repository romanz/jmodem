package jmodem;

public class Filter {

	private final double[] coeffs;
	private final int length;
	private final double[] buffer;

	public Filter(double[] c) {
		coeffs = c;
		length = c.length;
		buffer = new double[length];
	}

	public void process(double[] frame) {
		for (int offset = 0; offset < frame.length; offset++) {
			System.arraycopy(buffer, 1, buffer, 0, length - 1);
			buffer[length - 1] = frame[offset];

			double result = 0;
			for (int i = 0; i < length; i++) {
				result += (buffer[i] * coeffs[i]);
			}
			frame[offset] = result;
		}
	}
}
