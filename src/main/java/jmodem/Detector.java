package jmodem;

import java.io.IOException;
import java.util.ArrayDeque;

class Detector {

	private final InputSampleStream src;
	private final ArrayDeque<double[]> frames;
	private final int bufsize = 1000;
	private double drift;

	public Detector(InputSampleStream s) {
		src = s;
		frames = new ArrayDeque<double[]>(bufsize);
	}

	boolean process(double threshold) throws IOException {
		double[] frame = new double[Config.symbolLength];
		for (int i = 0; i < frame.length; i++) {
			frame[i] = src.read();
		}

		double real = 0;
		double imag = 0;
		for (int i = 0; i < frame.length; i += 4) {
			real += (frame[i] - frame[i + 2]);
			imag += (frame[i + 1] - frame[i + 3]);
		}

		double power = 0;
		for (double s : frame) {
			power += (s * s);
		}
		double coherency = 0;
		if (power > 0) {
			coherency = (real * real + imag * imag) / power;
		}

		frames.addLast(frame);
		if (frames.size() > bufsize) {
			frames.removeFirst();
		}
		return (coherency > threshold);
	}

	double[] detect() throws IOException {
		for (int i = 0; i < Config.prefixSilence; i++) {
			process(1); // prefix frames
		}
		int count = 0;
		while (count < Config.prefixSymbols) {
			boolean good = process(0.9);
			if (good) {
				count++;
			} else {
				count = 0;
			}
		}

		int size = 0;
		for (double[] frame : frames) {
			size += frame.length;
		}
		double[] buf = new double[size];
		int pos = 0;
		for (double[] frame : frames) {
			System.arraycopy(frame, 0, buf, pos, frame.length);
			pos += frame.length;
		}
		return buf;
	}

	private int findStart(double[] buf) {
		int m = 10 * Config.symbolLength;
		Vector cos = Vector.concat(Utils.zeros(m), Utils.cos(m));
		Vector sin = Vector.concat(Utils.zeros(m), Utils.sin(m));

		double[] res = new double[buf.length - 2 * m];
		for (int i = 0; i < res.length; i++) {
			Vector frame = new Vector(buf, i, 2 * m);
			double real = frame.dot(cos);
			double imag = frame.dot(sin);
			double norm = frame.norm();
			if (norm > 0) {
				res[i] = (real * real + imag * imag) / norm;
			}
		}
		int offset = Utils.argmax(res);
		return offset + m;
	}

	private double[] readPrefix(double[] buf, int start) throws IOException {
		int size = Config.prefixLength * Config.symbolLength;
		double[] prefix = Utils.zeros(size);
		int toCopy = buf.length - start;

		System.arraycopy(buf, start, prefix, 0, toCopy);
		for (int i = toCopy; i < prefix.length; i++) {
			prefix[i] = src.read();
		}
		return prefix;
	}

	public double[] run() throws IOException {
		double[] buf = detect();
		int start = findStart(buf);
		double[] prefix = readPrefix(buf, start);
		drift = estimateDrift(prefix);
		return prefix;
	}

	double estimateDrift(double[] prefix) throws IOException {
		Demodulator d = new Demodulator(new BufferedStream(prefix), null);
		Complex[] symbols = d.getSymbols(Config.prefixSymbols);

		final int skip = 5;
		double sum = 0.0;
		int count = 0;
		for (int i = skip + 1; i < symbols.length - skip; i++) {
			Complex z1 = symbols[i - 1];
			Complex z2 = symbols[i];
			// Compute "delta" phase of (z2' * z1)
			double real = z1.real * z2.real + z1.imag * z2.imag;
			double imag = z1.imag * z2.real - z1.real * z2.imag;
			sum += Math.atan2(imag, real);
			count++;
		}
		double avgDrift = sum / count;
		return avgDrift * Config.baudRate / (2 * Math.PI * Config.carrierFreq);
	}

	public double frequencyDrift() {
		return drift;
	}

}
