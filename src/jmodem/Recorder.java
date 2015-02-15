package jmodem;

import java.io.IOException;

public interface Recorder {

	public void read(float[] buffer, int offset, int size) throws IOException; 
	
}
