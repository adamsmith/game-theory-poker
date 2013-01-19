/*
 * Created on May 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _io;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import stage1.HandRecord;
import stage1.ScoreMaps;
import _misc.Constants;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WriteBinaryScoreMaps {

	public static void writeScoreMapHR(ScoreMaps in, String outFile, int bufferSize) throws IOException {
		
		if(new File(outFile).exists()) {
			throw new RuntimeException("file already exists: " + outFile);
		}

		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outFile), bufferSize));
		
		byte numBoardCards = in.getNumberBoardCards();
		byte[] holeCards = in.getHoleCards();
		
		//write version ID
		if(numBoardCards == 5) {
			out.close();
			throw new RuntimeException("score maps not defined for 5 board cards");
		} else if (numBoardCards == 4 || numBoardCards == 3 || numBoardCards == 0) {
			out.writeShort(Constants.vidScoreMap_holeCardsConstant_boardCardsImplicit);
		} else {
			out.close();
			throw new RuntimeException("score maps not supported for numBCs=" + numBoardCards);
		}
		
		//build header
		short[] header = new short[10];
		header[0] = holeCards[0];
		header[1] = holeCards[1];
		header[2] = numBoardCards;
		for(int i = 3; i < 10; i++) {
			header[i] = 0;
		}
		
		//write header
		for(int i = 0; i < 10; i++) {
			out.writeShort(header[i]);
		}
		
		
		HandRecord iterator = new HandRecord(numBoardCards, holeCards);
		
		int numScores = 0;
		int numHands = 0;
		int numScoresBySize = 0;

		// THIS CODE HAS BEEN TESTED -- DO NOT TOUCH!!!!!!!!!!!!!!!!!!!!
		while(iterator.hasMoreElements) {
			iterator.advanceRecord();
			
//			 doing the lookups against our iterator ensures proper ordering
			Map mapToWrite = in.getScoreMapWithCards(iterator);
			numHands++;
			
			// write score map to file, which includes several entries
			out.writeShort(mapToWrite.size()*6); // write number of total Bytes for this map (not counting this length value) as 6 bytes per entry
			numScoresBySize += mapToWrite.size();
			for(Iterator i = mapToWrite.entrySet().iterator(); i.hasNext(); ) {
				Map.Entry entryToWrite = (Map.Entry) i.next();
				out.writeShort(((Short) entryToWrite.getKey()).shortValue());
				out.writeInt(((Integer) entryToWrite.getValue()).intValue());
				numScores++;
			}
		}
		
		if(numBoardCards == 4) {
//			System.out.println("numScores: " + numScores);
//			System.out.println("numScoresBySize: " + numScoresBySize);
//			System.out.println("numHands: " + numHands);
//			System.out.println("mapAccumYo: " + in.mapAccumYo);
		}
		
		out.close();		
	}
}
