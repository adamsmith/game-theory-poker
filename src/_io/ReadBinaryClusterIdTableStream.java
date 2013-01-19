/*
 * Created on Jun 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _io;

import java.io.IOException;

import stage1.HandRecordClusterId;
import _misc.Constants;


public class ReadBinaryClusterIdTableStream {

	private BurstBufferedReader in;
	
//	private HandRecordClusterId nextRec;
	
	private byte[] holeCards;

	private short numClusters1;
	private short numClusters2;
	
	private short numBoardCards2;
	
	public ReadBinaryClusterIdTableStream(String fileName, int numBoardCards1, int bufferSize)
			throws IOException {
		
		this.in = new BurstBufferedReader(fileName, bufferSize);

		// read and verify format ID
		short formatID = in.readShort();
		if(formatID != Constants.vidClusterIDTables_holeCardsConstant_boardCardsImplicit) {
			throw new RuntimeException();
		}
		
		// read header from file
		short header[] = new short[10];
		for(int i = 0; i < 10; i++) {
			header[i] = in.readShort();
		}
		
		// process header
		this.holeCards = new byte[] { (byte)header[0], (byte)header[1] };
		if(numBoardCards1 != (byte)header[2]) {
			throw new RuntimeException("not right number of board cards");
		}
		this.numBoardCards2 = (byte)header[3];
		this.numClusters1 = (short)header[4];
		this.numClusters2 = (short)header[5];
		
//		nextRec = new HandRecordClusterId(numBoardCards1, holeCards);
	}
	
//	public HandRecordClusterId readRecord() {
//		if(!nextRec.hasMoreElements) {
//			return null;
//		}
//
//		nextRec.advanceRecord();
//		
//		// NO COPIES MADE (FOR NOW)!!!!!!!
//		try {
//			nextRec.clusterId = in.readByte();
//		} catch (IOException e) {
//			System.out.println(e.getMessage());
//			throw new RuntimeException();
//		}
//
//		return nextRec;
//	}

	public int[] getNumClusters() {
		return new int[] {numClusters1, numClusters2};
	}
	
	public int getNumBoardCards2() {
		return numBoardCards2;
	}
	
	public byte readClusterId() throws IOException {
		return in.readByte();
	}
	
	public void close() throws IOException {
		in.close();
	}
}