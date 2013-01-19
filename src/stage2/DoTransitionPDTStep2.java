/*
 * Created on Jun 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage2;

import java.io.*;

import _game.Card;
import _io.ReadBinaryData;
import _io.ReadBinaryScoreMapsStream;
import _io.ReadBinaryScoreStream;
import _io.WriteBinaryClusterIDStream;
import _misc.Constants;
import _misc.Helper;
import _io.*;
import _misc.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DoTransitionPDTStep2 {
	
	private static final String ROOT_INPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
	"stage2" + Constants.dirSep;
	
	private static final String ROOT_OUTPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
	"stage2" + Constants.dirSep;
	
	private static final int MAX_SIMULT_FILES_OPEN = Constants.choose(Card.NUM_CARDS, 2);

	public static void main(String[] args) throws IOException {

		double timer1 = System.currentTimeMillis();

		int stopAtBoardCards = new Integer(args[0]).intValue();
//		int numBoardCards = stopAtBoardCards;
		int numBoardCards = 0;
		while (numBoardCards <= stopAtBoardCards) {
			int numHoleCards = Constants.choose(Card.NUM_CARDS, 2);
			double tIter = System.currentTimeMillis();
			
			// ------------------------------------------------------------
			// load up all input files
			// ------------------------------------------------------------
			String inputDir = ROOT_INPUT_DIR + "transitions_step1_" + numBoardCards + Constants.dirSep;
			ReadBinaryClusterIdTableStream[] in = 
				new ReadBinaryClusterIdTableStream[numHoleCards];
			
			int inPointer = 0;
			for(int i = 0; i < (Card.NUM_CARDS-1); i++) {
				for(int j = i+1; j < Card.NUM_CARDS; j++) {
					String path = inputDir + new Integer(i).toString() + "_" + new Integer(j).toString();
					if(numBoardCards == 0 || numBoardCards == 3 || numBoardCards == 4) {
						in[inPointer++] = new ReadBinaryClusterIdTableStream(path, numBoardCards, Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
					} else {
						throw new RuntimeException();
					}
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

			int[] numClusters = in[0].getNumClusters();
			int numBoardCards2 = in[0].getNumBoardCards2();
			int numScenariosPerHC = Constants.choose(
					Card.NUM_CARDS - numBoardCards, numBoardCards2 - numBoardCards);
			int numEligible = Constants.choose(Card.NUM_CARDS - numBoardCards, 2);
			
			long[][][][] counts = new long[numClusters[0]][numClusters[0]][numClusters[1]][numClusters[1]];
			
			Combinations rootCombo = new Combinations(Card.ALLCARDSINDEX, numBoardCards);
			byte[] rootBoardCards;
			
			while(rootCombo.hasMoreElements()) {
				rootBoardCards = rootCombo.nextElement();
				
				byte[] cid1 = new byte[numHoleCards];
				byte[][] cid2 = new byte[numHoleCards][numScenariosPerHC];
				boolean[] eligible = new boolean[numHoleCards];
				
				for(int i = 0; i < numHoleCards; i++) {
					if(Helper.contains(rootBoardCards, holeCards[i][0]) ||
							Helper.contains(rootBoardCards, holeCards[i][1])) {
						eligible[i] = false;
					} else {
						eligible[i] = true;
						cid1[i] = in[i].readClusterId();
						for(int j = 0; j < numScenariosPerHC; j++) {
							cid2[i][j] = in[i].readClusterId();
						}
					}
				}
				
				for(int i = 0; i < numHoleCards; i++) {
					for(int j = i+1; j < numHoleCards; j++) {
						if(eligible[i] && eligible[j] && holeCards[i][0]!=holeCards[j][0]
								&& holeCards[i][0]!=holeCards[j][1]
								&& holeCards[i][1]!=holeCards[j][0]
								&& holeCards[i][1]!=holeCards[j][1]) {
							for(int k = 0; k < numScenariosPerHC; k++) {
								byte newClusterHC1 = cid2[i][k];
								byte newClusterHC2 = cid2[j][k];
								if(newClusterHC1 != Byte.MAX_VALUE &&
										newClusterHC2 != Byte.MAX_VALUE) {
									
									counts[cid1[i]][cid1[j]][newClusterHC1][newClusterHC2]++;
									
									// this just makes every # twice as large
//									counts[cid1[j]][cid1[i]][newClusterHC2][newClusterHC1]++;
								}
							}
						}
					}
				}
			}
			
			// finished calculating counts[][][][].
			
			
			for(int i = 0; i < in.length; i++) {
				in[i].close();
			}
			
			long sum = 0;
			for(int i = 0; i < numClusters[0]; i++) {
				for(int j = 0; j < numClusters[0]; j++) {
					for(int k = 0; k < numClusters[1]; k++) {
						for(int l = 0; l < numClusters[1]; l++) {
							System.out.println("[" + i + ", " + 
									j + ", " + k + ", " + l + "] = " + counts[i][j][k][l]);
							sum += counts[i][j][k][l];
						}
					}
				}
			}
			System.out.println(sum);
			
			// output counts[][][][] to file
			WriteBinaryTransitionPDT.writePDT(ROOT_OUTPUT_DIR + "transitions_" + numBoardCards,
					counts, numBoardCards, numBoardCards2, numClusters[0], numClusters[1], Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
			
			System.out.println("done " + numBoardCards + " boardcards in time: " + (System.currentTimeMillis() - tIter));

			switch(numBoardCards) { 
			case 0 : numBoardCards=3; break;
			case 3 : numBoardCards=4; break;
			case 4 : numBoardCards=Integer.MAX_VALUE; break;
			case 5 : numBoardCards=Integer.MAX_VALUE; break;
			default : throw new RuntimeException(); }
		}

		System.out.println("");
		System.out.println("");
		System.out.println("done job in total time: " + (System.currentTimeMillis() - timer1));
		
	}
}
