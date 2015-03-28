package jmodem;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class SamplerTest {
	
	@Test
	public void test() throws IOException {		
		BufferInputStream r = new BufferInputStream(3000);
		r.buffer[1000] = 1;
		
		float[] b = new float[2000];
		
		Sampler s = new Sampler(r);
		s.updateFreq(0);		
		s.read(b, 0, b.length);
		assertTrue(true);
	}
}
