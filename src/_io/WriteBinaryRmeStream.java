/*
 * Created on Jul 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _io;

import java.io.File;
import java.io.IOException;

import stage1.HandRecord;
import stage3.*;
import _misc.Constants;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WriteBinaryRmeStream {
	// RME = Reward Matrix Element

	private BurstBufferedWriter out;

	public WriteBinaryRmeStream(String outFile, int bufferSize, 
			int[] numClusters, int maxNumBoardCards) throws IOException{
		
		// maxNumBoardCards is only sensical if this is the root of the tree
		
		if(new File(outFile).exists()) {
			throw new RuntimeException("file already exists: " + outFile);
		}
		
		if(numClusters.length != 6) {
			throw new RuntimeException();
		}

		out = new BurstBufferedWriter(outFile, bufferSize);

		// write version ID
		out.writeShort(Constants.vidRewardMatrixElementList);
		
		//build header
		short[] header = new short[10];
		for(int i = 0; i < 6; i++) {
			header[i] = (short) numClusters[i];
		}
		header[6] = (short) maxNumBoardCards;
		for(int i = 7; i < 10; i++) {
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
	
	public void writeRme(int firstDim, int secondDim, float value) throws IOException {
		out.writeInt(firstDim);
		out.writeInt(secondDim);
		out.writeFloat(value);
	}
	
	public void writeRme(RewardMatrixElement rme) throws IOException {
		out.writeInt(rme.firstDim);
		out.writeInt(rme.secondDim);
		out.writeFloat(rme.value);
	}
}
