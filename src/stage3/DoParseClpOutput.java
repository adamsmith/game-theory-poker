/*
 * Created on Jul 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3;

import _misc.*;
import _io.*;

import java.io.*;
import java.io.FileReader;
import java.util.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DoParseClpOutput {
	
	public static final String ROOT_INPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
			"stage3" + Constants.dirSep;
	
	public static final String ROOT_OUTPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
			"stage3" + Constants.dirSep;
	
	private static final int MAX_SIMULT_FILES_OPEN = 2;
	
	private final static Float floatZero = new Float(0);
	
	private final static String solverName = "bpmpd";
	
	private final static int bpmpdPrefixLines = 13;

	public static void main(String[] args) throws Exception {
		String[] subtreesToWrite;
		if(args.length == 0) {
			System.out.println("specify tree solutions to parse.  [all-subtrees | some space-delimited subset of {a-g, root}]");
			throw new RuntimeException();
		}
		if(args[0].equals("all-subtrees")) {
			subtreesToWrite = Constants.subtreeNames;
		} else {
			subtreesToWrite = args;
		}
		
		double tTotal = System.currentTimeMillis();
		for(int ixSubtree = 0; ixSubtree < subtreesToWrite.length; ixSubtree++) {
			System.out.println("PARSING SOLUTIONS FOR SUBTREE " + subtreesToWrite[ixSubtree]);
			
			String inDir = ROOT_INPUT_DIR + subtreesToWrite[ixSubtree]
					+ Constants.dirSep;
			String outDir = ROOT_OUTPUT_DIR + subtreesToWrite[ixSubtree]
					+ Constants.dirSep;

			String[] inFileSuffixes = new String[] {"game.p1.sol", "game.p2.sol"};
			double tSubtree = System.currentTimeMillis();
			
			for(int file = 0; file < inFileSuffixes.length; file++) {
				
				double tFile = System.currentTimeMillis();
				
				char varPrefix;
				// sample file name: main.p1.sol
				System.out.println(inFileSuffixes[file]);
				String[] inNameParts = inFileSuffixes[file].split("\\.");
				boolean isP1Solution;
				if(inNameParts[1].equals("p1")) {
					isP1Solution = true;
				} else if(inNameParts[1].equals("p2")) {
					isP1Solution = false;
				} else {
					throw new RuntimeException();
				}
				if(isP1Solution) {
					varPrefix = "x".charAt(0);
				} else {
					varPrefix = "y".charAt(0);
				}
				
				BufferedReader in = new BufferedReader(new FileReader(inDir + 
						inFileSuffixes[file]),
						Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
				String line;
				String lineArray[];

				Map weights = new HashMap();
				
				if(solverName.equals("bpmpd")) {
					for(int i = 0; i < bpmpdPrefixLines; i++) {
						in.readLine();
					}
				}

				while ((line = in.readLine()) != null) {
					lineArray = line.split("\\s+");
					
					if(solverName.equals("clp")) {
						if(!processLineClp(weights, lineArray, varPrefix)) {
							break;
						}
					} else if(solverName.equals("bpmpd")) {
						if(!processLineBpmpd(weights, lineArray, varPrefix)) {
							break;
						}
					}

				}
				
				in.close();
				
				String outFile = outDir + inFileSuffixes[file] + ".obj";
				WriteBinarySolutionMap.writeSolutionMap(outFile, weights, isP1Solution);
				
				System.out.println("File " + inFileSuffixes[file] + " (" + weights.size() + 
						" records) done in time: " + (System.currentTimeMillis() - tFile));
			}
			
			System.out.println("All files in subtree done in time: " + (System.currentTimeMillis() - tSubtree));
			System.out.println("");
			System.out.println("");
		}
		System.out.println("");
		System.out.println("");
		System.out.println("Entire program done in time: " + (System.currentTimeMillis() - tTotal));
	}
	
	private static boolean processLineBpmpd(Map weights, String[] lineArray, char varPrefix) {
		if(lineArray.length == 1) {
			if(lineArray[0].equals("")) {
				return true;
			} else {
				throw new RuntimeException();
			}
		} else if(lineArray.length != 4 && lineArray.length != 5) {
			if(lineArray[1].substring(0,1).equals("-")) {
				// this is the bottom line, like:
				//  -----------------------------------------------------
				// or the beginning of another section, like:
				//  ---------------S-L-A-C-K---R-E-P-O-R-T---------------
				return false;
			}
			throw new RuntimeException();
		}
		if(lineArray[1].charAt(0) != varPrefix) {
			return true;
		}
		
		Integer name = new Integer(lineArray[1].substring(1));
		Float value = new Float(lineArray[2]);
		
		addToMap(weights, name, value);
		return true;
	}
	
	private static boolean processLineClp(Map weights, String[] lineArray, char varPrefix) {
		if(lineArray.length != 5) {
			throw new RuntimeException();
		}
		if(lineArray[0].equals("**")) {
			if(Math.abs(Float.valueOf(lineArray[3]).floatValue()) > 1e-4) {
				throw new RuntimeException();
			} else {
				// do nothing
				return true;
			}
		}
		if(!lineArray[0].equals("")) {
			throw new RuntimeException();
		}
		if(lineArray[2].charAt(0) != varPrefix) {
			return true;
		}
		
		Integer name = new Integer(lineArray[2].substring(1));
		Float value = new Float(lineArray[3]);
		
		addToMap(weights, name, value);
		return true;
	}
	
	private static void addToMap(Map weights, Integer name, Float value) {
		if(value.compareTo(floatZero) < 0) {
			// value is negative
			if(Math.abs(value.floatValue()) > 1e-4) {
				throw new RuntimeException();
			} else {
				return;
			}
		}
		
//		System.out.println(name + " -> " + value);
		weights.put(name, value);
	}
}
