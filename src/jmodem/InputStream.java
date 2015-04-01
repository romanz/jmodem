package jmodem;

import java.io.IOException;

public interface InputStream {

	public void read(double[] buffer, int offset, int size) throws IOException;

}
