/*
 * Created on Apr 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package unused;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import _game.Card;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PivotTextOntoHandCards {

	public static void main(String[] args) {
//		try { 
//			Class.forName("com.mysql.jdbc.Driver").newInstance();
//        } catch (Exception ex) {
//        	throw new RuntimeException("Failed to load JDBC/ODBC driver."); 
//        }

        try {
			double progressCounter = 0;
			double timer1 = System.currentTimeMillis();
			int i;
			int j;
        	String dir = "d:" + Constants.dirSep;
        	FileWriter[][] outs = new FileWriter[Card.NUM_CARDS][Card.NUM_CARDS];
			for(i = 0; i < (Card.NUM_CARDS-1); i++) {
				for(j = i+1; j < Card.NUM_CARDS; j++) {
	        		outs[i][j] = new FileWriter(dir + new Integer(i).toString() + "_" + new Integer(j).toString(), true);
				}
			}
			

			BufferedReader in = new BufferedReader(new FileReader(args[0]));
			String line;
			String lineArray[];
			while ((line = in.readLine()) != null) {
				progressCounter++;
//				if (progressCounter % 12995 == 0) {
				if (progressCounter % 14047379 == 0) {
					System.out.println ((System.currentTimeMillis() - timer1) + ": " + (progressCounter / 2809475.76 / 1000) + "% done");
				}
				lineArray = line.split("\t");
				outs[Integer.parseInt(lineArray[5], 10)]
					 [Integer.parseInt(lineArray[6], 10)].write(line + "\n");
			}
			
			for(i = 0; i < (Card.NUM_CARDS-1); i++) {
				for(j = i+1; j < Card.NUM_CARDS; j++) {
	        		outs[i][j].close();
				}
			}
			
			System.out.println(System.currentTimeMillis() - timer1);
			Runtime rt = Runtime.getRuntime();
			System.out.println(rt.totalMemory());

	    } catch (IOException ioe) {
	    	System.out.println(ioe.getMessage());
	    	System.out.println(ioe.getCause());
	    	throw new RuntimeException();
	    }
	}
}
