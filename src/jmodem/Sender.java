package jmodem;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Sender {

	private final double[] carrier;

	private short[] symbol;
	private byte[] word;

	private OutputStream out;

	public Sender(OutputStream o) {
		 carrier = new double[Commons.Nsym];
		 for (int i = 0; i < carrier.length; i++) {
			 carrier[i] = Math.sin((2 * Math.PI * Commons.Fc * i) / Commons.Fs);
		 }
		 symbol = new short[carrier.length];
		 word = new byte[2];
		 out = o;
	}

	void send(double amplitude, int n) throws IOException {
		for (int i = 0; i < symbol.length; i++) {
			symbol[i] = (short)(Commons.SCALING * amplitude * carrier[i]);
		}
		for (int i = 0; i < n; i++) {
			for (short value : symbol) {
				word[0] = (byte)(value & 0xFF);
				word[1] = (byte)(value >> 8);
				out.write(word);
			}
		}
	}


	public static void main(String []args) throws IOException {
		OutputStream out = System.out;
		InputStream in = System.in;
		Sender s = new Sender(new BufferedOutputStream(out, 1024));
		
		s.send(0., 500);
		s.send(1., 400);
		s.send(0., 100);

		int r = 0x1;
		for (int i = 0; i < 200; ++i) {
			r = Commons.prbs(r, 16, 0x1100b);
			s.send(2.0 * (r & 1) - 1, 1);
		}
		s.send(0., 100);
		
		while (true) {
			int b = in.read();
			if (b == -1) {
				break;
			}
			for (int i = 0; i < 8; i++) {
				int bit = (b >> i) & 1;
				s.send(2.0 * bit - 1, 1);
			}
			
		}
		s.send(0., 500);
		out.flush();
		out.close();
	}

}
