package jmodem;

import java.io.IOException;

public class Sampler implements InputStream {
	
	public final int width = 128;
	public final int resolution = 1024;
	
	private final int N = width * resolution;	
	private final float[][] filt;
	private final int coeffs_len;
	private float time;
	private float freq;
	private float[] buff;
	private int index;
	private InputStream src;
	
	public Sampler(InputStream source) {
		src = source;
		float[] h = new float[2*N];
		for (int i = -N; i < N; i++) {
			double u = i;
			double window = Math.cos(0.5 * Math.PI * u / N);
			h[N+i] = (float)(sinc(Math.PI * u / resolution) * window);			
		}
		coeffs_len = 2 * width;
		filt = new float[resolution][coeffs_len];
		for (int i = 0; i < resolution; i++) {
			for (int j = 0; j < coeffs_len; j++) {
				filt[i][j] = h[i + (coeffs_len - 1 - j) * resolution];
			}
		}
		
		buff = new float[coeffs_len];  // zeroes (by default)
		index = width;
		time = width + 1;
		freq = 1f;
	}

	private double sinc(double d) {
		return d != 0 ? Math.sin(d) / d : 1.0;
	}

	@Override
	public void read(float[] buffer, int offset, int size) throws IOException {
		for (int o = offset; o < size; o++) {
			int k = (int)time;
			int j = (int)((time - k) * resolution);
			float[] coeffs = filt[j];		
			for (int end = k + width; index < end; index++) {
				System.arraycopy(buff, 1, buff, 0, buff.length - 1);
				src.read(buff, buff.length - 1, 1);  // push to buff's end
			}
			time += freq;
			double result = 0f;
			for (int i = 0; i < buff.length; i++) {
				result += (coeffs[i] * (double)buff[i]);				
			}
			buffer[o] = (float)result;			
		}
	}
	
	public void updateTime(float dt) {
		time += dt;
	}
	
	public void updateFreq(float df) {
		freq += df;
	}

}
