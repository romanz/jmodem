package jmodem;

public class Config {
	public static final int SCALING = 32000;
	public static final int Fs = 8000;
	public static final int Fc = 2000;
	public static final int Nsym = 8;
	public static float Tsym = ((float) Nsym) / Fs;

	public static final int CHECKSUM_SIZE = 4;

	public static int prbs(int register, int size, int mask) {
		register = register << 1;
		if ((register >> size) != 0) {
			register = register ^ mask;
		}
		return register;
	}
}
