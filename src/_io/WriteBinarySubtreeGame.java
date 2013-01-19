/*
 * Created on Jul 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _io;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import _misc.Constants;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WriteBinarySubtreeGame {

	public static void writeSubtreeGameDescription(String outFile, double[][] startClusterPDT, 
			float p1Pot, float p2Pot) throws Exception {
		
		if(new File(outFile).exists()) {
			throw new RuntimeException("file already exists: " + outFile);
		}

		FileOutputStream fos = new FileOutputStream(outFile);
		DataOutputStream dos = new DataOutputStream(fos);
		
		// write version ID
		dos.writeShort(Constants.vidSubtreeGameDescription);

		//build header
		short[] header = new short[10];
		for(int i = 0; i < 10; i++) {
			header[i] = 0;
		}

		//write header
		for(int i = 0; i < 10; i++) {
			dos.writeShort(header[i]);
		}

		dos.writeFloat(p1Pot);
		dos.writeFloat(p2Pot);
		dos.writeInt(startClusterPDT.length);
		for(int i = 0; i < startClusterPDT.length; i++) {
			for(int j = 0; j < startClusterPDT[0].length; j++) {
				dos.writeDouble(startClusterPDT[i][j]);
			}
		}
		
		dos.close();
		fos.close();
		
	}
	
}
