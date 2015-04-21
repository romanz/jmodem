package jmodem;

import static org.junit.Assert.*;

import org.junit.Test;

public class DetectorTest {

	@Test
	public void test() throws Exception {
		BufferedStream b = new BufferedStream(1024 * 48);
		Modulator mod = new Modulator(b);
		mod.writeSilence(100);
		mod.writePrefix();
		int offset = b.offset;
		mod.writeTraining();
		b.reset();

		Detector d = new Detector(b);
		double[] prefix = d.run();
		assertEquals(offset, b.offset);
		assertEquals(prefix.length, Config.prefixLength * Config.symbolLength);

		Demodulator demod = new Demodulator(new BufferedStream(prefix), null);
		for (int i = 0; i < Config.prefixSymbols; i++) {
			Complex c = demod.getSymbol();
			assertEquals(c.real * c.real + c.imag * c.imag, 1, 1e-6);
		}
		for (int i = 0; i < Config.prefixSilence; i++) {
			Complex c = demod.getSymbol();
			assertEquals(c.real * c.real + c.imag * c.imag, 0, 1e-6);
		}
	}

}
