/*
 * Created on May 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package unused;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import _misc.Constants;
/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CorrectBeginningOffset {

	public static void fixOffset(String inFile) {
		try{
			String outFile = inFile + "cc";
    		BufferedWriter out = new BufferedWriter(new FileWriter(outFile), Constants.BUFFER_SIZE);
			BufferedReader in = new BufferedReader(new FileReader(inFile), Constants.BUFFER_SIZE);
			String line;

			line = in.readLine();
			char lastVal = line.charAt(0);
			char thisVal;
			int stage = 0;
			int counter = 0;
			while ((line = in.readLine()) != null) {
				thisVal = line.charAt(0);
				if(thisVal < lastVal) {
					stage++;
				}
				lastVal = thisVal;
				if(stage == 2) {
					break;
				}
				counter++;
			}
			
			out.write(line + "\n");
			while ((line = in.readLine()) != null) {
				out.write(line + "\n");
			}
			
			in.close();
			in = new BufferedReader(new FileReader(inFile), Constants.BUFFER_SIZE);
			for(int i = 0; i <= counter; i++) {
				out.write(in.readLine() + "\n");
			}
			
			in.close();
			out.close();
			
			
			File original = new File(inFile);
			File tmp = new File(outFile);
			
			original.delete();
			tmp.renameTo(new File(inFile+"c"));
			
		} catch (IOException e) {
			throw new RuntimeException();
		}
		
	}
}
