/*
 * Created on Jun 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import stage1.HandRecord;
import stage1.HandRecordClusterId;
import stage1.HandRecordScore;
import stage1.HandRecordScoreMap;
import stage2.Touple.ToupleFloatComparator;
import stage2.Touple.ToupleFloatInt;
import stage2.Touple.ToupleIntComparator;
import _game.Card;
import _io.*;
import _io.WriteBinaryClusterIDTableStream;
import _misc.ByteArrayWrapper;
import _misc.Combinations;
import _misc.Constants;
import _misc.Helper;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DoClusteringStep1 {

	private static final int[] NUM_SCORE_GROUPS = 
		new int[] {2, -1, -1, 2, 2};
	
	private static final String ROOT_INPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
	"stage1" + Constants.dirSep;
	
	private static final String ROOT_OUTPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
	"stage2" + Constants.dirSep;
	
	private static final int MAX_SIMULT_FILES_OPEN = 2;
	
	public static void main(String[] args) throws IOException {

		double tTotal = System.currentTimeMillis();
		
		int numBoardCards = Integer.parseInt(args[0]);
		while (numBoardCards <= 4) {
			double tBoardCards = System.currentTimeMillis();
			System.out.println("");
			System.out.println("---------");
			System.out.println("Clustering step 1 - " + numBoardCards + " boardcards");
			System.out.println("---------");

			String inputDir = ROOT_INPUT_DIR + numBoardCards + "" + Constants.dirSep;
			String outputDir = ROOT_OUTPUT_DIR + "clustering_step1_" + numBoardCards + "" + Constants.dirSep;
			
			for(int i = 0; i < (Card.NUM_CARDS-1); i++) {
				double tSubiteration = System.currentTimeMillis();
				for(int j = i+1; j < Card.NUM_CARDS; j++) {

					String inPath = inputDir + new Integer(i).toString() + "_" + new Integer(j).toString();
					ReadBinaryScoreMapsStream in = new ReadBinaryScoreMapsStream(inPath, 
							numBoardCards, Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));

					String outPath = outputDir + new Integer(i).toString() + "_" + new Integer(j).toString();
					Helper.prepFilePath(outPath);
					WriteBinaryScoreGroupStream out = new WriteBinaryScoreGroupStream(outPath,
							numBoardCards, in.getHoleCards(), NUM_SCORE_GROUPS[numBoardCards],
							Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
					
					HandRecord hrsm;

					while((hrsm = in.readRecord()) != null) {
						short[] sample;
						sample = handRecordScoreMapToArray((HandRecordScoreMap) hrsm, numBoardCards);
						out.putScoreGroup(sample);
					}
					
					out.close();
				}
				System.out.println("   Iteration " + ((int) Math.floor(100*(((double) i/(Card.NUM_CARDS-2))))) + "% done in time: " + (System.currentTimeMillis() - tSubiteration));
			}

			System.out.println("numBoardCards " + numBoardCards + " completed in time: " + (System.currentTimeMillis() - tBoardCards));
			switch(numBoardCards) { 
			case 0 : numBoardCards=3; break;
			case 3 : numBoardCards=4; break;
			case 4 : numBoardCards=Integer.MAX_VALUE; break;
			default : throw new RuntimeException(); }
		}
		System.out.println("Clustering step 1 completed in time: " + (System.currentTimeMillis() - tTotal));
	}
	
	private static short[] handRecordScoreMapToArray(
			HandRecordScoreMap in, int numBoardCards) {
		
		int[] toReturn = new int[NUM_SCORE_GROUPS[numBoardCards]];
		
		List recs = new ArrayList();
		
		for(Iterator i = in.scoreMap.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry tmpRec = (Map.Entry) i.next();
			recs.add(new ToupleFloatInt((float)((Short) tmpRec.getKey()).shortValue(),
					((Integer) tmpRec.getValue()).intValue()));
		}
		
		return getTopScoreGroups(recs, numBoardCards);
	}
	
	private static short[] getTopScoreGroups(List recs, int numBoardCards) {
		// input should be ordered in ascending order based on recScore
		int numScoreGroups = NUM_SCORE_GROUPS[numBoardCards];
		
		if(recs.size() < numScoreGroups) {
			// not a good situation
			Collections.sort(recs, new ToupleIntComparator());
			short[] paddedScores = new short[numScoreGroups];
			for(int i = 0; i < recs.size(); i++) {
				paddedScores[numScoreGroups - i - 1] = (short)Math.round(((ToupleFloatInt)recs.get(recs.size() - i - 1)).o1);
			}
			for(int i = 0; i < numScoreGroups-recs.size(); i++) {
				paddedScores[i] = (short)Math.round(((ToupleFloatInt)recs.get(0)).o1);
			}
			return paddedScores;
		}
		
		while(recs.size() > numScoreGroups) {
			// take the two closest scores, merge them
			
			Collections.sort(recs, new ToupleFloatComparator());
			
			float minDist = Float.MAX_VALUE;
//			boolean tie = false;
			int minDistIndex= -1;
			float prevVal = ((ToupleFloatInt)recs.get(0)).o1;
			for(int i = 0; i < (recs.size()-1); i++) {
				float thisVal = ((ToupleFloatInt)recs.get(i+1)).o1;
				float dist = thisVal-prevVal;
				if(dist < minDist) {
					minDist = dist;
					minDistIndex = i;
				}
				prevVal = thisVal;
			}

			ToupleFloatInt greater = (ToupleFloatInt) recs.get(minDistIndex+1);
			ToupleFloatInt lesser = (ToupleFloatInt) recs.get(minDistIndex);
			
			double greaterWeight = ((double) greater.o2/
					(greater.o2+lesser.o2));
			greater.o1 = (float) (greaterWeight*greater.o1+(1-greaterWeight)*lesser.o1);
			greater.o2 = greater.o2 + lesser.o2;
			
			//now delete lesser from the list
			recs.remove(minDistIndex);
		}
		
		Collections.sort(recs, new ToupleIntComparator());
		
		short[] topScores = new short[numScoreGroups];
		for(int i = 0; i < numScoreGroups; i++) {
			topScores[i] = (short) Math.round(((ToupleFloatInt)recs.get(i)).o1);
		}
		
		return topScores;
	}

}
