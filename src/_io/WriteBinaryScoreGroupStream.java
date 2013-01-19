/*
 * Created on Jun 24, 2005
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
public class WriteBinaryScoreGroupStream {

	private BurstBufferedWriter out;
	private int numScoreGroups;
	
	public WriteBinaryScoreGroupStream(String outFile, int numBoardCards, 
			byte[] holeCards, int numScoreGroups, int bufferSize) throws IOException {
		
		if(new File(outFile).exists()) {
			throw new RuntimeException("file already exists: " + outFile);
		}

		out = new BurstBufferedWriter(outFile, bufferSize);

		this.numScoreGroups = numScoreGroups;
		
		// write version ID
		if (numBoardCards == 4 || numBoardCards == 3 || numBoardCards == 0) {
			out.writeShort(Constants.vidClusterGroupsStep_holeCardsConstant_boardCardsImplicit);
		} else {
			out.close();
			throw new RuntimeException("score only out not supported for numBCs=" + numBoardCards);
		}

		
		//build header
		short[] header = new short[10];
		header[0] = holeCards[0];
		header[1] = holeCards[1];
		header[2] = (short) numBoardCards;
		header[3] = (short) numScoreGroups;
		for(int i = 4; i < 10; i++) {
			header[i] = 0;
		}
		
		//write header
		for(int i = 0; i < 10; i++) {
			out.writeShort(header[i]);
		}
	}
	
	public void close() throws IOException {
		out.close();
	}
	
	public void putScoreGroup(short[] group) throws IOException {
		if(group.length != numScoreGroups) {
			throw new RuntimeException();
		}
		for(int i = 0; i < numScoreGroups; i++) {
			out.writeShort(group[i]);
		}
	}
	
}
