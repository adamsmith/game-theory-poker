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
	
	Depreciated
	
	public static final String ROOT_INPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
			"stage3" + Constants.dirSep;
	
	public static final String ROOT_OUTPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
			"stage3" + Constants.dirSep;
	
	private static final int MAX_SIMULT_FILES_OPEN = 1;

	private final static int[] numClusters = new int[] {7, -1, -1, 7, 7, 7};
	private static int maxNumBoardCards;
	
	public static void main(String[] args) throws IOException {
		double tTotal = System.currentTimeMillis();
		
		String inFile = ROOT_INPUT_DIR + "rewardMatrix";
		
		System.out.println("Preprocessing reward matrix (" + inFile + ")");
		System.out.println("");
		System.out.println("Reading in file...");
		double tStage = System.currentTimeMillis();
		
		Object[] answer = ReadBinaryRm.getRewardMatrix(inFile, 
				Helper.getBufferSize(MAX_SIMULT_FILES_OPEN), numClusters);
		List rewardMatrix = (List) answer[0];
		maxNumBoardCards = ((Integer) answer[1]).intValue();
		
		System.out.println("Finished reading file in time: " + 
				(System.currentTimeMillis() - tStage));
		System.out.println("NNZ: " + rewardMatrix.size());
		System.out.println("");
		System.out.println("Sorting matrix...");
		tStage = System.currentTimeMillis();
		
		Collections.sort(rewardMatrix);
		
		System.out.println("Finished sorting in time: " + 
				(System.currentTimeMillis() - tStage));
		System.out.println("");
		System.out.println("Pruning matrix...");
		tStage = System.currentTimeMillis();
		
		pruneRewardMatrix(rewardMatrix);
		
		System.out.println("Finished pruning in time: " + 
				(System.currentTimeMillis() - tStage));
		System.out.println("NNZ: " + rewardMatrix.size());
		System.out.println("");
		System.out.println("Writing preprocessed matrix to disk...");
		tStage = System.currentTimeMillis();

		String outFile = ROOT_OUTPUT_DIR + "rewardMatrix.preprocessed";
		Helper.prepFilePath(outFile);
		WriteBinaryRmeStream out = new WriteBinaryRmeStream(outFile,
				Helper.getBufferSize(MAX_SIMULT_FILES_OPEN), numClusters,
				maxNumBoardCards);
		for(int i = 0; i < rewardMatrix.size(); i++) {
			RewardMatrixElement rme = (RewardMatrixElement) rewardMatrix.get(i);
			if(rme != null) {
				out.writeRme(rme.rowAsP1, rme.columnAsP2, rme.value);
			}
		}
		out.close();
		
		System.out.println("Finished writing to disk in time: " + 
				(System.currentTimeMillis() - tStage));
		System.out.println("");
		System.out.println("");
		System.out.println("Finished preprocessing program in time: " + 
				(System.currentTimeMillis() - tTotal));		
	}

	private static void pruneRewardMatrix(List rm) {
		// remove duplicates
		//   (we're not worried about removing records with value==0 since that is now
		//    done in the previous step)

		for(int i = rm.size()-1; i > 0; i--) {
			if(rm.get(i).equals(rm.get(i-1))) {
				rm.set(i, null);
			}
		}
	}
}
