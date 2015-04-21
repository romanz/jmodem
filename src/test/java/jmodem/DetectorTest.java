package jmodem;

import static org.junit.Assert.*;

import org.junit.Test;

public class DetectorTest {

	@Test
	public void test() throws Exception {
		BufferedStream b = new BufferedStream(1024 * 48);
		Sender s = new Sender(b);
		s.writeSilence(100);
		s.writePrefix();
		int offset = b.offset;
		s.writeTraining();
		b.reset();

		Detector d = new Detector(b);
		double[] prefix = d.run();
		assertEquals(offset, b.offset);
		assertEquals(prefix.length, Config.prefixLength * Config.symbolLength);

		Demodulator m = new Demodulator(new BufferedStream(prefix), null);
		for (int i = 0; i < Config.prefixSymbols; i++) {
			Complex c = m.getSymbol();
			assertEquals(c.real * c.real + c.imag * c.imag, 1, 1e-6);
		}
		for (int i = 0; i < Config.prefixSilence; i++) {
			Complex c = m.getSymbol();
			assertEquals(c.real * c.real + c.imag * c.imag, 0, 1e-6);
		}
	}

}
