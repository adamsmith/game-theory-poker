/*
 * Created on Jun 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _io;

import java.io.*;
import _misc.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WriteBinaryTransitionPDT {

	public static void writePDT(String outFile, long[][][][] pdt, 
			int numBoardCards1, int numBoardCards2, 
			int numClusters1, int numClusters2, int bufferSize) throws IOException {

		if(new File(outFile).exists()) {
			throw new RuntimeException("file already exists: " + outFile);
		}
		
		if(pdt.length != numClusters1 || pdt[0].length != numClusters1
				|| pdt[0][0].length != numClusters2 || pdt[0][0][0].length != numClusters2) {
			throw new RuntimeException();
		}
		
		BurstBufferedWriter out = new BurstBufferedWriter(outFile, bufferSize);
		
		// write version ID
		out.writeShort(Constants.vidTransitionPDTLongCounts);

		//build header
		short[] header = new short[10];
		header[0] = (short) numBoardCards1;
		header[1] = (short) numBoardCards2;
		header[2] = (short) numClusters1;
		header[3] = (short) numClusters2;
		for(int i = 4; i < 10; i++) {
			header[i] = 0;
		}

		//write header
		for(int i = 0; i < 10; i++) {
			out.writeShort(header[i]);
		}
		
		for(int i = 0; i < numClusters1; i++) {
			for(int j = 0; j < numClusters1; j++) {
				for(int k = 0; k < numClusters2; k++) {
					for(int l = 0; l < numClusters2; l++) {
						out.writeLong(pdt[i][j][k][l]);
					}
				}
			}
		}
		
		out.close();
	}
}
