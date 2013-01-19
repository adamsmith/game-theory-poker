/*
 * Created on Jun 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _io;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.*;
import java.io.ObjectInputStream;

import stage1.HandRecord;
import stage3.*;
import _misc.Constants;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ReadBinaryNameMap {
	
	public static NameMap getNameMap(String fileName) throws Exception {

		FileInputStream fis = new FileInputStream(fileName);
		DataInputStream dis = new DataInputStream(fis);
		
		// read and verify format ID
		short formatID = dis.readShort();
		if(formatID != Constants.vidTreeNodeNameMap) {
			throw new RuntimeException();
		}
		
		// read header from file
		short header[] = new short[10];
		for(int i = 0; i < 10; i++) {
			header[i] = dis.readShort();
		}
		
		// process header
		// [...]

		ObjectInputStream ois = new ObjectInputStream(dis);
		
		NameMap toReturn = (NameMap) ois.readObject();

		ois.close();
		dis.close();
		fis.close();

		return toReturn;
	}

}
