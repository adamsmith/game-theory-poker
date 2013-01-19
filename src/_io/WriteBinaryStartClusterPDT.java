/*
 * Created on Jun 19, 2005
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
public class WriteBinaryStartClusterPDT {
	
	public static void writeStartPDT(String outFile, double[][] startPDT, 
			int numClusters, int bufferSize) throws IOException {

		if(new File(outFile).exists()) {
			throw new RuntimeException("file already exists: " + outFile);
		}
		
		if(startPDT.length != numClusters || startPDT[0].length != numClusters) {
			throw new RuntimeException();
		}
		
		BurstBufferedWriter out = new BurstBufferedWriter(outFile, bufferSize);
		
		// write version ID
		out.writeShort(Constants.vidStartClusterPDT);

		//build header
		short[] header = new short[10];
		header[0] = (short) numClusters;
		for(int i = 1; i < 10; i++) {
			header[i] = 0;
		}

		//write header
		for(int i = 0; i < 10; i++) {
			out.writeShort(header[i]);
		}
		
		for(int i = 0; i < numClusters; i++) {
			for(int j = 0; j < numClusters; j++) {
				out.writeDouble(startPDT[i][j]);
			}
		}
		
		out.close();
	}
	
}
