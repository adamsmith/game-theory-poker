/*
 * Created on Aug 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage4;

import _io.*;
import _misc.*;
import stage3.*;
import stage3.InfoSet.*;

import java.util.*;
import java.io.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DoConvertSolsForRndAccess {

	
	public static final String ROOT_INPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
		"stage3" + Constants.dirSep;
	
	public static final String ROOT_OUTPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
		"stage3" + Constants.dirSep;
	
	private static final int MAX_SIMULT_FILES_OPEN = 1;
	
	private static final boolean deleteOld = true;

	public static void main(String[] args) throws Exception {
		double tTotal = System.currentTimeMillis();

		String[] subtreesToWrite;
		if(args.length == 0) {
			System.out.println("specify trees to preprocess.  [all-subtrees | some space-delimited subset of {a-g, root}]");
			throw new RuntimeException();
		}
		if(args[0].equals("all-subtrees")) {
			subtreesToWrite = Constants.subtreeNames;
		} else {
			subtreesToWrite = args;
		}
		
		for(int ixSubtree = 0; ixSubtree < subtreesToWrite.length; ixSubtree++) {
			System.out.println("CONVERTING SOLUTION FILES FOR SUBTREE " + subtreesToWrite[ixSubtree]);
			
			double tSubtree = System.currentTimeMillis();
			
			String inDir = ROOT_INPUT_DIR + subtreesToWrite[ixSubtree] + Constants.dirSep;
			String outDir = ROOT_OUTPUT_DIR + subtreesToWrite[ixSubtree] + Constants.dirSep;
			
			// solution files
			for(int p = 0; p < 2; p++) {
				boolean isP1 = (p == 0);
				String player = (isP1 ? "p1" : "p2");
				String awName = inDir + "game." + player + ".sol.obj";
				Map actionWeights = ReadBinarySolutionMap.getSolutionMap(awName, isP1);
				// Integers => Floats
				List lstActionWeights = new ArrayList(actionWeights.size());
				for(Iterator i = actionWeights.entrySet().iterator(); i.hasNext(); ) {
					lstActionWeights.add(i.next());
				}
				Collections.sort(lstActionWeights, new CompareWeights());
				String outName = outDir + "game." + player + ".sol.bin";
				if(deleteOld && new File(outName).exists()) {
					new File(outName).delete();
				}
				WriteBinaryActionWeightList.writeSolutionList(lstActionWeights, 
						outName, Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
			}
			
			// name maps
			for(int p = 0; p < 2; p++) {
				boolean isP1 = (p == 0);
				String player = (isP1 ? "p1" : "p2");
				String nmName = inDir + "nameMap." + player + ".obj";
				NameMap nm = ReadBinaryNameMap.getNameMap(nmName);
				Map longToShort = nm.longToShort;
				// InfoStrings => Integers
				List lstInfoStrings = new ArrayList();
				int maxLength = Integer.MIN_VALUE;
				for(Iterator i = longToShort.entrySet().iterator(); i.hasNext(); ) {
					Map.Entry record = (Map.Entry) i.next();
					
					InfoString longName = (InfoString) record.getKey();
					lstInfoStrings.add(longName); // we'll get the short names at the end
					
					if(longName.arr.length > maxLength) {
						maxLength = longName.arr.length;
					}
				}
				Collections.sort(lstInfoStrings, new CompareInfoStrings());
				String outName = outDir + "nameMap." + player + ".bin";
				if(deleteOld && new File(outName).exists()) {
					new File(outName).delete();
				}
				WriteBinaryNameList.writeNameList(lstInfoStrings, nm, maxLength, 
						outName, Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));			
			}
			
			System.out.println("done subtree in time: " + (System.currentTimeMillis() - tSubtree));
		}
		
		System.out.println("");
		System.out.println("done entire program in time: " + (System.currentTimeMillis() - tTotal));
	}
	
	public static class CompareWeights implements Comparator {
		public int compare(Object o1, Object o2) {
			// assume correct types
			Integer i1 = (Integer) ((Map.Entry) o1).getKey();
			Integer i2 = (Integer) ((Map.Entry) o2).getKey();
			return i1.compareTo(i2);
		}
	}
	
	public static class CompareInfoStrings implements Comparator {
		public int compare(Object o1, Object o2) {
			// assume correct types
			byte[] b1 = ((InfoString) o1).arr;
			byte[] b2 = ((InfoString) o2).arr;
			
			// higher array indices are more significant (big endian)
			//  (therefore shorter => smaller)
			if(b1.length < b2.length) {
				return -1;
			} else if(b1.length > b2.length) {
				return 1;
			} else {
				for(int i = b1.length - 1; i >= 0; i--) {
					if(b1[i] < b2[i]) {
						return -1;
					} else if(b1[i] > b2[i]) {
						return 1;
					}
				}
				return 0;
			}
		}
	}
	
}
