package _io;

import java.io.IOException;

import stage1.*;
import _misc.*;


/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ReadBinaryScoreStream implements ReadBinaryData {
	
	private BurstBufferedReader in;
	
	private HandRecordScore nextRec;
	
	private byte[] holeCards;
	
	public ReadBinaryScoreStream(String fileName, int numBoardCards, int bufferSize) throws IOException {
		
		try {
			this.in = new BurstBufferedReader(fileName, bufferSize);
		} catch (IOException ioe) {
			Helper.tryToFreeMemory();
			this.in = new BurstBufferedReader(fileName, bufferSize);
		}

		// read and verify format ID
		short formatID = in.readShort();
		if(formatID != Constants.vidScoreOnly_holeCardsConstant_boardCardsImplicit) {
			throw new RuntimeException();
		}
		
		// read header from file
		short header[] = new short[10];
		for(int i = 0; i < 10; i++) {
			header[i] = in.readShort();
		}
		
		// process header
		this.holeCards = new byte[] { (byte)header[0], (byte)header[1] };
		if(numBoardCards != (byte)header[2]) {
			throw new RuntimeException("not right number of board cards");
		}
		
		nextRec = new HandRecordScore(numBoardCards, holeCards);
	}
	
	public HandRecord readRecord() {
		if(!nextRec.hasMoreElements) {
			return null;
		}

		nextRec.advanceRecord();
		
		// NO COPIES MADE (FOR NOW)!!!!!!!
		try {
			nextRec.score = in.readShort();
		} catch (IOException e) {
			try {
				Helper.tryToFreeMemory();
				nextRec.score = in.readShort();
			} catch (IOException e2) {
				System.out.println(e2.getMessage());
				throw new RuntimeException(e2);
			}
		}

		return nextRec;
	}
	
	public void close() throws IOException {
		in.close();
		in = null;
		System.runFinalization();
		System.gc();
		System.gc();
		System.gc();
	}
	
	protected void finalize() throws Throwable {
	    try {
	        close();
	    } finally {
	        super.finalize();
	    }
	}
	
	public byte[] getHoleCards() {
		return this.holeCards;
	}
}
