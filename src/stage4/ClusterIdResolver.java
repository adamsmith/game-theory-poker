/*
 * Created on Jul 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage4;

import _misc.*;
import _io.*;
import _game.*;
import java.io.*;
import stage1.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class ClusterIdResolver {
	
	public static final String ROOT_INPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
			"stage2" + Constants.dirSep;
	
	private static final int MAX_SIMULT_FILES_OPEN = 1;
	
	private final int numHoleCards;
	private byte[][] holeCardsFlatIndex;
	private int[][] reverseHoleCardsFlatIndex;
	private byte[] bc0Clusters;
	private byte[][] bc3Clusters;
	
	public static void main(String[] args) throws IOException {
		ClusterIdResolver yo = ClusterIdResolver.getClusterIdResolver();
	}
	
	
	private static ClusterIdResolver instance = null;
	public static ClusterIdResolver getClusterIdResolver() throws IOException {
		if(instance == null) {
			instance = new ClusterIdResolver();
		}
		return instance;
	}
	
	private ClusterIdResolver() throws IOException {
		numHoleCards = Constants.choose(Card.NUM_CARDS, 2);
		holeCardsFlatIndex = new byte[numHoleCards][];
		reverseHoleCardsFlatIndex = new int[Card.NUM_CARDS][Card.NUM_CARDS];
		
		int holeCardsPointer = 0;
		for(byte i = 0; i < (Card.NUM_CARDS-1); i++) {
			for(byte j = (byte)(i+1); j < Card.NUM_CARDS; j++) {
				reverseHoleCardsFlatIndex[i][j] = holeCardsPointer;
				holeCardsFlatIndex[holeCardsPointer] = new byte[] {i, j};
				
				holeCardsPointer++;
			}
		}
		if(holeCardsPointer != numHoleCards) {
			throw new RuntimeException();
		}
		
		bc0Clusters = new byte[numHoleCards];
		for(int i = 0; i < numHoleCards; i++) {
			String inName = ROOT_INPUT_DIR + "clustering_step2_0" + Constants.dirSep +
					holeCardsFlatIndex[i][0] + "_" + holeCardsFlatIndex[i][1];
			ReadBinaryClusterIdStream in = new ReadBinaryClusterIdStream(inName, 0, 
					Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));					
					
			bc0Clusters[i] = in.readRecord().clusterId;
		}
		
		bc3Clusters = new byte[numHoleCards][Constants.choose(Card.NUM_CARDS-2, 3)];
		for(int i = 0; i < numHoleCards; i++) {
			String inName = ROOT_INPUT_DIR + "clustering_step2_3" + Constants.dirSep +
					holeCardsFlatIndex[i][0] + "_" + holeCardsFlatIndex[i][1];
			ReadBinaryClusterIdStream in = new ReadBinaryClusterIdStream(inName, 3, 
					Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
			
			int bc3ClustersPointer = 0;
			HandRecordClusterId hr;
			while((hr = in.readRecord()) != null) {
				// check to make sure random access will work
				int randomAccessPointer = getCardsArrayIndex(holeCardsFlatIndex[i], hr.boardCards);
				if(randomAccessPointer != bc3ClustersPointer) {
					throw new RuntimeException();
				}
				
				bc3Clusters[i][bc3ClustersPointer++] = hr.clusterId;
			}
			if(bc3ClustersPointer != bc3Clusters[i].length) {
				throw new RuntimeException();
			}
		}
		
	}
	
	public byte getCluster(byte[] holeCards, byte[] boardCards) throws IOException {
		switch(boardCards.length) {
			case 0:
				return bc0Clusters[reverseHoleCardsFlatIndex[holeCards[0]][holeCards[1]]];
			case 3:
				return bc3Clusters[reverseHoleCardsFlatIndex[holeCards[0]][holeCards[1]]]
								   [getCardsArrayIndex(holeCards, boardCards)];
			case 4:
				return getClusterFromFile(holeCards, boardCards);
			case 5:
				return getClusterFromFile(holeCards, boardCards);
			default:
				throw new RuntimeException();
		}
	}
	
	private byte getClusterFromFile(byte[] holeCards, byte[] boardCards) throws IOException {
		if(boardCards.length != 4 && boardCards.length != 5) {
			throw new RuntimeException();
		}

		String inName = ROOT_INPUT_DIR + "clustering_step2_" + boardCards.length + 
				Constants.dirSep + holeCards[0] + "_" + holeCards[1];
		RandomAccessFile in = new RandomAccessFile(inName, "r");
		in.seek(Constants.FILE_HEADER_LENGTH_BYTES + 
				getCardsArrayIndex(holeCards, boardCards));
		byte answer = in.readByte();
		in.close();
		return answer;
	}
	
	private int getCardsArrayIndex(byte[] holeCards, byte[] boardCards) {
		// holeCards is ordered and length 2
		// for each x in holeCards, for each y in boardCards, x != y
		
		byte[] comboIndices = new byte[boardCards.length];
		byte subtract = 0;
		for(int i = 0; i < boardCards.length; i++) {
			if(subtract == 0) {
				if(holeCards[0] < boardCards[i]) {
					subtract++;
				}
			}
			if(subtract == 1) {
				if(holeCards[1] < boardCards[i]) {
					subtract++;
				}
			}
			comboIndices[i] = (byte) (boardCards[i] - subtract);
			if(comboIndices[i] < 0) {
				throw new RuntimeException();
			}
		}
		return Helper.getComboArrayIndex(Card.NUM_CARDS - 2, comboIndices.length, comboIndices);
	}
}
