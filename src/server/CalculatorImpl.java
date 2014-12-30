package server;

import java.io.Serializable;
import java.math.BigDecimal;
import java.rmi.RemoteException;

public class CalculatorImpl implements Calculator, Serializable {

	private static final long serialVersionUID = 1L;
	private boolean m_busy;
	
	/** constants used in pi computation */
	private static final BigDecimal FOUR = BigDecimal.valueOf(4);

	/** rounding mode to use during pi computation */
	private static final int roundingMode = BigDecimal.ROUND_HALF_EVEN;

	/**
	 * Compute the value of pi to the specified number of digits after the
	 * decimal point. The value is computed using Machin's formula:
	 * 
	 * pi/4 = 4*arctan(1/5) - arctan(1/239)
	 * 
	 * and a power series expansion of arctan(x) to sufficient precision.
	 */
	public static BigDecimal computePi(int digits) {
		int scale = digits + 5;
		BigDecimal arctan1_5 = arctan(5, scale);
		BigDecimal arctan1_239 = arctan(239, scale);
		BigDecimal pi = arctan1_5.multiply(FOUR).subtract(arctan1_239).multiply(FOUR);
		return pi.setScale(digits, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * Compute the value, in radians, of the arctangent of the inverse of the
	 * supplied integer to the specified number of digits after the decimal
	 * point. The value is computed using the power series expansion for the arc
	 * tangent:
	 * 
	 * arctan(x) = x - (x^3)/3 + (x^5)/5 - (x^7)/7 + (x^9)/9 ...
	 */
	public static BigDecimal arctan(int inverseX, int scale) {
		BigDecimal result, numer, term;
		BigDecimal invX = BigDecimal.valueOf(inverseX);
		BigDecimal invX2 = BigDecimal.valueOf(inverseX * inverseX);

		numer = BigDecimal.ONE.divide(invX, scale, roundingMode);

		result = numer;
		int i = 1;
		do {
			numer = numer.divide(invX2, scale, roundingMode);
			int denom = 2 * i + 1;
			term = numer.divide(BigDecimal.valueOf(denom), scale, roundingMode);
			if ((i % 2) != 0) {
				result = result.subtract(term);
			} else {
				result = result.add(term);
			}
			i++;
		} while (term.compareTo(BigDecimal.ZERO) != 0);
		return result;
	}

	public BigDecimal pi(int digits,Type type) {
		// setting the server busy
		m_busy = true;
		// computing PI
		BigDecimal res = computePi(digits);
		// setting the server free
		m_busy = false;
		// returning PI
		return res;
	}

	public boolean isBusy() {
		return m_busy;
	}

	public BigDecimal pi(Type type) {
		int digits = (int)((Math.random()*1000)+1);
		return pi(digits,type);
	}

	public BigDecimal pi_cpu() {
		return null;
	}

	public BigDecimal pi_io() {
		return null;
	}

	public BigDecimal pi_ram() {
		return null;
	}

	public BigDecimal pi_sessionPers() {
		return null;
	}
}
