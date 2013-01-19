/*
 * Created on May 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _io;

import java.io.IOException;

import stage1.*;
import _misc.Constants;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ReadBinaryScoreMapsStream implements ReadBinaryData {
	
	private String m_fileName;
	private int m_numBoardCards;
	private int m_bufferSize;
	
	private BurstBufferedReader m_in;
	private HandRecordScoreMap m_nextRec;
	private byte[] m_holeCards;

	public ReadBinaryScoreMapsStream(String fileName, int numBoardCards, int bufferSize) 
			throws IOException {
		
		m_fileName = fileName;
		m_numBoardCards = numBoardCards;
		m_bufferSize = bufferSize;
		m_in = new BurstBufferedReader(fileName, bufferSize);
		
		// read and verify format ID
		short formatID = m_in.readShort();
		if(formatID != Constants.vidScoreMap_holeCardsConstant_boardCardsImplicit) {
			throw new RuntimeException();
		}
		
		// read header from file
		short header[] = new short[10];
		for(int i = 0; i < 10; i++) {
			header[i] = m_in.readShort();
		}
		
		// process header
		m_holeCards = new byte[] { (byte)header[0], (byte)header[1] };
		if(numBoardCards != (byte)header[2]) {
			throw new RuntimeException("not right number of board cards");
		}
		
		m_nextRec = new HandRecordScoreMap(numBoardCards, m_holeCards);
	}
	
	public HandRecord readRecord() throws IOException {
		if(!m_nextRec.hasMoreElements) {
			return null;
		}
		
		m_nextRec.advanceRecord();
		
		short length;
		short score;
		int count;
		
		// THIS CODE HAS BEEN TESTED -- DO NOT TOUCH!!!!!!!!!!!!!!!!!!!!? :)
		length = m_in.readShort();
		
		for(int i = 0; i < length/6; i++) {
			score = m_in.readShort();
			count = m_in.readInt();
			
			m_nextRec.addScoreFromFile(score, count);
		}
		
		return m_nextRec;  // copy not made!!  don't mutate.
	}
	
	public void close() throws IOException {
		m_in.close();
		m_in = null;
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
	
	public ReadBinaryScoreMapsStream reset() throws IOException {
		close();
		return new ReadBinaryScoreMapsStream(m_fileName, m_numBoardCards, m_bufferSize);
	}
	
	public byte[] getHoleCards() {
		return m_holeCards;
	}
	
}
