package jmodem;

import java.io.IOException;

public interface InputStream {

	public void read(float[] buffer, int offset, int size) throws IOException; 
	
}
