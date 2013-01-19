/*
 * Created on Aug 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage4;

import java.io.IOException;
import java.io.RandomAccessFile;

import stage3.*;
import stage3.InfoSet.*;
import _misc.Constants;
import stage4.DoConvertSolsForRndAccess.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WeightResolver {
	
	public static final String ROOT_INPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
			"stage3" + Constants.dirSep;
	
	private static final int MAX_SIMULT_FILES_OPEN = 1;
	
	private static CompareInfoStrings cmpInfoStrings = 
			new DoConvertSolsForRndAccess.CompareInfoStrings();
	
	private static WeightResolver instance = null;
	public static WeightResolver getWeightResolver() throws IOException {
		if(instance == null) {
			instance = new WeightResolver();
		}
		return instance;
	}
	
	private String subtreeDir = "";
	
	public void setSubtreeName(String subtreeName) {
		subtreeDir = subtreeName + Constants.dirSep;
	}
	
	public float getWeight(InfoString x, boolean isP1) throws IOException {
		int shortName;
		try {
			shortName = getShortName(x, isP1); // level of indirection
		} catch (RuntimeException rte) {
			// note: this happens when we are asked for a move that is illegal
			//  such as raise after there have already been three raises in this
			//  betting round.
			// you might want to disable this if/when debugging.
			return 0;
		}
		if(shortName < 0) {
			throw new RuntimeException();
		}
		return getWeightOfShortName(shortName, isP1);
	}
	
	public static void main(String[] args) throws IOException {
		WeightResolver yo = getWeightResolver();
		
//		InfoString isTst = new InfoString(new byte[] {
//				-79, 48, 41, 42, 43, 44, 53, -48, 72, 73, 82, -14, 104, 105, 106, 99});
//		yo.setSubtreeName("g");
//		System.out.println(yo.getShortName(isTst, true));
//		System.out.println(yo.getWeight(isTst, true));
		

		InfoString isTst2 = new InfoString(new byte[] {
				-111, 0});
		yo.setSubtreeName("root");
		System.out.println(yo.getShortName(isTst2, true));
		System.out.println(yo.getWeightOfShortName(22552, true));
		System.out.println(yo.getWeight(isTst2, true));
	}
	
	private float getWeightOfShortName(int x, boolean isP1) throws IOException {
		String player = (isP1 ? "p1" : "p2");
		
		// first, look up short name in name map
		String inName = ROOT_INPUT_DIR + subtreeDir + "game." + player + ".sol.bin";
		RandomAccessFile in = new RandomAccessFile(inName, "r");
		if(in.readShort() != Constants.vidSolutionList) {
			throw new RuntimeException();
		}
		short tmp = in.readShort();
		int listSize = in.readShort();
		listSize <<= 16;
		listSize |= tmp & 0xFFFF;
		
		// binary search
		int ixLow = 0;
		int ixHigh = listSize - 1;
		final int bytesPerRecord = 8;
		while(true) {
			if(ixLow > ixHigh) {
				throw new RuntimeException("not in file -- if LP solver doesn't write out " +
						"zero answers then you should return 0 here");
			}
			
			// seek to halfway between ixLow and ixHigh
			int ixMiddle = ixLow + Math.round((float)(ixHigh - ixLow) / 2);
			int seekAddr = Constants.FILE_HEADER_LENGTH_BYTES + bytesPerRecord * ixMiddle;
			in.seek(seekAddr);
			
			// determine if we're too high, too low, or right on
			int record = in.readInt();
			if(x == record) {
				// a match!
				return in.readFloat();
			} else if(x < record) {
				ixHigh = ixMiddle - 1;
			} else {
				ixLow = ixMiddle + 1;
			}
		}
	}
	
	private int getShortName(InfoString x, boolean isP1) throws IOException {
		String player = (isP1 ? "p1" : "p2");
		
		// first, look up short name in name map
		String inName = ROOT_INPUT_DIR + subtreeDir + "nameMap." + player + ".bin";
		RandomAccessFile in = new RandomAccessFile(inName, "r");
		if(in.readShort() != Constants.vidNameList) {
			throw new RuntimeException();
		}
		short tmp = in.readShort();
		int listSize = in.readShort();
		listSize <<= 16;
		listSize |= tmp & 0xFFFF;
		int maxLongNameLength = in.readShort();
		
		// binary search
		int ixLow = 0;
		int ixHigh = listSize - 1;
		final int bytesPerRecord = maxLongNameLength + 4;
		while(true) {
			if(ixLow > ixHigh) {
				throw new RuntimeException("not in file");
			}
			
			// seek to halfway between ixLow and ixHigh
			int ixMiddle = ixLow + Math.round((float)(ixHigh - ixLow) / 2);
			int seekAddr = Constants.FILE_HEADER_LENGTH_BYTES + bytesPerRecord * ixMiddle;
			in.seek(seekAddr);
			
			// determine if we're too high, too low, or right on
			byte[] record = null;
			for(int i = maxLongNameLength - 1; i >= 0 ; i--) {
				byte byt = in.readByte();
				if(record == null) {
					if(byt != InfoToken.reservedToken) {
						record = new byte[i+1];
						record[i] = byt;
					}
				} else {
					// big endian
					record[i] = byt;
				}
			}
			if(record == null) {
				throw new RuntimeException();
			}
			InfoString iSRecord = new InfoString(record);
			int cmp = cmpInfoStrings.compare(x, iSRecord);
			if(cmp == 0) {
				// a match!
				return in.readInt();
			} else if(cmp < 0) {
				ixHigh = ixMiddle - 1;
			} else {
				ixLow = ixMiddle + 1;
			}
		}
	}	
}
