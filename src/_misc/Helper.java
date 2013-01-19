/*
 * Created on Jun 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _misc;

import java.io.File;

import _game.Card;
import _io.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Helper {
	
	public static void tryToFreeMemory() {
		// note: the linux jvm is slow to gc, so we call it many times.  It works
		//   with only one call to gc() under the windows jvm
		
		// note 2: it's much better to overkill here than to throw a memory exception
		//   during runtime and lose those calculations
		
		// note 3: on the other hand, there have been OutOfMemoryErrors from this
		//   code since it creates new threads (over runs the small native thread 
		//   address space, or something like that.
//		System.runFinalization();
		for(int i = 0; i < 3; i++) {
			System.gc();
		}
		System.runFinalization();
		for(int i = 0; i < 3; i++) {
			System.gc();
		}
	}
	
	public static String byteArrayToString(byte[] x) {
		String y = "[";
		for(int i = 0; i < x.length; i++) {
			y = y.concat(x[i] + ", ");
		}
		y = y.substring(0, y.length() - 2);
		y = y + "]";
		return y;
	}
	
	public static String floatArrayToString(float[] x) {
		String y = "[";
		for(int i = 0; i < x.length; i++) {
			y = y.concat(x[i] + ", ");
		}
		y = y.substring(0, y.length() - 2);
		y = y + "]";
		return y;
	}
	
	public static byte getNextBoardCardCount(int x, boolean tolerateLast) {
		switch(x) { 
		case 0 : return 3;
		case 3 : return 4;
		case 4 : return 5;
		case 5 : 
			if(tolerateLast){
				return Byte.MAX_VALUE;
			} else {
				throw new RuntimeException();
			}
		default : throw new RuntimeException(); }
	}
	
	public static byte getPreviousBoardCardCount(int x, boolean tolerateFirst) {
		switch(x) { 
		case 0 : 
			if(tolerateFirst){
				return Byte.MIN_VALUE;
			} else {
				throw new RuntimeException();
			}
		case 3 : return 0;
		case 4 : return 3;
		case 5 : return 4;
		default : throw new RuntimeException(); }
	}
	
	public static long overflowedIntVal(int x) {
		long y = Integer.MAX_VALUE;
		y += x - Integer.MIN_VALUE + 1;
		return y;
	}
	
	public static void prepFilePath(String file) {
		File dir = new File(dirFromFileName(file));
		if(!dir.exists()) {
			dir.mkdir();
		}
		
		File tmpFileObj = new File(file);
		if(tmpFileObj.exists()) {
			if(tmpFileObj.length() == 0) {
				tmpFileObj.delete();
			} else {
				throw new RuntimeException("output file (" + file + ") already exists and is non-zero length!");
			}
		}
	}
	
	public static int getBufferSize(int maxSimultFilesOpen) {
		return Math.min(Constants.MAX_SINGLE_FILE_BUFFER, 
				(int)Math.floor(((double)
				Constants.TOTAL_BUFFER_AVAILABLE / maxSimultFilesOpen)));
	}
	
	public static int hashByteArray(byte[] array) {
		int result = 0;
		for(int i = 0; i < array.length; i++) {
			result += array[i];
		}
		
		return result;
	}
	
	public static int hashIntArray(int[] array) {
		int result = 0;
		for(int i = 0; i < array.length; i++) {
			result += array[i];
		}
		
		return result;
	}
	
	public static byte[] appendToByteArray(byte[] x, byte toAppend) {
		byte[] y = new byte[x.length + 1];
		System.arraycopy(x, 0, y, 0, x.length);
		y[x.length] = toAppend;
		return y;
	}
	
	public static Integer[] intToIntegerArray(int[] x) {
		Integer[] y = new Integer[x.length];
		for(int i = 0; i < x.length; i++) {
			y[i] = new Integer(x[i]);
		}
		return y;
	}
	
	public static String dirFromFileName(String fileName) {
		return fileName.substring(0, fileName.lastIndexOf("" + Constants.dirSep) + 1);
	}
	
	public static int sqr(int x) {
		return x*x;
	}
	
	public static float sqr(float x) {
		return x*x;
	}
	
	public static double sqr(double x) {
		return x*x;
	}
	
	public static long sqr(long x) {
		return x*x;
	}
	
	public static boolean allTrue(boolean[] x) {
		for(int i = 0; i < x.length; i++) {
			if(x[i] == false) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean contains(byte[] haystack, byte needle) {
		for(int i = 0; i < haystack.length; i++) {
			if(haystack[i] == needle) {
				return true;
			}
		}
		
		return false;
	}

	// must return ordered array
	public static byte[] getRemainingCards(byte[] notEligible) {
		// figure out possible cards
		byte[] remainingCards = new byte[Card.NUM_CARDS - notEligible.length];
		int remainingCardsPointer = 0;
		boolean add;
		for(byte i = 0; i < Card.NUM_CARDS; i++) {
			add = true;
			for(int j = 0; j < notEligible.length; j++) {
				if(notEligible[j] == i) {
					add = false;
					break;
				}
			}
			if (add) {
				remainingCards[remainingCardsPointer] = i;
				remainingCardsPointer++;
			}
		}
		return remainingCards;
	}

	public static byte[] mergeByteArrays(byte[] x, byte[] y) {
		byte[] rtn = new byte[x.length + y.length];
		System.arraycopy(x, 0, rtn, 0, x.length);
		System.arraycopy(y, 0, rtn, x.length, y.length);
//		for(int i = 0; i < rtn.length; i++) {
//			if(i < x.length) {
//				rtn[i] = x[i];
//			} else {
//				rtn[i] = y[i-x.length];
//			}
//		}
		return rtn;
	}
	
	public static byte[] mergeOrderedByteArrays(byte[] x, byte[] y) {
		// x in increasing order, y in increasing order
		// return x U y in increasing order (don't worry about if x.a == y.b)
		
		byte[] rtn = new byte[x.length + y.length];
		int xPointer = 0;
		int yPointer = 0;
		int rtnPointer = 0;
		while(xPointer < x.length || yPointer < y.length) {
			if(xPointer == x.length) {  // done with x
				rtn[rtnPointer++] = y[yPointer++];
			} else if(yPointer == y.length) {
				rtn[rtnPointer++] = x[xPointer++];
			} else {
				if(x[xPointer] < y[yPointer]) {
					rtn[rtnPointer++] = x[xPointer++];
				} else {
					rtn[rtnPointer++] = y[yPointer++];
				}
			}
		}
		
		return rtn;
	}

	public static int getComboArrayIndex(int numCards, int numHoleCards, byte[] comboIndexes) {
		// Might throw RuntimeException if k > 12
	
		byte[] indexes = new byte[numHoleCards+1];
		indexes[0] = 0;
		for(byte i = 0; i < numHoleCards; i++) {
			indexes[i+1] = (byte) (comboIndexes[i]+1);
		}
		
		int answer = 0;
		for(byte j = 1; j <= numHoleCards; j++) {
			for(byte i = 1; i <= indexes[j]-indexes[j-1]-1; i++) {
				int tmp = 1;
				for(int p = numCards-indexes[j-1]-i-numHoleCards+j+1; p <= numCards-indexes[j-1]-i; p++) {
					tmp *= p;
				}
				answer += tmp / Constants.factTo12[numHoleCards-j];
			}
		}
		return answer;
	}

}
