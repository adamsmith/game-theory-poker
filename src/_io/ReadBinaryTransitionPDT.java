/*
 * Created on Jun 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _io;

import _misc.Constants;
import java.io.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ReadBinaryTransitionPDT {

	public static long[][][][] readPDT(String fileName, int bufferSize) throws IOException {

		BurstBufferedReader in = new BurstBufferedReader(fileName, bufferSize);
		
		// read and verify format ID
		short formatID = in.readShort();
		if(formatID != Constants.vidTransitionPDTLongCounts) {
			throw new RuntimeException();
		}
		
		// read header from file
		short header[] = new short[10];
		for(int i = 0; i < 10; i++) {
			header[i] = in.readShort();
		}
		
		// process header
		int numClusters1 = header[2];
		int numClusters2 = header[3];
		
		long[][][][] pdt = new long[numClusters1][numClusters1][numClusters2][numClusters2];
		
		for(int i = 0; i < numClusters1; i++) {
			for(int j = 0; j < numClusters1; j++) {
				for(int k = 0; k < numClusters2; k++) {
					for(int l = 0; l < numClusters2; l++) {
						pdt[i][j][k][l] = in.readLong();
					}
				}
			}
		}
		
		in.close();
		
		return pdt;
	}
}
