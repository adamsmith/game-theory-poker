/*
 * Created on Jun 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _io;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.*;
import _io.*;
import _misc.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WriteMPS {
	
	private String[] emptyStrings;
	private final static int numEmptyStrings = 65;
	
	public final static double logOneTenth = -1; // == Math.log10(.1)

	// column # for beginning character, minus one
	private final static int[] fieldOffset = new int[] { -1, 4, 14, 24, 39, 49 };
	
	private DataOutputStream out;
	

	public WriteMPS(String outFile, int bufferSize) throws IOException {
		initEmptyStrings();

		if(new File(outFile).exists()) {
			throw new RuntimeException("file already exists: " + outFile);
		}

		out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outFile), bufferSize));
		
	}
	
	private void initEmptyStrings() {
		if(emptyStrings == null) {
			emptyStrings = new String[numEmptyStrings];
			// assume numEmptyStrings > 0
			emptyStrings[0] = "";
			for(int i = 1; i < numEmptyStrings; i++) {
				emptyStrings[i] = emptyStrings[i-1].concat(" ");
			}
		}
	}
	

	private String m_cachedAddRowColumnName;
	private String m_cachedAddRowRowName;
	private float m_cachedAddRowValue;
	public void addElement(String columnName, String rowName, float value) throws IOException {
//		System.out.println("pulse: entering addElement");
		if(Float.isNaN(value)) {
			throw new RuntimeException();
		}
		if(m_cachedAddRowColumnName != null) {
			if(m_cachedAddRowColumnName.equals(columnName)) {
				// print both
				print(new String[] {null, columnName, m_cachedAddRowRowName, 
						floatToTrimString(m_cachedAddRowValue), rowName, 
						floatToTrimString(value)});
				m_cachedAddRowColumnName = null;
				return;
			} else {
				// print cached, copy these to cache
				print(new String[] {null, m_cachedAddRowColumnName, m_cachedAddRowRowName, 
						floatToTrimString(m_cachedAddRowValue)});
			}
		}

		m_cachedAddRowColumnName = columnName;
		m_cachedAddRowRowName = rowName;
		m_cachedAddRowValue = value;
//		System.out.println("pulse: leaving addElement");
	}
	
	public void flushAddElement() throws IOException {
		if(m_cachedAddRowColumnName != null) {
			print(new String[] {null, m_cachedAddRowColumnName, m_cachedAddRowRowName, 
					floatToTrimString(m_cachedAddRowValue)});
			m_cachedAddRowColumnName = null;
		}
	}

	private final static String strDecimalPoint = ".";
	private final static String strNegativeSign = "-";
	private final static String[] zeros = new String[] {"", "0", "00", "000", 
			"0000", "00000", "000000", "0000000", "00000000"};
	private final static int maxNumberStringLength = 9;
	
	public static String floatToTrimString(float x) {
//		System.out.println("pulse: entering floatToTrimString");
		if(Float.isNaN(x)) {
			throw new RuntimeException();
		}
		
		// save some computation
		if(x == -1) return "-1.";
		if(x == 1) return "1.";
		
		if(x > 10000) { 
			// make our lives easier  (according to Float.toString, this 
			//   threshold could be as high as 10^7, but we don't need that much room)
			throw new RuntimeException();
		}
		
		if(x == 0) {
//			System.out.println("WARNING: unexpected zero in floatToTrimString");
//			System.out.println("pulse: leaving floatToTrimString");
			return "0.";
		}
		
//		if(Math.abs(x) >= 0.001) { // 0.001 is from the Float.toString() javadocs
			String val = Float.toString(x);
			if(val.length() > maxNumberStringLength) {
//				System.out.println("pulse: leaving floatToTrimString");
				int expPos = val.indexOf("E");
				if(expPos == -1) {
					return val.substring(0, maxNumberStringLength);
				} else {
					int expSize = val.length() - expPos;
					return val.substring(0, expPos - (val.length() - maxNumberStringLength)) + 
							val.substring(expPos);
				}
			} else {
//				System.out.println("pulse: leaving floatToTrimString");
				return val;
			}
//		}
//		
//		// get rid of the exponent
//		boolean isNegative = false;
//		if(x < 0) {
//			isNegative = true;
//			x *= -1;
//		}
//		double log = Helper.log10(x);
//		log /= logOneTenth;
//		log = Math.ceil(log);
//		int multiplier = (int) Math.abs(log)-3; // assume x != 0
//		if(multiplier < 1) {
//			throw new RuntimeException();
//		}
//		for(int i = 0; i < multiplier; i++) {
//			x *= 10;
//			if(Float.isInfinite(x)) {
//				throw new RuntimeException();
//			}
//		}
//		String shifted = Float.toString(x);
//		// now, find the decimal point and shift it left 'multiplier' times
//		int pos = shifted.indexOf(strDecimalPoint);
//		if(pos < multiplier) {
//			shifted = zeros[multiplier-pos] + shifted;
//			pos += multiplier-pos;
//		}
//		shifted = shifted.substring(0, (pos - multiplier)) + strDecimalPoint + 
//				shifted.substring(pos - multiplier, pos) + shifted.substring(pos+1);
//		if(isNegative) {
//			shifted = strNegativeSign + shifted.substring(0, shifted.length()-1);
//		}
//		if(shifted.length() > maxNumberStringLength) {
//			return shifted.substring(0, maxNumberStringLength);
//		} else {
//			return shifted;
//		}
	}
	
	public void print(String[] fields) throws IOException {
//		System.out.println("pulse: entering print");
		String line = (fields[0] == null ? emptyStrings[0] : fields[0]);
//		System.out.println("pulse: entering print loop");
		for(int i = 1; i < fields.length; i++) {
			if(fields[i] != null) {
				line = padLength(line, fieldOffset[i]);
				line = line.concat(fields[i]);
			}
		}
//		System.out.println("pulse: leaving print loop");
		
		print(line);
	}
	
	public void print(String line) throws IOException {
//		System.out.println("pulse: writing bytes from WriteMPS.out()");
//		System.out.println("OUT: " + line);
		out.writeBytes(line + "\n");
//		System.out.println("pulse: DONE writing bytes from WriteMPS.out()");
//		System.out.println(line);
	}
	
	public void close() throws IOException {
		out.close();
	}
	
	private String padLength(String x, int len) {
		// assume x.length >= len
		
		if(x.length() == len) {
			return x;
		}
		return x.concat(emptyStrings[len-x.length()]);
	}
	
}
