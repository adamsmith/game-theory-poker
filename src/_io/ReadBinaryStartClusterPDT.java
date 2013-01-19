/*
 * Created on Jun 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _io;

import java.io.IOException;

import _misc.Constants;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ReadBinaryStartClusterPDT {
	
	public static double[][] getStartPDT(String fileName, int bufferSize) throws IOException {
		
		BurstBufferedReader in = new BurstBufferedReader(fileName, bufferSize);
		
		// read and verify format ID
		short formatID = in.readShort();
		if(formatID != Constants.vidStartClusterPDT) {
			throw new RuntimeException();
		}

		// read header from file
		short header[] = new short[10];
		for(int i = 0; i < 10; i++) {
			header[i] = in.readShort();
		}
		
		// process header
		int numClusters = header[0];
		
		double[][] startPDT = new double[numClusters][numClusters];
		for(int i = 0; i < numClusters; i++) {
			for(int j = 0; j < numClusters; j++) {
				startPDT[i][j] = in.readDouble();
			}
		}
		
		in.close();
		
		return startPDT;
	}
}
