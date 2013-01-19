/*
 * Created on Jul 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _io;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.*;

import stage3.*;
import _misc.Constants;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ReadBinaryConstraintMatrix {

	public static Object[] getCm(String fileName) throws Exception {

		FileInputStream fis = new FileInputStream(fileName);
		DataInputStream dis = new DataInputStream(fis);
		
		// read and verify format ID
		short formatID = dis.readShort();
		if(formatID != Constants.vidConstraintMatrix) {
			throw new RuntimeException();
		}
		
		// read header from file
		short header[] = new short[10];
		for(int i = 0; i < 10; i++) {
			header[i] = dis.readShort();
		}
		
		// process header
		int maxNumBoardCards = header[4];
		int numUniqueNames = header[6] & 0xFFFF; // do the AND so it's unsigned
		numUniqueNames <<= 16;
		numUniqueNames |= header[5] & 0xFFFF;
		if(numUniqueNames <= 0) {
			throw new RuntimeException();
			
		}

		ObjectInputStream ois = new ObjectInputStream(dis);
		
		ConstraintMatrix cm = (ConstraintMatrix) ois.readObject();
		
		ois.close();
		dis.close();
		fis.close();

		return new Object[] {cm, new Integer(maxNumBoardCards), new Integer(numUniqueNames)};
	}
}
