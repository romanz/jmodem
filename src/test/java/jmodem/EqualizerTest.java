package jmodem;

import static org.junit.Assert.*;

import org.junit.Test;

public class EqualizerTest {

	@Test
	public void testScalar() {
		double[] x = new double[] {1, 2, 0, -1, 1, -2, 3, 2, -1, 0, 1};
		double[] y = new double[] {2, 4, 0, -2, 2, -4, 6, 4, -2, 0, 2};
		
		Equalizer eq = new Equalizer(1, 0);
		Filter f = eq.train(x, y);
		assertArrayEquals(new double[]{2}, f.coeffs, 1e-8);
	}

	@Test
	public void testFIR() {
		double[] x = new double[] {1, 0, -1, 1, 0};
		double[] y = new double[] {2, 3, -2, -1, 3};
		
		Equalizer eq = new Equalizer(2, 0);
		Filter f = eq.train(x, y);
		assertArrayEquals(new double[]{2, 3}, f.coeffs, 1e-8);
	}

	@Test
	public void testLookahead() {
		double[] x = new double[] {0, 0, 1, 0, 0, 0, 0, 1, 0};
		double[] y = new double[] {0, 3, 1, 2, 0, 0, 3, 1, 2};
		
		Equalizer eq = new Equalizer(2, 1);
		Filter f = eq.train(x, y);
		assertArrayEquals(new double[]{3, 1, 2}, f.coeffs, 1e-8);
	}

	@Test
	public void testFull() {
		double[] x = new double[] {0, 1, 1, -1, 2, 1, 1, 1, 0};
		double[] y = new double[] {1, 2, 1,  2, 2, 4, 3, 2, 1};
		
		Equalizer eq = new Equalizer(2, 1);
		Filter f = eq.train(x, y);
		assertArrayEquals(new double[]{1, 1, 1}, f.coeffs, 1e-8);
	}

}
