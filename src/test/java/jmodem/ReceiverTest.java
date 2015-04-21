package jmodem;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

public class ReceiverTest {

	@Test
	public void test() throws Exception {
		byte[] bytes = "foo bar\nxxyyzz\n".getBytes();

		BufferedStream b = new BufferedStream(48 * 1000);
		Sender s = new Sender(b);
		s.writeSilence(100);
		s.writePrefix();
		s.writeTraining();
		s.writeData(bytes, bytes.length);
		s.writeEOF();

		b.reset();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Receiver.run(b, out);

		assertArrayEquals(bytes, out.toByteArray());
	}

}
