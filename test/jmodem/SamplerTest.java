package jmodem;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class SamplerTest {
	
	class RecorderTest implements Recorder {

		public float[] buffer;
		int offset;

		RecorderTest(int size) {
			buffer = new float[size];
			offset = 0;
		}
		
		@Override
		public void read(float[] buffer, int offset, int size) throws IOException {
			if (this.offset + size > this.buffer.length) {
				throw new IOException("EOF");
			}
			System.arraycopy(this.buffer, this.offset, buffer, offset, size);
			this.offset += size;
		}
		
	}

	@Test
	public void test() throws IOException {		
		RecorderTest r = new RecorderTest(2048);
		for (int i = 0; i < r.buffer.length; i++) {
			r.buffer[i] = i;
		}
		float[] b = new float[1024];
		
		Sampler s = new Sampler(r);
		s.updateTime(1e-3f);
		
		s.read(b, 0, b.length);		
		for (int i = 0; i < b.length; i++) {
			assertEquals(i + s.width, b[i], 1e-2);
		}
		try {
			s.read(b, 0, b.length);
			fail("should raise");
		} catch (IOException e) {
			assert e.getMessage() == "EOF";
		}
	}

}
