/*
 * Created on Apr 24, 2005
 */

/**
 * @author Adam
 *
 */

package stage1;

import java.io.IOException;

import _game.Card;
import _io.WriteBinaryScoreStream;
import _misc.Combinations;
import _misc.Constants;
import _misc.Helper;

public class DoShowdowns {

	private static final String ROOT_OUTPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
		"stage1" + Constants.dirSep + "5" + Constants.dirSep;
	
	public final static int NUM_HOLE_HANDS = Constants.choose(Card.NUM_CARDS-5, 2);
	
	private static final int MAX_SIMULT_FILES_OPEN = Constants.choose(Card.NUM_CARDS, 2);
	
	public static void main(String[] args) {
        try {
        	
			// ------------------------------------------------------------
			// load up all of the output files
			// ------------------------------------------------------------
			WriteBinaryScoreStream[][] out = 
				new WriteBinaryScoreStream[Card.NUM_CARDS][Card.NUM_CARDS];
			
			for(int i = 0; i < (Card.NUM_CARDS-1); i++) {
				for(int j = i+1; j < Card.NUM_CARDS; j++) {
					String path = ROOT_OUTPUT_DIR + new Integer(i).toString() + "_" +  new Integer(j).toString();
					Helper.prepFilePath(path);
	        		out[i][j] = new WriteBinaryScoreStream(
	        				path, 5, new byte[] {(byte) i, (byte) j}, 
							Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
				}
			}
			

			// ------------------------------------------------------------
			// loop through all final board card configs, write out scores
			// for each legal hand card combo
			// ------------------------------------------------------------
    		
			byte[] boardCards = new byte[5];
			byte[] holeArray;
			Combinations boardCardsIterator = new Combinations(Card.ALLCARDSINDEX, 5);
			int handScore;
			int opponentScore;
			double timer1 = System.currentTimeMillis();
			double progressCounter = 0;

			while (boardCardsIterator.hasMoreElements()) {
				progressCounter++;
				if (progressCounter % 12995 == 0) {
					System.out.println ((System.currentTimeMillis() - timer1) + ": " + (progressCounter / 2598960) + "% done");
				}
				
				// get 5 board cards
				boardCards = boardCardsIterator.nextElement();
				
            	// figure out possible hole cards
				Combinations holeCards = new Combinations(
						Helper.getRemainingCards(boardCards), 2);

				int scores[][] = new int[Card.NUM_CARDS][Card.NUM_CARDS];
				short winCount[][] = new short[Card.NUM_CARDS][Card.NUM_CARDS];
				short tieCount[][] = new short[Card.NUM_CARDS][Card.NUM_CARDS];
				short loseCount[][] = new short[Card.NUM_CARDS][Card.NUM_CARDS];
				byte doneCard1[] = new byte[NUM_HOLE_HANDS];
				byte doneCard2[] = new byte[NUM_HOLE_HANDS];
				byte b1[] = new byte[NUM_HOLE_HANDS];
				byte b2[] = new byte[NUM_HOLE_HANDS];
				byte b3[] = new byte[NUM_HOLE_HANDS];
				byte b4[] = new byte[NUM_HOLE_HANDS];
				byte b5[] = new byte[NUM_HOLE_HANDS];
				int doneCardPointer = 0;
				
				// iterate over each possible hole card combo, 
				// assign score, winCount, tieCount, and loseCount
				while(holeCards.hasMoreElements()) {
					holeArray = holeCards.nextElement();
					handScore = HandEvaluator.rankHand_Java(Helper.mergeByteArrays(boardCards, holeArray));
					
					for(int i = 0; i < doneCardPointer; i++) {
						if(
								(doneCard1[i] != holeArray[0]) &&
								(doneCard1[i] != holeArray[1]) &&
								(doneCard2[i] != holeArray[0]) &&
								(doneCard2[i] != holeArray[1])) {
							// showdown!
							opponentScore = scores[doneCard1[i]][doneCard2[i]];
							if(opponentScore > handScore) {
								winCount[doneCard1[i]][doneCard2[i]]++;
								loseCount[holeArray[0]][holeArray[1]]++;
							} else if (opponentScore < handScore) {
								loseCount[doneCard1[i]][doneCard2[i]]++;
								winCount[holeArray[0]][holeArray[1]]++;
							} else { //equal
								tieCount[doneCard1[i]][doneCard2[i]]++;
								tieCount[holeArray[0]][holeArray[1]]++;
							}
						}
					}

					scores[holeArray[0]][holeArray[1]] = handScore;
					doneCard1[doneCardPointer] = holeArray[0];
					doneCard2[doneCardPointer] = holeArray[1];
					doneCardPointer++;
				}
				
				if(doneCardPointer != NUM_HOLE_HANDS) {
					throw new RuntimeException();
				}
				
				// we're still in a single loop of one set of board cards

				for(int i = 0; i < NUM_HOLE_HANDS; i++) {
					short countBasedScore = (short) ((2 * winCount[doneCard1[i]][doneCard2[i]])
						+ tieCount[doneCard1[i]][doneCard2[i]]);
					out[doneCard1[i]][doneCard2[i]].putScore(countBasedScore);
				}
			}
			
			// outside of all loops now -- we're done
			for(int i = 0; i < (Card.NUM_CARDS-1); i++) {
				for(int j = i+1; j < Card.NUM_CARDS; j++) {
					if(out[i][j] != null) {
						out[i][j].close();
					}
				}
			}
			
			System.out.println(System.currentTimeMillis() - timer1);
			
	    } catch (IOException e) {
	    	System.out.println(e.getMessage());
	    	System.out.println(e.getCause());
	    	throw new RuntimeException();
	    }
	}
}
