/*
 * Created on May 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage1;

import java.io.File;
import java.io.IOException;

import _game.Card;
import _io.ReadBinaryScoreStream;
import _io.WriteBinaryScoreMaps;
import _misc.Combinations;
import _misc.Constants;
import _misc.Helper;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DoBacktracking {
	

	private static final String ROOT_OUTPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
		"stage1" + Constants.dirSep;
	
	private static final String ROOT_INPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
		"stage1" + Constants.dirSep;
	
	private static final int MAX_SIMULT_FILES_OPEN = 1;

	public static void main(String[] args) throws IOException {
		double tTotal = System.currentTimeMillis();

		byte[] holeCards;
		
		int numIterations = Constants.choose(Card.NUM_CARDS, 2);
		int iterationCounter = 0;
		while((holeCards = getNextTask()) != null) {
			System.out.println("   Executing on hole cards: " + Helper.byteArrayToString(holeCards) + "...");
		
			double tIteration = System.currentTimeMillis();
			System.gc();
			
			String inFile = ROOT_INPUT_DIR + "5" + Constants.dirSep + holeCards[0] + "_" + holeCards[1];
			
			String outFile4 = ROOT_OUTPUT_DIR + "4" + Constants.dirSep + holeCards[0] + "_" + holeCards[1];
			String outFile3 = ROOT_OUTPUT_DIR + "3" + Constants.dirSep + holeCards[0] + "_" + holeCards[1];
			String outFile0 = ROOT_OUTPUT_DIR + "0" + Constants.dirSep + holeCards[0] + "_" + holeCards[1];

			ScoreMaps sm4 = new ScoreMaps((byte) 4, holeCards);
			ScoreMaps sm3 = new ScoreMaps((byte) 3, holeCards);
			ScoreMaps sm0 = new ScoreMaps((byte) 0, holeCards);
			
			ReadBinaryScoreStream in = 
				new ReadBinaryScoreStream(inFile, 5, Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));

			HandRecordScore hr ;
			
			while ((hr = (HandRecordScore)in.readRecord()) != null) {					
				sm4.addScore(hr, hr.score);
				sm3.addScore(hr, hr.score);
				sm0.addScore(hr, hr.score);
			}

			in.close();

			Helper.prepFilePath(outFile4);
			Helper.prepFilePath(outFile3);
			Helper.prepFilePath(outFile0);
			
			WriteBinaryScoreMaps.writeScoreMapHR(sm4, outFile4, Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
			WriteBinaryScoreMaps.writeScoreMapHR(sm3, outFile3, Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
			WriteBinaryScoreMaps.writeScoreMapHR(sm0, outFile0, Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));

			deleteLock(holeCards);
			System.out.println("   Iteration " + ((int) Math.floor(100*((double) ++iterationCounter/numIterations))) + "% done in time: " + (System.currentTimeMillis() - tIteration));
		}

		System.out.println("All possible backtracking completed in time: " + (System.currentTimeMillis() - tTotal));
		
	}
		
		private static byte[] getNextTask() throws IOException {
			Combinations combo = new Combinations(Card.ALLCARDSINDEX, 2);
			byte[] holeCards;
			
			while(combo.hasMoreElements()) {
				holeCards = combo.nextElement();
				
				// disqualified if it's already been done
				if(new File(getOneOutputFileName(holeCards)).exists()) {
					continue;
				}
				
				// disqualified if it's currently being done...otherwise lock it
				//  (check for lock, and lock, atomically)
				File lock = new File(getInputFileName(holeCards) + ".lock");
				if(lock.createNewFile()) {
					// we got a lock!
					return holeCards;
				}
			}
			
			// we couldn't find any tasks
			return null;
		}
		
		private static void deleteLock(byte[] holeCards) {
			File lock = new File(getInputFileName(holeCards) + ".lock");
			if(!lock.exists()) {
				throw new RuntimeException();
			}
			lock.delete();
		}
		
		private static String getInputFileName(byte[] holeCards) {
			return ROOT_INPUT_DIR + "5" + Constants.dirSep + holeCards[0] 
					+ "_" + holeCards[1];
		}
		
		private static String getOneOutputFileName(byte[] holeCards) {
			return ROOT_OUTPUT_DIR + "4" + Constants.dirSep + holeCards[0] 
					+ "_" + holeCards[1];
		}
}
