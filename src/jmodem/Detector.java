package jmodem;

import java.io.IOException;
import java.util.ArrayDeque;

public class Detector {

	private final InputSampleStream src;
	private final ArrayDeque<double[]> frames;
	private final int bufsize = 1000;

	public Detector(InputSampleStream s) {
		src = s;
		frames = new ArrayDeque<double[]>(bufsize);
	}

	boolean process(double threshold) throws IOException {
		double[] frame = new double[Config.Nsym];
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
		for (int i = 0; i < 100; i++) {
			process(1); // prefix frames
		}
		int count = 0;
		while (count < 360) {
			boolean good = process(0.9);
			if (good) {
				count++;
			} else {
				count = 0;
			}
		}
		for (int i = 0; i < 100; i++) {
			process(1); // postfix frames
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

	private double[] optimize(double[] buf) {
		return buf;
	}

	public InputSampleStream run() throws IOException {
		double[] buf = detect();
		buf = optimize(buf);
		return null;
	}

	public double frequencyError() {
		// TODO Auto-generated method stub
		return 0;
	}

}
