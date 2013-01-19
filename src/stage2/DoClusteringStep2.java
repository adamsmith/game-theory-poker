/*
 * Created on May 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import stage2.Touple.*;
import stage1.HandRecordScore;
import stage1.*;
import _game.Card;
import _io.*;
import _io.ReadBinaryScoreMapsStream;
import _io.ReadBinaryScoreStream;
import _io.WriteBinaryClusterIDStream;
import _misc.Combinations;
import _misc.Constants;
import _misc.Helper;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DoClusteringStep2 {
	
	// convention is to always end a directory path with the backslash
	private static final String ROOT_INPUT_DIR_NOSTEP = Constants.DATA_FILE_REPOSITORY + 
		"stage1" + Constants.dirSep;
	private static final String ROOT_INPUT_DIR_STEP = Constants.DATA_FILE_REPOSITORY + 
		"stage2" + Constants.dirSep;
	private static final String ROOT_OUTPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
		"stage2" + Constants.dirSep;
	
	private static final int MAX_SIMULT_FILES_OPEN = 2;
	
	// running time is roughly linear in NUM_CLUSTERERS
	protected static final byte[] NUM_CLUSTERERS = 
		new byte[] {10, -1, -1, 4, 1, 1};
	
//	private static final double WEIGHT_PRECISION_FACTOR = 8.5;
	
	
	
	// --------------------------------------------------------------------------
	protected static final int MAX_IN_FILE = 2 * 
		Constants.choose(Card.NUM_CARDS - 7, 2);
	
//	private static final int weightPrecision = (int) Math.floor(((double) MAX_IN_FILE)/WEIGHT_PRECISION_FACTOR);
	
//	protected static final int[] MAX_IN_CLUSTERING = new int[] {
//			(int) (MAX_IN_FILE / RESOLUTION_DECREASE_FACTOR[0]),
//			(int) (MAX_IN_FILE / RESOLUTION_DECREASE_FACTOR[1]),
//			(int) (MAX_IN_FILE / RESOLUTION_DECREASE_FACTOR[2]),
//			(int) (MAX_IN_FILE / RESOLUTION_DECREASE_FACTOR[3]),
//			(int) (MAX_IN_FILE / RESOLUTION_DECREASE_FACTOR[4]),
//			(int) (MAX_IN_FILE / RESOLUTION_DECREASE_FACTOR[5])
//	};
//	
//	protected static final float[] STDDEV_IN_SMOOTHING = new float[] {
//			MAX_IN_CLUSTERING[0] / 25,
//			MAX_IN_CLUSTERING[1] / 25,
//			MAX_IN_CLUSTERING[2] / 25,
//			MAX_IN_CLUSTERING[3] / 25,
//			MAX_IN_CLUSTERING[4] / 25,
//			MAX_IN_CLUSTERING[5] / 25
//	};
	
//	protected static final int MAX_NUM_ITERS_IN_CLUSTERING = 12;
//	protected static final double TOLERANCE_BETWEEN_ITERS_IN_CLUSTERING = .05;
	
//	protected static float[] weight = new float[weightPrecision];

	private static final int s_PhaseCalculateClusters = 0;
	private static final int s_PhaseSelectClusterer = 1;
	private static final int s_PhaseWriteClusteringToDisk = 2;
	private static final int s_PhaseDone = 3;
	

	public static void main(String[] args) throws IOException {
		double tTotal = System.currentTimeMillis();

		int numBoardCards = Integer.parseInt(args[0]);
		int stopAtNumBoardCards = Integer.parseInt(args[1]);
		
		if(numBoardCards > stopAtNumBoardCards) {
			throw new RuntimeException();
		}
		
		while (numBoardCards <= stopAtNumBoardCards) {
			double tBoardCards = System.currentTimeMillis();
			System.out.println("");
			System.out.println("---------");
			System.out.println("Clustering " + numBoardCards + " boardcards");
			System.out.println("---------");
			
			int numClusterers = NUM_CLUSTERERS[numBoardCards];
			String inputDir = null;
			if(numBoardCards < 5) {
				inputDir = ROOT_INPUT_DIR_STEP + "clustering_step1_" + numBoardCards + "" + Constants.dirSep;
			} else {
				inputDir = ROOT_INPUT_DIR_NOSTEP + numBoardCards + "" + Constants.dirSep;
			}
			String outputDir = ROOT_OUTPUT_DIR + "clustering_step2_" + numBoardCards + "" + Constants.dirSep;

			int numScoreGroups = -1;
			if(numBoardCards < 5) { // if you move this, make sure it's scoped so that getNSG is GC'ed
				ReadBinaryScoreGroupStream getNSG = new ReadBinaryScoreGroupStream(
						inputDir+"0_1", numBoardCards, 1000);
				numScoreGroups = getNSG.getNumScoreGroups();
				getNSG.close();
			}

			ClustererStream[] clusterers = new ClustererStream[numClusterers];
			boolean[] clustererDone = new boolean[numClusterers];
			for(int i = 0; i < numClusterers; i++) {
				if(numBoardCards < 5) {
					clusterers[i] = new ClustererStream(Card.NUM_CLUSTERS[numBoardCards], numScoreGroups);
				} else {
					clusterers[i] = new ClustererStream(Card.NUM_CLUSTERS[numBoardCards], 1);
				}
				clustererDone[i] = false;
			}

			int numHands = Constants.choose(Card.NUM_CARDS, 2) *
					Constants.choose(Card.NUM_CARDS-2, numBoardCards);


			
			
			
			
			
			System.out.println("Entering phase 1 -- clustering...");
			double tPhase = System.currentTimeMillis();
			int iterCounter = 1;
			int phase = s_PhaseCalculateClusters;
			int minErrorCluster = -1;
			while(phase != s_PhaseDone) {
				System.out.println("");
				double tIteration = System.currentTimeMillis();
				
				// set up tasks
				switch(phase) {
					case s_PhaseCalculateClusters:
						for(int i = 0; i < numClusterers; i++) {
							if(clustererDone[i] == false) {
								clusterers[i].beginSampleRound();
							}
						}
						break;
						
					
					case s_PhaseSelectClusterer:
						// wrap up previous phase
						System.out.println("   Completed cluster calculation phase in time: " + (System.currentTimeMillis() - tPhase));
						
						// start this phase
						System.out.println("Entering phase 2 --  computing total errors...");
						tPhase = System.currentTimeMillis();
						for(int i = 0; i < numClusterers; i++) {
							clusterers[i].startErrorComputeRound();
						}
						break;
						
						
					case s_PhaseWriteClusteringToDisk:
						// wrap up previous phase
						System.out.println("   Completed clusterer selection phase in time: " + (System.currentTimeMillis() - tPhase));
						
						// start this phase
						// write cluster IDs to disk
						System.out.println("Entering phase 3 -- writing to disk...");
						tPhase = System.currentTimeMillis();
						break;
						
					default:
						throw new RuntimeException();						
				}
				
				
				for(int i = 0; i < (Card.NUM_CARDS-1); i++) {
					double tSubiteration = System.currentTimeMillis();
					for(int j = i+1; j < Card.NUM_CARDS; j++) {
						
						// load up input file
						ReadBinaryData in;
						String path = inputDir + new Integer(i).toString() + "_" + new Integer(j).toString();
						if(numBoardCards == 5) {
							in = new ReadBinaryScoreStream(path, numBoardCards, Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
						} else if(numBoardCards == 0 || numBoardCards == 3 || numBoardCards == 4) {
							in = new ReadBinaryScoreGroupStream(path, numBoardCards, Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
						} else {
							throw new RuntimeException();
						}
						
						WriteBinaryClusterIDStream out = null;
						if(phase == s_PhaseWriteClusteringToDisk) {
							String outPath = outputDir + new Integer(i).toString() + "_" +  new Integer(j).toString();
							Helper.prepFilePath(outPath);
							out = new WriteBinaryClusterIDStream(
									outPath,
									numBoardCards, 
									new byte[] {(byte) i, (byte) j},
									Card.NUM_CLUSTERS[numBoardCards], 
									Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
						}
						
						HandRecord hrsm;
						// if numBoardCards < 5 then in.readRecord() returns a HandRecordScoreGroups
						//   else, in.readRecord() returns a HandRecordScore
						while((hrsm = in.readRecord()) != null) {
							int[] sample;
							
							if(numBoardCards == 5) {
								sample = new int[] {((HandRecordScore)hrsm).score};
							} else if(numBoardCards == 0 || numBoardCards == 3 || numBoardCards == 4) {
								sample = ((HandRecordScoreGroups)hrsm).scoreGroups;
							} else {
								throw new RuntimeException();
							}
							
							switch(phase) {
								case s_PhaseCalculateClusters:
									int a= 1;
									for(int k = 0; k < numClusterers; k++) {
										if(!clustererDone[k]) {
											clusterers[k].addSample(sample);
										}
									}
									break;
									
								case s_PhaseSelectClusterer:
									for(int k = 0; k < numClusterers; k++) {
										clusterers[k].addSample(sample);
									}
									break;
									
								case s_PhaseWriteClusteringToDisk:
									byte cluster_id = clusterers[minErrorCluster].getMembership(sample);
									out.putClusterID(cluster_id);
									break;
									
								default:
									throw new RuntimeException();
							}
						}
						in.close();
						in = null;
						if(phase == s_PhaseWriteClusteringToDisk) {
							out.close();
							out = null;
						}
						System.runFinalization();
						System.gc();
						System.gc();
						System.gc();
					}
					System.out.println("   Iteration " + ((int) Math.floor(100*(((double) i/(Card.NUM_CARDS-2))))) + "% done in time: " + (System.currentTimeMillis() - tSubiteration));
				}
				
				
				// close down tasks
				switch(phase) {
				
					case s_PhaseCalculateClusters:
						String clustererDoneStr = "[ ";
						for(int i = 0; i < numClusterers; i++) {
							if(!clustererDone[i]) {
								clustererDone[i] = clusterers[i].doneSampleRound();
							}
							clustererDoneStr += (new Boolean(clustererDone[i])).toString() + " ";
						}
						iterCounter++;
						
						System.out.println("");
						System.out.println("   Completed iteration " + iterCounter + " in time: " + (System.currentTimeMillis() - tIteration));
						System.out.println("   clustererDone == " + clustererDoneStr + " ]");
						
						if(Helper.allTrue(clustererDone)) {
							phase = s_PhaseSelectClusterer;
						}
						break;
						
					case s_PhaseSelectClusterer:
						for(int i = 0; i < numClusterers; i++) {
							clusterers[i].endErrorComputeRound();
						}
						// pick the clusterer with lowest total error
						double minError = Double.MAX_VALUE;
						minErrorCluster = -1;
						for(int i = 0; i < numClusterers; i++) {
							double error = clusterers[i].getError();
							System.out.println("   Clusterer error: " + error);
							
							if(error < minError) {
								minError = error;
								minErrorCluster = i;
							}
						}
						System.out.println("   Completed phase in time: " + (System.currentTimeMillis() - tPhase));
						System.out.println("");
						System.out.println("");
						System.out.println("Priting clusters...");
						clusterers[minErrorCluster].print();
						System.out.println("");
						System.out.println("");
						phase = s_PhaseWriteClusteringToDisk;
						break;
						
					case s_PhaseWriteClusteringToDisk:
						System.out.println("   Completed phase in time: " + (System.currentTimeMillis() - tPhase));
						phase = s_PhaseDone;
						break;
						
					default:
						throw new RuntimeException();
				}
			}

			System.out.println("numBoardCards " + numBoardCards + " completed in time: " + (System.currentTimeMillis() - tBoardCards));
			
			switch(numBoardCards) { 
			case 0 : numBoardCards=3; break;
			case 3 : numBoardCards=4; break;
			case 4 : numBoardCards=5; break;
			case 5 : numBoardCards=Integer.MAX_VALUE; break;
			default : throw new RuntimeException(); }
		}
		System.out.println("All clustering completed in time: " + (System.currentTimeMillis() - tTotal));
	}
	
}
