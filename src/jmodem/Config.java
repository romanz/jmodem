package jmodem;

public class Config {
	public static final int SCALING = 32000;
	public static final int Fs = 8000;
	public static final int Fc = 2000;
	public static final int Nsym = 8;
	public static float Tsym = ((float) Nsym) / Fs;

	static final int CHECKSUM_SIZE = 4;

	static final int frameSize = 250;

	final static int symbolLength = 8;
	final static int sampleRate = 8000;

	final static double scaling = 30e3;

	final static int prefixSymbols = 400;
	final static int prefixSilence = 50;
	final static int prefixLength = prefixSymbols + prefixSilence;

	final static int trainingSilence = 100;
	final static int trainingSymbols = 500;
	final static int trainingConstant = 16;

	final static int trainingLength = trainingSilence * 2 + trainingSymbols;
}
