/*
 * Created on Jun 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _io;

import java.io.File;
import java.io.IOException;

import _misc.Constants;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WriteBinaryClusterIDTableStream {

	private BurstBufferedWriter out;
	
	public WriteBinaryClusterIDTableStream(String outFile, 
			int numBoardCards1, 
			int numBoardCards2, 
			byte[] holeCards,
			int numClusters1,
			int numClusters2,
			int bufferSize) throws IOException {
		
		if(new File(outFile).exists()) {
			throw new RuntimeException("file already exists: " + outFile);
		}

		out = new BurstBufferedWriter(outFile, bufferSize);
		
		// write version ID
		out.writeShort(Constants.vidClusterIDTables_holeCardsConstant_boardCardsImplicit);
		
		//build header
		short[] header = new short[10];
		header[0] = holeCards[0];
		header[1] = holeCards[1];
		header[2] = (short) numBoardCards1;
		header[3] = (short) numBoardCards2;
		header[4] = (short) numClusters1;
		header[5] = (short) numClusters2;
		for(int i = 6; i < 10; i++) {
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
	
	// NO ORDER CHECKING!
	public void putClusterId(byte id) throws IOException {
		out.writeByte(id);
	}
}
