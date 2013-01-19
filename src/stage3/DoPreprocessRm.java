/*
 * Created on Jul 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3;

import _misc.*;
import _io.*;
import java.util.*;
import java.io.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DoPreprocessRm {
	
	public static final String ROOT_INPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
			"stage3" + Constants.dirSep;
	
	public static final String ROOT_OUTPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
			"stage3" + Constants.dirSep;
	
	private static final int MAX_SIMULT_FILES_OPEN = 4;

	private static int[] numClusters;
	private static int maxNumBoardCards;
	
	private static String currentWorkingFileName;

	private static void pruneRewardMatrix(String outName, int bufSize) throws IOException {
		// remove duplicates
		//   (we're not worried about removing records with value==0 since that is now
		//    done in the previous step)
		
		ReadBinaryRmStream in = new ReadBinaryRmStream(currentWorkingFileName, bufSize, true);
		
		WriteBinaryRmeStream out = 
			new WriteBinaryRmeStream(outName, bufSize, numClusters, maxNumBoardCards);

		in.getRmeViaContainer();
		int firstDim = -1;
		int secondDim = -1;
		float val;
		while(in.container != null) {
			if(in.container.firstDim == firstDim) {
				if(in.container.secondDim == secondDim) {
					in.getRmeViaContainer();
					continue;
				}
			}
			
			firstDim = in.container.firstDim;
			secondDim = in.container.secondDim;
			val = in.container.value;
			
			out.writeRme(firstDim, secondDim, val);
			
			in.getRmeViaContainer();
		}
		
		out.close();
		in.close();
		
		new File(currentWorkingFileName).delete();
	}
	
	private static void populateMetaData(String inFile) throws IOException {
		ReadBinaryRmStream in = new ReadBinaryRmStream(inFile, 10000, true);
		numClusters = in.numClusters;
		maxNumBoardCards = in.maxNumBoardCards;
		in.close();
		in = null;
	}
	
	private static void transposeMatrix(String inFile, String outFile, int bufSize) 
			throws IOException {
		
		populateMetaData(inFile);
		
		ReadBinaryRmStream in = new ReadBinaryRmStream(inFile, bufSize, true);
		
		File fOut = new File(outFile);
		int counter = 0;
		while(fOut.exists()) {
			fOut.delete();
			if(counter++ == 2000) {
				throw new RuntimeException();
			}
		}
		
		WriteBinaryRmeStream out = 
			new WriteBinaryRmeStream(outFile, bufSize, numClusters, maxNumBoardCards);

		in.getRmeViaContainer();
		int firstDim = -1;
		int secondDim = -1;
		float val;
		while(in.container != null) {
			
			secondDim = in.container.firstDim;
			firstDim = in.container.secondDim;
			val = in.container.value;
			
			out.writeRme(firstDim, secondDim, val);
			
			in.getRmeViaContainer();
		}
		
		out.close();
		in.close();
	}
	
	private static void negateMatrix(String inFile, String outFile, int bufSize) 
			throws IOException {
		
		ReadBinaryRmStream in = new ReadBinaryRmStream(inFile, bufSize, true);
		
		WriteBinaryRmeStream out = 
			new WriteBinaryRmeStream(outFile, bufSize, numClusters, maxNumBoardCards);

		in.getRmeViaContainer();
		int firstDim = -1;
		int secondDim = -1;
		float val;
		while(in.container != null) {
			
			firstDim = in.container.firstDim;
			secondDim = in.container.secondDim;
			val = -1*in.container.value;
			
			out.writeRme(firstDim, secondDim, val);
			
			in.getRmeViaContainer();
		}
		
		out.close();
		in.close();
	}
	
	private static void sortRewardMatrix(String inFile, int bufSize) throws IOException {
		ReadBinaryRmStream in = new ReadBinaryRmStream(inFile, bufSize, false);
		numClusters = in.numClusters;
		maxNumBoardCards = in.maxNumBoardCards;
		
		int recordCount = (int) (new File(inFile).length() - Constants.HEADER_SIZE ) /
				RewardMatrixElement.RECORD_SIZE;

		RewardMatrixElement[] toBeSorted = new RewardMatrixElement[recordCount];
		for(int i = 0; i < recordCount; i++) {
			toBeSorted[i] = in.getRme();
			
			if(toBeSorted[i] == null) {
				// recordCount is too high
				throw new RuntimeException();
			}
		}
		if(in.getRme() != null) {
			// recordCount is too low
			throw new RuntimeException();
		}
		in.close();
		
		Arrays.sort(toBeSorted);

		String outFileName = inFile + ".sorted";
		WriteBinaryRmeStream out = new WriteBinaryRmeStream(outFileName, 
				bufSize, numClusters, maxNumBoardCards);
		for(int i = 0; i < recordCount; i++) {
			out.writeRme(toBeSorted[i]);
		}
		out.close();
		
		currentWorkingFileName = outFileName;
	}
	
	private static void sortRewardMatrixMemLite(String inFile, String outDir, int bufSize) throws IOException {
		ReadBinaryRmStream[] in = new ReadBinaryRmStream[] {
				new ReadBinaryRmStream(inFile, bufSize, true)};
		numClusters = in[0].numClusters;
		maxNumBoardCards = in[0].maxNumBoardCards;
		String[] outName = new String[] {outDir + "rewardMatrix_0_a",
				outDir + "rewardMatrix_0_b"};
		for(int i = 0; i < 2; i++) {
			File out = new File(outName[i]);
			int counter = 0;
			while(out.exists()) {
				out.delete();
				if(counter++ == 2000) {
					throw new RuntimeException();
				}
			}
		}
		WriteBinaryRmeStream[] out = new WriteBinaryRmeStream[] {
				new WriteBinaryRmeStream(outName[0], bufSize, numClusters, maxNumBoardCards),
				new WriteBinaryRmeStream(outName[1], bufSize, numClusters, maxNumBoardCards)				
		};

		long wholeFileSize = 0;
		int outPointer = 0;
		in[0].getRmeViaContainer();
		while(in[0].container != null) {
			out[outPointer].writeRme(in[0].container.firstDim, in[0].container.secondDim, 
					in[0].container.value);
			outPointer ^= 1;
			wholeFileSize++;
			in[0].getRmeViaContainer();
		}
		out[0].close();
		out[1].close();
		in[0].close();
		in = null;
		
		// ...now we're split
		// don't delete original (for backup purposes)
		// start doing merge sort...

		long prevMergeSize = 1;
		long mergeSize = 2;
		int iterCounter = 1;
		while(prevMergeSize < wholeFileSize) {

			// load up output files
			outName = new String[] {outDir + "rewardMatrix_" + iterCounter + "_a",
					outDir + "rewardMatrix_" + iterCounter + "_b"};
			out = new WriteBinaryRmeStream[] {
					new WriteBinaryRmeStream(outName[0], bufSize, numClusters, maxNumBoardCards),
					new WriteBinaryRmeStream(outName[1], bufSize, numClusters, maxNumBoardCards)				
			};

			// load up input files
			String[] inName = new String[] {outDir + "rewardMatrix_" + (iterCounter-1) + "_a",
					outDir + "rewardMatrix_" + (iterCounter-1) + "_b"};
			in = new ReadBinaryRmStream[] {
					new ReadBinaryRmStream(inName[0], bufSize, true),
					new ReadBinaryRmStream(inName[1], bufSize, true)
			};
			in[0].getRmeViaContainer();
			in[1].getRmeViaContainer();
			
			outPointer = 0;
			int inPointer = -1;

			try {
				while(true) {
					long[] counter = new long[] {0, 0};
					long i;
					for(i = 0; i < mergeSize; i++) {
						inPointer = (in[0].container.compareTo(in[1].container) < 0 ? 0 : 1);
						
						out[outPointer].writeRme(in[inPointer].container.firstDim, 
								in[inPointer].container.secondDim, in[inPointer].container.value);
						in[inPointer].getRmeViaContainer();
						
						if(++counter[inPointer] == prevMergeSize) {
							inPointer ^= 1;
							i++;
							break;
						}
					}
					for( ; i < mergeSize; i++) {
						out[outPointer].writeRme(in[inPointer].container.firstDim, 
								in[inPointer].container.secondDim, in[inPointer].container.value);
						in[inPointer].getRmeViaContainer();
					}
					
					outPointer ^= 1;
				}
			} catch (NullPointerException npe) {
				// we're reached the end of one input file...
				inPointer = (in[0].container == null ? 1 : 0);
				while(in[inPointer].container != null) {
					out[outPointer].writeRme(in[inPointer].container.firstDim, 
							in[inPointer].container.secondDim, in[inPointer].container.value);
					in[inPointer].getRmeViaContainer();
				}
			}
			if(in[0].container != null || in[1].container != null) {
				throw new RuntimeException();
			}
			
			// delete files from previous generation
			in[0].close();
			in[1].close();
			in = null;
			out[0].close();
			out[1].close();
			out = null;
			System.runFinalization();
			System.runFinalization();
			System.gc();
			System.gc();
			System.gc();
			new File(inName[0]).delete();
			new File(inName[1]).delete();
			
			
			// get ready for next iteration
			iterCounter++;
			prevMergeSize = mergeSize;
			mergeSize = 2 * mergeSize;
		}
		
		// done!
		File laggard = new File(outDir + "rewardMatrix_" + (iterCounter-1) + "_b");
		if(laggard.length() != Constants.HEADER_SIZE) { // file should only contain a header (no body)
			throw new RuntimeException();
		}
		
		int counter = 0;
		while(laggard.exists()) {
			laggard.delete();
			System.runFinalization();
			System.runFinalization();
			System.gc();
			System.gc();
			System.gc();
			if(counter++ == 10000) {
				throw new RuntimeException();
			}
		}
		
		currentWorkingFileName = outDir + "rewardMatrix_" + (iterCounter-1) + "_a";
	}
	
	public static void main(String[] args) throws IOException {
		String[] subtreesToPreprocess;
		boolean memLite = false;
		if(args.length == 0 || !(args[0].equals("mem-lite") || args[0].equals("mem-heavy"))) {
			System.out.println("specify mem mode AND trees to preprocess:  [mem-lite | mem-heavy] . [all-subtrees | some space-delimited subset of {a-g, root}]");
			throw new RuntimeException();
		}
		if(args[0].equals("mem-lite")) {
			memLite = true;
		}
		if(args[1].equals("all-subtrees")) {
			subtreesToPreprocess = Constants.subtreeNames;
		} else {
			subtreesToPreprocess = new String[args.length - 1];
			System.arraycopy(args, 1, subtreesToPreprocess, 0, args.length - 1);
		}
		
		double tTotal = System.currentTimeMillis();
		
		for(int ixSubtree = 0; ixSubtree < subtreesToPreprocess.length; ixSubtree++) {
			
			// p1: transpose, sort (2nd col first), prune
			// p2: negate, sort (2nd col first), prune
			
			double tSubtree = System.currentTimeMillis();
			double tStage;
			
			String inDir = ROOT_INPUT_DIR + subtreesToPreprocess[ixSubtree]
					+ Constants.dirSep;
			String outDir = ROOT_OUTPUT_DIR + subtreesToPreprocess[ixSubtree]
					+ Constants.dirSep;
			String outName;

			String startInFile = inDir + "rewardMatrix";
			System.out.println("Preprocessing reward matrix (" + startInFile + ")");
			System.out.println("");
			System.out.println("P1:");
			System.out.println("   Transposing...");
			tStage = System.currentTimeMillis();
			
			String rewardMatrixTransposed = outDir + "rewardMatrix.transposed";
			transposeMatrix(startInFile, rewardMatrixTransposed, 
					Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
			
			System.out.println("   Finished transposing in time: " + 
					(System.currentTimeMillis() - tStage));
			System.out.println("");
			System.out.println("   Sorting...");
			tStage = System.currentTimeMillis();
			
			RewardMatrixElement.compareOnFirstDim = false;
			if(memLite) {
				sortRewardMatrixMemLite(rewardMatrixTransposed, outDir, 
						Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
			} else {
				sortRewardMatrix(rewardMatrixTransposed, 
						Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
			}
			new File(rewardMatrixTransposed).delete();
			
			System.out.println("   Finished sorting in time: " + 
					(System.currentTimeMillis() - tStage));
			System.out.println("");
			System.out.println("   Pruning...");
			tStage = System.currentTimeMillis();
			
			outName = outDir + "rewardMatrix.preprocessed.p1";
			pruneRewardMatrix(outName, Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
			
			System.out.println("   Finished pruning in time: " + 
					(System.currentTimeMillis() - tStage));
			System.out.println("");
			System.out.println("P2:");
			populateMetaData(startInFile);
			System.out.println("   Negating...");
			tStage = System.currentTimeMillis();

			RewardMatrixElement.compareOnFirstDim = false;
			outName = outDir + "rewardMatrix.negated";
			negateMatrix(startInFile, outName, Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
			
			System.out.println("   Finished negating in time: " + 
					(System.currentTimeMillis() - tStage));
			System.out.println("");
			System.out.println("   Sorting...");
			tStage = System.currentTimeMillis();

			RewardMatrixElement.compareOnFirstDim = false;
			if(memLite) {
				sortRewardMatrixMemLite(outName, outDir, 
						Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
			} else {
				sortRewardMatrix(outName, 
						Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
			}
			new File(outName).delete();
			
			System.out.println("   Finished sorting in time: " + 
					(System.currentTimeMillis() - tStage));
			System.out.println("");
			System.out.println("   Pruning...");
			tStage = System.currentTimeMillis();
			
			outName = outDir + "rewardMatrix.preprocessed.p2";
			pruneRewardMatrix(outName, Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
			
			System.out.println("   Finished pruning in time: " + 
					(System.currentTimeMillis() - tStage));
			System.out.println("");
			System.out.println("");
			System.out.println("Finished preprocessing subtree in time: " + 
					(System.currentTimeMillis() - tSubtree));
		}
		
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("Finished program in time: " + (System.currentTimeMillis() - tTotal));
	}
}