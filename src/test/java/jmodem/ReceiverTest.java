package jmodem;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

public class ReceiverTest {

	@Test
	public void test() throws Exception {
		byte[] bytes = "foo bar\nxxyyzz\n".getBytes();

		BufferedStream b = new BufferedStream(48 * 1000);
		Modulator m = new Modulator(b);
		m.writeSilence(100);
		m.writePrefix();
		m.writeTraining();
		m.writeData(bytes, bytes.length);
		m.writeEOF();

		b.reset();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Receiver.run(b, out);

		assertArrayEquals(bytes, out.toByteArray());
	}

}
