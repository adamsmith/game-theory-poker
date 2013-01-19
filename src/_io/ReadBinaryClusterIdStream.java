/*
 * Created on Jun 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _io;

import java.io.IOException;

import stage1.HandRecordClusterId;
import _misc.Constants;


public class ReadBinaryClusterIdStream {

	private BurstBufferedReader in;
	
	private HandRecordClusterId nextRec;
	
	private byte[] holeCards;
	
	private short numClusters;
	
	public ReadBinaryClusterIdStream(String fileName, int numBoardCards, int bufferSize)
			throws IOException {
		
		this.in = new BurstBufferedReader(fileName, bufferSize);

		// read and verify format ID
		short formatID = in.readShort();
		if(formatID != Constants.vidClusterIDs_holeCardsConstant_boardCardsImplicit) {
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
		this.numClusters = header[3];
		
		nextRec = new HandRecordClusterId(numBoardCards, holeCards);
	}
	
	public short getNumClusters() {
		return this.numClusters;
	}
	
	public HandRecordClusterId readRecord() {
		if(!nextRec.hasMoreElements) {
			return null;
		}

		nextRec.advanceRecord();
		
		// NO COPIES MADE (FOR NOW)!!!!!!!
		try {
			nextRec.clusterId = in.readByte();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			throw new RuntimeException();
		}

		return nextRec;
	}
	
	public void close() throws IOException {
		in.close();
	}
}
