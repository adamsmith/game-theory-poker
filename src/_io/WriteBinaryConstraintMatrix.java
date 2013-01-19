/*
 * Created on Jul 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _io;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import stage3.*;
import _misc.Constants;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WriteBinaryConstraintMatrix {

	public static void writeConstraintMatrix(String outFile, ConstraintMatrix cm, 
			int[] numClusters, int maxNumBoardCards, int numUniqueNames) throws Exception {
		
		if(new File(outFile).exists()) {
			throw new RuntimeException("file already exists: " + outFile);
		}
		
		if(numClusters.length != 6) {
			throw new RuntimeException();
		}

		FileOutputStream fos = new FileOutputStream(outFile);
		DataOutputStream dos = new DataOutputStream(fos);
		
		// write version ID
		dos.writeShort(Constants.vidConstraintMatrix);

		//build header
		short[] header = new short[10];
		header[0] = (short) numClusters[0];
		header[1] = (short) numClusters[3];
		header[2] = (short) numClusters[4];
		header[3] = (short) numClusters[5];
		header[4] = (short) maxNumBoardCards;
		header[5] = (short) numUniqueNames;
		header[6] = (short) (numUniqueNames>>16);
		for(int i = 7; i < 10; i++) {
			header[i] = 0;
		}

		//write header
		for(int i = 0; i < 10; i++) {
			dos.writeShort(header[i]);
		}
		
		ObjectOutputStream oos = new ObjectOutputStream(dos);

		oos.writeObject(cm);
		
		oos.close();
		dos.close();
		fos.close();
		
	}
	
}
