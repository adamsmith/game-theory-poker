/*
 * Created on Jul 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3;

import java.io.BufferedReader;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import _io.*;
import _misc.Constants;
import _misc.Helper;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InterpretMPS {
	
	public static final String SUB_DIR = "root" + Constants.dirSep;
	
	public static final String ROOT_INPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
			"stage3" + Constants.dirSep + SUB_DIR;

	public static void main(String[] args) throws Exception {

		NameMap nmPri = ReadBinaryNameMap.getNameMap(ROOT_INPUT_DIR + "nameMap.p1.obj");
		String inName = ROOT_INPUT_DIR + "game.p1.mps";
		
		BufferedReader in = new BufferedReader(new FileReader(inName),
				Helper.getBufferSize(2));
		String outName = inName + ".interpreted";
		BufferedWriter out = new BufferedWriter(new FileWriter(outName),
				Helper.getBufferSize(2));
		String line;



		while ((line = in.readLine()) != null) {
			int index = -1;
			
			while((index = line.indexOf("p", index+1)) >= 0) {
				int numEndIndex = line.indexOf(" ", index+1);
				if(numEndIndex == -1) {
					numEndIndex = line.length();
				}
				int num = Integer.valueOf(line.substring(index+1, numEndIndex)).intValue();
				String val = nmPri.getLong(num).toString();
				line = line.substring(0, index) + val + line.substring(numEndIndex);
			}
			index = -1;
			while((index = line.indexOf("x", index+1)) >= 0) {
				int numEndIndex = line.indexOf(" ", index+1);
				if(numEndIndex == -1) {
					numEndIndex = line.length();
				}
				int num = Integer.valueOf(line.substring(index+1, numEndIndex)).intValue();
				String val = nmPri.getLong(num).toString();
				line = line.substring(0, index) + val + line.substring(numEndIndex);
			}
			index = -1;
			while((index = line.indexOf("y", index+1)) >= 0) {
				int numEndIndex = line.indexOf(" ", index+1);
				if(numEndIndex == -1) {
					numEndIndex = line.length();
				}
				int num = Integer.valueOf(line.substring(index+1, numEndIndex)).intValue();
				String val = nmPri.getLong(num).toString();
				line = line.substring(0, index) + val + line.substring(numEndIndex);
			}
			
			out.write(line);
			out.newLine();
		}
		
		in.close();
		out.close();
	}
}
