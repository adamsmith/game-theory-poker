/*
 * Created on Jul 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage2;

import java.io.IOException;

import stage3.*;

import _game.Card;
import _io.*;
import _misc.Constants;
import _misc.Helper;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DoTerminalClusterValuesStep2 {
	
	private static final String ROOT_INPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
			"stage2" + Constants.dirSep;
	
	private static final String ROOT_OUTPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
			"stage2" + Constants.dirSep;
	
	private static final int MAX_SIMULT_FILES_OPEN = 1;
	
	public static void main(String[] args) throws IOException {
		double tTotal = System.currentTimeMillis();
		
		double[][][] terminalValues = new double[6][][];

		Object[] inData = LoadInputData.getTreeData(false, ROOT_INPUT_DIR);
		terminalValues[5] = ((double[][][]) inData[4])[5];
		float[][][][][] transition = new float[][][][][] {
				(float[][][][]) inData[1], // index 0
				new float[][][][] {}, 
				new float[][][][] {}, 
				(float[][][][]) inData[2], // 3
				(float[][][][]) inData[3]  // 4
		};
		
		int[] numClusters = new int[] {
				((double[][]) inData[0]).length,	// 0 bc's
				-1,								// 1
				-1,								// 2
				transition[3].length,			// 3 bc's
				transition[4].length,			// 4 bc's
				terminalValues[5].length			// 5 bc's
		};

		
		int numHoleCards = Constants.choose(Card.NUM_CARDS, 2);
		int numBoardCards = 4;
		while (numBoardCards >= 0) {
			
			double[][] newTVs = new double[numClusters[numBoardCards]][numClusters[numBoardCards]];
			int numPreviousBcs = Helper.getNextBoardCardCount(numBoardCards, false);
			
			for(int i = 0; i < numClusters[numBoardCards]; i++) {
				for(int j = 0; j < numClusters[numBoardCards]; j++) {
					// compute value of {i, j}
					double expectedValue = 0;

					for(int k = 0; k < numClusters[numPreviousBcs]; k++) {
						for(int l = 0; l < numClusters[numPreviousBcs]; l++) {
							// probabilities will sum to 1 across k, l
							double probability = transition[numBoardCards][i][j][k][l];
							
							double value;
							if(k < l) {
								value = terminalValues[numPreviousBcs][k][l];
							} else if(k > l) {
								value = 1-terminalValues[numPreviousBcs][l][k];
							} else {
								value = 0;
							}
							
							expectedValue += probability * value;
							
						}
					}
					
					if(i < j) {
						newTVs[i][j] += (expectedValue / 2);
					} else if(i > j) {
						newTVs[j][i] += ((1-expectedValue) / 2);
					}
				}
			}
			
			terminalValues[numBoardCards] = newTVs;
			
			String outFile = ROOT_OUTPUT_DIR + "terminal_values_" + numBoardCards;
			Helper.prepFilePath(outFile);
			WriteBinaryTerminalClusterValues.writeTerminalMatrix(
					outFile, terminalValues[numBoardCards], numClusters[numBoardCards],
					Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));

			switch(numBoardCards) { 
			case 4 : numBoardCards=3; break;
			case 3 : numBoardCards=0; break;
			case 0 : numBoardCards=Integer.MIN_VALUE; break;
			default : throw new RuntimeException(); }
		}

		// print all terminal value results (interesting stuff)
		numBoardCards = 5;
		while (numBoardCards >= 0) {
			
			System.out.println(numBoardCards);
			for(int i = 0; i < numClusters[numBoardCards]-1; i++) {
				for(int j = i+1; j < numClusters[numBoardCards]; j++) {
					System.out.println("[" + i + ", " + j + "] = " + terminalValues[numBoardCards][i][j]);
				}
			}

			switch(numBoardCards) { 
			case 5 : numBoardCards=4; break;
			case 4 : numBoardCards=3; break;
			case 3 : numBoardCards=0; break;
			case 0 : numBoardCards=Integer.MIN_VALUE; break;
			default : throw new RuntimeException(); }
		}
		

		
		
		
	}
}
