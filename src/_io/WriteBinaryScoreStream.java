/*
 * Created on May 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _io;

import java.io.File;
import java.io.IOException;

import stage1.HandRecord;
import _misc.Constants;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WriteBinaryScoreStream {

	private BurstBufferedWriter out;
	private HandRecord iterator;
	
	public WriteBinaryScoreStream(String outFile, int numBoardCards, byte[] holeCards, int bufferSize) throws IOException {
		
		if(new File(outFile).exists()) {
			throw new RuntimeException("file already exists: " + outFile);
		}

		out = new BurstBufferedWriter(outFile, bufferSize);

		// write version ID
		if (numBoardCards == 5 || numBoardCards == 4 || numBoardCards == 3 || numBoardCards == 0) {
			out.writeShort(Constants.vidScoreOnly_holeCardsConstant_boardCardsImplicit);
		} else {
			out.close();
			throw new RuntimeException("score only out not supported for numBCs=" + numBoardCards);
		}

		
		//build header
		short[] header = new short[10];
		header[0] = holeCards[0];
		header[1] = holeCards[1];
		header[2] = (short) numBoardCards;
		for(int i = 3; i < 10; i++) {
			header[i] = 0;
		}
		
		//write header
		for(int i = 0; i < 10; i++) {
			out.writeShort(header[i]);
		}
		
		
		//set up iterator
		iterator = new HandRecord(numBoardCards, holeCards);
	}
	
	public void close() throws IOException {
		out.close();
	}
	
	public void putScore(short score) throws IOException {
		iterator.advanceRecord();
		out.writeShort(score);
	}
	
	public void putScore(short score, HandRecord toVerify) throws IOException {
		iterator.advanceRecord();
		
		//check boardCards[]
		for(int i = 0; i < 5; i++) {
			if(iterator.boardCards[i] != toVerify.boardCards[i]) {
				throw new RuntimeException("verification negative");
			}
		}
		
		//don't check holeCards[]
		
		out.writeShort(score);
	}
	
}
