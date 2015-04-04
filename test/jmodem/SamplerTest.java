package jmodem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

public class SamplerTest {

	@Test
	public void test() throws IOException {
		BufferInputStream r = new BufferInputStream(150);
		for (int i = 0; i < r.buffer.length; i++) {
			r.buffer[i] = i;
		}

		Sampler s = new Sampler(r, 1.0);
		s.updateTime(0.3);
		s.updateFreq(-0.1);

		double[] expected = new double[] { 0.21032059598392791,
				1.230948722027172, 2.0889280530745129, 2.9999999999999982,
				3.905555713242542, 4.790283469423569, 5.710319425568426 };

		double[] result = new double[expected.length];
		s.read(result, 0, result.length);

		assertEquals(expected.length, result.length);
		for (int i = 0; i < result.length; i++) {
			double err = Math.abs(expected[i] - result[i]);
			assertTrue(err < 1e-12);
		}
	}
}
