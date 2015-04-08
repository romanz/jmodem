package jmodem;

import static org.junit.Assert.*;

import org.junit.Test;

public class LevinsonTest {

	@Test
	public void test() {
		double[] t = new double[]{ 3, -2, 1, -1};
		double[] y = new double[]{ -1, -2, -3, 15};
		double[] x = Levinson.solver(t, y);
		double[] x_ = new double[]{1, 0, 4, 8};
		assertArrayEquals(x_, x, 1e-6);
	}

}
