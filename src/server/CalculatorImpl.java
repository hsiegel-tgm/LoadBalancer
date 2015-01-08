package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Vector;

import main.Log;
import client.Client;

/**
 * The class CalculatorImpl is the implementation of an calculator object.
 * Took it from last year's RMI task and modified it for cpu / ram / io ...
 * 
 * @author Hannah Siegel
 * @version 2014-12-30
 *
 */
public class CalculatorImpl implements Calculator, Serializable {
	private static final long serialVersionUID = 1L;
	private boolean m_busy; //TODO
	private Vector <StringBuffer> m_vectorRAM = new Vector<StringBuffer>();

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
		BigDecimal ret = pi.setScale(digits, BigDecimal.ROUND_HALF_UP);
		return ret;
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
	
	/**
	 * makes a random String Buffer
	 * 
	 * @return
	 */
	private static StringBuffer makeBuffer(){
		String H = "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
		StringBuffer b = new StringBuffer();
		for (int i = 0; i<= 10000;++i){
			b.append(H);
		}
		return b;
	}
	
	/**
	 * Compute the value of pi to the specified number of digits after the
	 * decimal point. The value is computed using Machin's formula:
	 * 
	 * pi/4 = 4*arctan(1/5) - arctan(1/239)
	 * 
	 * and a power series expansion of arctan(x) to sufficient precision.
	 */
	public BigDecimal computePiRAM(int digits) {
		//adding stuff to RAM
		StringBuffer b = makeBuffer();
		m_vectorRAM.addElement(b);
		return computePi(digits);

//		int scale = digits + 5;
//		BigDecimal arctan1_5 = arctan(5, scale);
//		BigDecimal arctan1_239 = arctan(239, scale);
//		BigDecimal pi = arctan1_5.multiply(FOUR).subtract(arctan1_239).multiply(FOUR);
//		return pi.setScale(digits, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * Compute the value of pi to the specified number of digits after the
	 * decimal point. The value is computed using Machin's formula:
	 * 
	 * pi/4 = 4*arctan(1/5) - arctan(1/239)
	 * 
	 * and a power series expansion of arctan(x) to sufficient precision.
	 */
	public BigDecimal computePiCPU(int digits) {
		//do some useless things
		computePi(digits*100000);
		
		return computePi(digits);
//		int scale = digits + 5;
//		BigDecimal arctan1_5 = arctan(5, scale);
//		BigDecimal arctan1_239 = arctan(239, scale);
//		BigDecimal pi = arctan1_5.multiply(FOUR).subtract(arctan1_239).multiply(FOUR);
//		return pi.setScale(digits, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * Compute the value of pi to the specified number of digits after the
	 * decimal point. The value is computed using Machin's formula:
	 * 
	 * pi/4 = 4*arctan(1/5) - arctan(1/239)
	 * 
	 * and a power series expansion of arctan(x) to sufficient precision.
	 */
	public BigDecimal computePiIO(int digits,String name) {
		StringBuffer b = makeBuffer();
		FileOutputStream fo = null;
		PrintWriter pw = null;
		try {
			File f = new File("C:/temp/myfile"+name+".txt");
			f.deleteOnExit();
			fo = new FileOutputStream(f);
			pw = new PrintWriter(fo,true);
			pw.println(b.toString());
			pw.close();
			fo.close();
		} catch (FileNotFoundException e) {
			Log.error("Could not find File", e);
		} catch (IOException e) {
			Log.error("There occured a IO Exception...", e);
		}
		
		return computePi(digits);
		
		//int scale = digits + 5;
		//BigDecimal arctan1_5 = arctan(5, scale);
		//BigDecimal arctan1_239 = arctan(239, scale);
		//BigDecimal pi = arctan1_5.multiply(FOUR).subtract(arctan1_239).multiply(FOUR);
		//return pi.setScale(digits, BigDecimal.ROUND_HALF_UP);
	}
	
	public BigDecimal pi(int digits,Type type,String c) {
		m_busy = true;
		BigDecimal res = null;
		if(type == Type.NORMAL){
			res = computePi(digits);
		}
		else if(type == Type.CPU){
			res = computePiCPU(digits);
		}
		else if(type == Type.RAM){
			res = computePiRAM(digits);
		}
		else if(type == Type.IO){
			res = computePiIO(digits,c);
		}
		
		m_busy = false;
		
		// returning PI
		return res;
	}

	public boolean isBusy() {
		return m_busy;
	}

	public BigDecimal pi(Type type,String c) {
		int digits = (int)((Math.random()*1000)+1); //TODO different.. ?  THIS METHOD SHOULD NEVER BE NEEDED...
		return pi(digits,type,c);
	}
	
	
	// 3 DIGITS
	//Server sp
	// hoert auf nach ersten
	
}
