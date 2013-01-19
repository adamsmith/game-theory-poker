/*
 * Created on Jun 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage2;

import _game.Card;
import _io.*;
import _misc.Combinations;
import _misc.Constants;
import _misc.Helper;
import stage1.*;
import java.io.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DoTerminalClusterValuesStep1 {
	
	private static final String ROOT_INPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
	"stage2" + Constants.dirSep;
	
	private static final String ROOT_OUTPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
	"stage2" + Constants.dirSep;
	
	private static final int MAX_SIMULT_FILES_OPEN = Constants.choose(Card.NUM_CARDS, 2);
	
	public static void main(String[] args) throws IOException {
		double tTotal = System.currentTimeMillis();

		int numHoleCards = Constants.choose(Card.NUM_CARDS, 2);
		final int numBoardCards = 5;
		
		// ------------------------------------------------------------
		// load up all input files
		// ------------------------------------------------------------
		String inputDir = ROOT_INPUT_DIR + "clustering_step2_5" + Constants.dirSep;
		ReadBinaryClusterIdStream[] in = 
			new ReadBinaryClusterIdStream[numHoleCards];
		
		int inPointer = 0;
		for(int i = 0; i < (Card.NUM_CARDS-1); i++) {
			for(int j = i+1; j < Card.NUM_CARDS; j++) {
				String path = inputDir + new Integer(i).toString() + "_" + new Integer(j).toString();
				in[inPointer++] = new ReadBinaryClusterIdStream(
						path, 
						numBoardCards, 
						Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
			}
		}
		if(inPointer != numHoleCards) {
			throw new RuntimeException();
		}

		// ------------------------------------------------------------
		// do it
		// ------------------------------------------------------------
		byte[][] holeCards = new byte[numHoleCards][];
		int holeCardsPointer = 0;
		for(byte i = 0; i < (Card.NUM_CARDS-1); i++) {
			for(byte j = (byte)(i+1); j < Card.NUM_CARDS; j++) {
				holeCards[holeCardsPointer++] = new byte[] {i, j};
			}
		}

		int numClusters = in[0].getNumClusters();
		
		// strictly upper triangular matrix where higher values are in favor of the row-player
		long[][] winCount = new long[numClusters][numClusters];
		long[][] normalizeCount = new long[numClusters][numClusters];
		

		Combinations rootCombo = new Combinations(Card.ALLCARDSINDEX, numBoardCards);
		byte[] rootBoardCards;
		int numLegalHoleCards = Constants.choose(Card.NUM_CARDS - numBoardCards, 2);

		System.out.println("Entering phase 1 -- computing expected values...");
		double tPhase = System.currentTimeMillis();
		int progressCounter = 0;
		int fiveThousands = (int) Math.floor(Constants.choose(Card.NUM_CARDS, numBoardCards) / 200);
		
		while(rootCombo.hasMoreElements()) {
			progressCounter++;
			if (progressCounter % fiveThousands == 0) {
				System.out.println("  " +
						(System.currentTimeMillis() - tPhase) + 
						": " + (new Double((double)progressCounter / 
						Constants.choose(Card.NUM_CARDS, numBoardCards))).toString() + "% done");
			}
			
			rootBoardCards = rootCombo.nextElement();

			int[] score = new int[numLegalHoleCards];
			byte[] clusterId = new byte[numLegalHoleCards];
			
			int pointer = 0;
			for(int i = 0; i < numHoleCards; i++) {
				if(!Helper.contains(rootBoardCards, holeCards[i][0]) &&
						!Helper.contains(rootBoardCards, holeCards[i][1])) {
					
					score[pointer] = HandEvaluator.rankHand_Java(Helper.mergeByteArrays(rootBoardCards, holeCards[i]));
					clusterId[pointer] = in[i].readRecord().clusterId;
					
					pointer++;
				}
			}
			
			if(pointer != numLegalHoleCards) {
				throw new RuntimeException();
			}
			
			for(int i = 0; i < (numLegalHoleCards-1); i++) {
				for(int j = i+1; j < numLegalHoleCards; j++) {
					if(clusterId[i] == clusterId[j]) {
						continue;
					}
					if(score[i] > score[j]) {
						// i wins...is it the row or column player?
						if(clusterId[i] < clusterId[j]) {
							// i is the row player
							winCount[clusterId[i]][clusterId[j]] += 2;
							normalizeCount[clusterId[i]][clusterId[j]]++;
						} else {
							// i is the column player...only inc the counter
							normalizeCount[clusterId[j]][clusterId[i]]++;
						}
					} else if(score[i] < score[j]) {
						// i loses...is it the row or column player?
						if(clusterId[i] < clusterId[j]) {
							// i is the row player...only inc the counter
							normalizeCount[clusterId[i]][clusterId[j]]++;
						} else {
							// i is the column player
							winCount[clusterId[j]][clusterId[i]] += 2;
							normalizeCount[clusterId[j]][clusterId[i]]++;
						}
					} else {
						// a tie!
						if(clusterId[i] < clusterId[j]) {
							// i is the row player
							winCount[clusterId[i]][clusterId[j]] += 1;
							normalizeCount[clusterId[i]][clusterId[j]]++;
						} else {
							// i is the column player
							winCount[clusterId[j]][clusterId[i]] += 1;
							normalizeCount[clusterId[j]][clusterId[i]]++;
						}
					}
				}
			}
		}
		System.out.println("   Completed phase in time: " + (System.currentTimeMillis() - tPhase));
		
		for(int i = 0; i < in.length; i++) {
			in[i].close();
		}
		
		double[][] terminalValues = new double[numClusters][numClusters];
		for(int i = 0; i < (numClusters-1); i++) {
			for(int j = i+1; j < numClusters; j++) {
				terminalValues[i][j]  = (double)winCount[i][j];
				terminalValues[i][j] /= (double)normalizeCount[i][j];
				terminalValues[i][j] /= 2;
			}
		}
		
		for(int i = 0; i < numClusters; i++) {
			for(int j = 0; j < numClusters; j++) {
				System.out.println("[" + i + "," + j + "] = " + terminalValues[i][j]);
			}
		}
		
		// output terminalValues[][] to file
		WriteBinaryTerminalClusterValues.writeTerminalMatrix(
				ROOT_OUTPUT_DIR + "terminal_values_" + numBoardCards,
				terminalValues, numClusters, Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));

		System.out.println("All terminal values computed+recorded in time: " + (System.currentTimeMillis() - tTotal));
	}
}
