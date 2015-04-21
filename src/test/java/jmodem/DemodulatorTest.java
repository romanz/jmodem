package jmodem;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class DemodulatorTest {

	@Test
	public void testSymbol() throws IOException {
		double[] x = new double[] { 2, -1, -2, 1, 2, -1, -2, 1 };
		BufferedStream b = new BufferedStream(x);
		Filter filt = new Filter(new double[] { 1 });
		Demodulator d = new Demodulator(b, filt);
		Complex c = d.getSymbol();
		assertEquals(+2.0, c.real, 1e-6);
		assertEquals(-1.0, c.imag, 1e-6);
	}

	@Test
	public void testByte() throws IOException {
		BufferedStream b = new BufferedStream(1024);
		Modulator m = new Modulator(b);
		int[] data = new int[] { 42, 156, 255, 0 };
		for (int x : data) {
			m.writeByte((byte) x);
		}
		b.reset();
		Demodulator d = new Demodulator(b, new Filter(new double[] { 1 }));
		for (int x : data) {
			assertEquals(x, d.getByte());
		}
	}

	@Test
	public void testData() throws IOException {
		BufferedStream b = new BufferedStream(1024 * 16);
		Modulator m = new Modulator(b);
		byte[] data = new byte[] { 42, -100, -1, 0 };
		m.writeData(data, data.length);
		m.writeEOF();

		b.reset();

		Demodulator d = new Demodulator(b, new Filter(new double[] { 1 }));
		ByteArrayOutputStream dst = new ByteArrayOutputStream();
		d.run(dst);
		assertArrayEquals(data, dst.toByteArray());
	}
}
