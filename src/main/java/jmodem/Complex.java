package jmodem;

public class Complex {
	public Complex(double realPart, double imagPart) {
		this.real = realPart;
		this.imag = imagPart;
	}

	@Override
	public String toString() {
		return "<" + real + ", " + imag + ">";
	}

	public double real;
	public double imag;
}
