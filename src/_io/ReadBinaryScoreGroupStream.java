/*
 * Created on Jun 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _io;

import java.io.*;

import stage1.HandRecord;
import stage1.HandRecordScoreGroups;
import _misc.Constants;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ReadBinaryScoreGroupStream  implements ReadBinaryData {
	
	private String m_fileName;
	private int m_numBoardCards;
	private int m_bufferSize;
	private int m_numScoreGroups;
	
	private BurstBufferedReader m_in;
	private HandRecordScoreGroups m_nextRec;
	private byte[] m_holeCards;

	public ReadBinaryScoreGroupStream(String fileName, int numBoardCards, int bufferSize) 
			throws IOException {
		
		m_fileName = fileName;
		m_numBoardCards = numBoardCards;
		m_bufferSize = bufferSize;
		m_in = new BurstBufferedReader(fileName, bufferSize);
		
		// read and verify format ID
		short formatID = m_in.readShort();
		if(formatID != Constants.vidClusterGroupsStep_holeCardsConstant_boardCardsImplicit) {
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
		m_numScoreGroups = header[3];
		
		m_nextRec = new HandRecordScoreGroups(m_numBoardCards, m_holeCards, m_numScoreGroups);
	}
	
	public HandRecord readRecord() throws IOException {
		if(!m_nextRec.hasMoreElements) {
			return null;
		}
		
		m_nextRec.advanceRecord();

		for(int i = 0; i < m_numScoreGroups; i++) {
			m_nextRec.scoreGroups[i] = (int) m_in.readShort();
		}
		
		return m_nextRec;  // copy not made!!  don't mutate.
	}
	
	public void close() throws IOException {
		m_in.close();
	}
	
	public ReadBinaryScoreMapsStream reset() throws IOException {
		close();
		return new ReadBinaryScoreMapsStream(m_fileName, m_numBoardCards, m_bufferSize);
	}
	
	public byte[] getHoleCards() {
		return m_holeCards;
	}
	
	public int getNumScoreGroups() {
		return m_numScoreGroups;
	}

}
