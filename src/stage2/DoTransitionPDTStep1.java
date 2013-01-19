/*
 * Created on Jun 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage2;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import stage1.HandRecordClusterId;
import _game.Card;
import _io.ReadBinaryClusterIdStream;
import _io.WriteBinaryClusterIDTableStream;
import _misc.ByteArrayWrapper;
import _misc.Combinations;
import _misc.Constants;
import _misc.Helper;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DoTransitionPDTStep1 {
	
	private static final String ROOT_INPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
	"stage2" + Constants.dirSep;
	
	private static final String ROOT_OUTPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
	"stage2" + Constants.dirSep;
	
	private static final int MAX_SIMULT_FILES_OPEN = 1;
	
	public static void main(String[] args) throws IOException {

		double tTotal = System.currentTimeMillis();

		int numBoardCards1 = Integer.parseInt(args[0]);
		int stopAtNumBoardCards1 = Integer.parseInt(args[1]);
		
		if(numBoardCards1 > stopAtNumBoardCards1) {
			throw new RuntimeException();
		}
		
		int numBoardCards2 = Helper.getNextBoardCardCount(numBoardCards1, false);
		while (numBoardCards1 <= stopAtNumBoardCards1) {
			double tBoardCards = System.currentTimeMillis();
			System.out.println("");
			System.out.println("---------");
			System.out.println("TPDT Step 1'ing " + numBoardCards1 + "->" + numBoardCards2 + " boardcards");
			System.out.println("---------");

			boolean isFiveBcs = (numBoardCards2 == 5 ? true : false);
			
			byte[] holeCards;
			
			int numIterations = Constants.choose(Card.NUM_CARDS, 2);
			int iterationCounter = 0;
			while((holeCards = getNextTask(numBoardCards1)) != null) {
				double tIteration = System.currentTimeMillis();
				
				System.out.println("Executing on hole cards: " + Helper.byteArrayToString(holeCards) + "...");
				
				// ------------------------------------------------------------
				// load up our two input files -- (x) bc's and (x+1) bc's
				// ------------------------------------------------------------
				String inputPath1 = getInputFileName(numBoardCards1, holeCards);
				String inputPath2 = getInputFileName(numBoardCards2, holeCards);

				ReadBinaryClusterIdStream in1 = 
					new ReadBinaryClusterIdStream(inputPath1, numBoardCards1, Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
				ReadBinaryClusterIdStream in2 = 
					new ReadBinaryClusterIdStream(inputPath2, numBoardCards2, Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
				
				
				// ------------------------------------------------------------
				// load up output file
				// ------------------------------------------------------------
				String outputPath = getOutputFileName(numBoardCards1, holeCards);
				Helper.prepFilePath(outputPath);
				
				WriteBinaryClusterIDTableStream out = 
					new WriteBinaryClusterIDTableStream(
					outputPath, numBoardCards1, numBoardCards2, holeCards, 
					in1.getNumClusters(), in2.getNumClusters(), 
					Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
				
				

				
				// ------------------------------------------------------------
				// read input for numBoardCards2 into array for random access
				// ------------------------------------------------------------
				byte[][][][][] newClusterIds = new byte[Card.NUM_CARDS+1]
														[Card.NUM_CARDS+1]
														 [Card.NUM_CARDS+1]
														  [Card.NUM_CARDS+1]
														   [Card.NUM_CARDS+1];
				HandRecordClusterId hr2;
				double arrayPopulateTimer = System.currentTimeMillis();
				int numEntriesToPopulate = Constants.choose(Card.NUM_CARDS-2, numBoardCards2);
				int modVal = (int)Math.floor(numEntriesToPopulate/100);

				if(isFiveBcs) {
					int counter = 0;
					int percent = 0;
					while((hr2 = in2.readRecord()) != null) {
						byte[] boardCards2Copy = hr2.boardCards;
						newClusterIds[boardCards2Copy[0]]
									  [boardCards2Copy[1]]
									   [boardCards2Copy[2]]
										[boardCards2Copy[3]]
										 [boardCards2Copy[4]] = hr2.clusterId;
						if(++counter % modVal == 0) {
//							System.out.println("  " + (percent++) + "% done populating array");
						}
					}
				} else {
					while((hr2 = in2.readRecord()) != null) {
						byte[] boardCards2Copy = new byte[5];
						for(int i = 0; i < numBoardCards2; i++) {
							boardCards2Copy[i] = hr2.boardCards[i];
						}
						for(int i = numBoardCards2; i < 5; i++) {
							boardCards2Copy[i] = Card.NUM_CARDS;
						}
						newClusterIds[boardCards2Copy[0]]
									  [boardCards2Copy[1]]
									   [boardCards2Copy[2]]
										[boardCards2Copy[3]]
										 [boardCards2Copy[4]] = hr2.clusterId;
					}
				}
//				System.out.println("    load array: " + 
//						(System.currentTimeMillis() - arrayPopulateTimer));
				
				
				// ------------------------------------------------------------
				// create output
				// ------------------------------------------------------------
				
				// for each (50 choose numBoardCards1) ...
				//   print clusterId for numBoardCards1
				//   loop through all possible (50 choose numBoardCards2) boardcards,
				//     merge each one with our holecards to get the candidates for the
				//     next card-turn.  For each of those, look up the cluster ID for
				//     these holecards and print that out
				
				HandRecordClusterId hr1;
				
				// each output sequence (for each boardCard config) will now:
				//  1) be the same length
				//  2) be in the same order, given the boardcards
				// output cluster ID will only be Byte.MAX_VALUE if the newly delt
				// card is one of the ones in these holecards.  
				
				while((hr1 = in1.readRecord()) != null) {
					// first print the master clusterId for numBoardCards1
					out.putClusterId(hr1.clusterId);
					
					Combinations nextHandAddCards = new Combinations(
							Helper.getRemainingCards(hr1.boardCards), 
							(numBoardCards2 - numBoardCards1)); //hot spot?
					
					byte[] addCards;
					
					while(nextHandAddCards.hasMoreElements()) {
						addCards = nextHandAddCards.nextElement();
						
						boolean legalGivenHCs = true;
						for(int i = 0; i < addCards.length; i++) {
							if(addCards[i] == holeCards[0] ||
									addCards[i] == holeCards[1]) {
								legalGivenHCs = false;
								break;
							}
						}
						
						if(legalGivenHCs) {
							byte[] newHand = Helper.mergeOrderedByteArrays(
									hr1.boardCards, addCards);
							byte newClusterId = -1;
							if(isFiveBcs) {
								newClusterId = newClusterIds[newHand[0]]
															 [newHand[1]]
															  [newHand[2]]
															   [newHand[3]]
																[newHand[4]];
							} else {
								byte[] newBcs = new byte[5];
								for(int i = 0; i < numBoardCards2; i++) {
									newBcs[i] = newHand[i];
								}
								for(int i = numBoardCards2; i < 5; i++) {
									newBcs[i] = Card.NUM_CARDS;
								}
								newClusterId = newClusterIds[newBcs[0]]
															 [newBcs[1]]
															  [newBcs[2]]
															   [newBcs[3]]
																[newBcs[4]];
							}
							out.putClusterId(newClusterId);
						} else {
							out.putClusterId(Byte.MAX_VALUE); // place holder!
						}
						
					}
				}
				
				out.close();
				deleteLock(numBoardCards1, holeCards);
				System.out.println("   Iteration " + ((int) Math.floor(100*((double) ++iterationCounter/numIterations))) + "% done in time: " + (System.currentTimeMillis() - tIteration));
			}

			System.out.println("numBoardCards " + numBoardCards1 + "->" + numBoardCards2 + " completed in time: " + (System.currentTimeMillis() - tBoardCards));
			
			
			switch(numBoardCards1) { 
			case 0 : numBoardCards1=3; numBoardCards2=4; break;
			case 3 : numBoardCards1=4; numBoardCards2=5; break;
			case 4 : numBoardCards1=Integer.MAX_VALUE; numBoardCards2=Integer.MAX_VALUE; break;
			default : throw new RuntimeException(); }
		}

		System.out.println("All TPDT Step1'ing completed in time: " + (System.currentTimeMillis() - tTotal));
	}
	
	private static byte[] getNextTask(int numBoardCards1) throws IOException {
		Combinations combo = new Combinations(Card.ALLCARDSINDEX, 2);
		byte[] holeCards;
		
		while(combo.hasMoreElements()) {
			holeCards = combo.nextElement();
			
			// disqualified if it's already been done
			if(new File(getOutputFileName(numBoardCards1, holeCards)).exists()) {
				continue;
			}
			
			// disqualified if it's currently being done...otherwise lock it
			//  (check for lock, and lock, atomically)
			File lock = new File(getInputFileName(numBoardCards1, holeCards) + ".lock");
			if(lock.createNewFile()) {
				// we got a lock!
				return holeCards;
			}
		}
		
		// we couldn't find any tasks
		return null;		
	}
	
	private static void deleteLock(int numBoardCards1, byte[] holeCards) {
		File lock = new File(getInputFileName(numBoardCards1, holeCards) + ".lock");
		if(!lock.exists()) {
			throw new RuntimeException();
		}
		lock.delete();
	}
	
	private static String getInputFileName(int numBoardCards, byte[] holeCards) {
		return ROOT_INPUT_DIR + "clustering_step2_" + numBoardCards+ "" + Constants.dirSep
				+ new Integer(holeCards[0]).toString() + "_" 
				+ new Integer(holeCards[1]).toString();
	}
	
	private static String getOutputFileName(int numBoardCards1, byte[] holeCards) {
		return ROOT_OUTPUT_DIR + "transitions_step1_" 
				+ numBoardCards1 + "" + Constants.dirSep
				+ new Integer(holeCards[0]).toString() + "_" 
				+ new Integer(holeCards[1]).toString();
	}
}
