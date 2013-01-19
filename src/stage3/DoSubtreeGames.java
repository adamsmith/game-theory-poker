/*
 * Created on Jul 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3;

import _misc.*;
import _io.*;
import stage3.InfoSet.*;

import java.util.*;
import java.io.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DoSubtreeGames {
	
	public static final String ROOT_INPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
			"stage2" + Constants.dirSep;
	
	public static final String ROOT_GAME_INPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
			"stage3" + Constants.dirSep + "root" + Constants.dirSep;
	
	public static final String ROOT_OUTPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
			"stage3" + Constants.dirSep;
	
	private static final int MAX_SIMULT_FILES_OPEN = 4;
	
	private final static byte[] bytNums = new byte[] {0, 1, 2, 3, 4, 5};

	public static double[][] startPDT;
	public static float[][][][] transition0to3;
	public static float[][][][] transition3to4;
	public static float[][][][] transition4to5;
	public static float[][][][][] transition;
	public static double[][][] terminalValues;
	public static int[] numClusters;

	public static Map rootSolP1;
	public static Map rootSolP2;

	public static NameMap rootNamesP1;
	public static NameMap rootNamesP2;
	
	private static int numZeroLookups = 0;

	// DEFINE THE SUBTREES!
	// ----------------------------------------------
	// subtreeNames is imported from _misc.Constants
	public final static float[] p1Pots = new float[] {1, 2, 3, 4, 2, 3, 4};
	public final static float[] p2Pots = new float[] {1, 2, 3, 4, 2, 3, 4};

	public final static InfoString[][] decisionsP1 = new InfoString[][] {
			// subtree a:
			//  P1 checks
			//  P2 checks
			new InfoString[] {
				new InfoString(new byte[] {
					InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_call, false)})
			},
			// subtree b:
			//  P1 checks
			//  P2 raise
			//  P1 calls
			new InfoString[] {
					new InfoString(new byte[] {
						InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_call, false)}),
					new InfoString(new byte[] {
						InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_call, false),
						InfoToken.factory(bytNums[0], bytNums[1], InfoToken.s_raise, false),
						InfoToken.factory(bytNums[0], bytNums[2], InfoToken.s_call, false)})
			},
			// subtree c:
			//  P1 checks
			//  P2 raise
			//  P1 re-raise
			//  P2 calls
			new InfoString[] {
					new InfoString(new byte[] {
						InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_call, false)}),
					new InfoString(new byte[] {
						InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_call, false),
						InfoToken.factory(bytNums[0], bytNums[1], InfoToken.s_raise, false),
						InfoToken.factory(bytNums[0], bytNums[2], InfoToken.s_raise, false)})
			},
			// subtree d:
			//  P1 checks
			//  P2 raise
			//  P1 re-raise
			//  P2 re-re-raise
			//  P1 calls
			new InfoString[] {
					new InfoString(new byte[] {
						InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_call, false)}),
					new InfoString(new byte[] {
						InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_call, false),
						InfoToken.factory(bytNums[0], bytNums[1], InfoToken.s_raise, false),
						InfoToken.factory(bytNums[0], bytNums[2], InfoToken.s_raise, false)}),
					new InfoString(new byte[] {
						InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_call, false),
						InfoToken.factory(bytNums[0], bytNums[1], InfoToken.s_raise, false),
						InfoToken.factory(bytNums[0], bytNums[2], InfoToken.s_raise, false),
						InfoToken.factory(bytNums[0], bytNums[3], InfoToken.s_raise, false),
						InfoToken.factory(bytNums[0], bytNums[4], InfoToken.s_call, false)})
			},
			// subtree e:
			//  P1 raise
			//  P2 calls
			new InfoString[] {
					new InfoString(new byte[] {
						InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_raise, false)})
			},
			// subtree f:
			//  P1 raise
			//  P2 re-raise
			//  P1 calls
			new InfoString[] {
					new InfoString(new byte[] {
						InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_raise, false)}),
					new InfoString(new byte[] {
						InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_raise, false),
						InfoToken.factory(bytNums[0], bytNums[1], InfoToken.s_raise, false),
						InfoToken.factory(bytNums[0], bytNums[2], InfoToken.s_call, false)})
			},
			// subtree g:
			//  P1 raise
			//  P2 re-raise
			//  P1 re-re-raise
			//  P2 calls
			new InfoString[] {
					new InfoString(new byte[] {
						InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_raise, false)}),
					new InfoString(new byte[] {
						InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_raise, false),
						InfoToken.factory(bytNums[0], bytNums[1], InfoToken.s_raise, false),
						InfoToken.factory(bytNums[0], bytNums[2], InfoToken.s_raise, false)})
			}
	};
	
	public final static InfoString[][] decisionsP2 = new InfoString[][] {
			// subtree a:
			//  P1 checks
			//  P2 checks
			new InfoString[] {
				new InfoString(new byte[] {
						InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_call, false),
						InfoToken.factory(bytNums[0], bytNums[1], InfoToken.s_call, false)})
			},
			// subtree b:
			//  P1 checks
			//  P2 raise
			//  P1 calls
			new InfoString[] {
					new InfoString(new byte[] {
						InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_call, false),
						InfoToken.factory(bytNums[0], bytNums[1], InfoToken.s_raise, false)})
			},
			// subtree c:
			//  P1 checks
			//  P2 raise
			//  P1 re-raise
			//  P2 calls
			new InfoString[] {
					new InfoString(new byte[] {
						InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_call, false),
						InfoToken.factory(bytNums[0], bytNums[1], InfoToken.s_raise, false)}),
					new InfoString(new byte[] {
						InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_call, false),
						InfoToken.factory(bytNums[0], bytNums[1], InfoToken.s_raise, false),
						InfoToken.factory(bytNums[0], bytNums[2], InfoToken.s_raise, false),
						InfoToken.factory(bytNums[0], bytNums[3], InfoToken.s_call, false)})
			},
			// subtree d:
			//  P1 checks
			//  P2 raise
			//  P1 re-raise
			//  P2 re-re-raise
			//  P1 calls
			new InfoString[] {
					new InfoString(new byte[] {
						InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_call, false),
						InfoToken.factory(bytNums[0], bytNums[1], InfoToken.s_raise, false)}),
					new InfoString(new byte[] {
						InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_call, false),
						InfoToken.factory(bytNums[0], bytNums[1], InfoToken.s_raise, false),
						InfoToken.factory(bytNums[0], bytNums[2], InfoToken.s_raise, false),
						InfoToken.factory(bytNums[0], bytNums[3], InfoToken.s_raise, false)})
			},
			// subtree e:
			//  P1 raise
			//  P2 calls
			new InfoString[] {
					new InfoString(new byte[] {
						InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_raise, false),
						InfoToken.factory(bytNums[0], bytNums[1], InfoToken.s_call, false)})
			},
			// subtree f:
			//  P1 raise
			//  P2 re-raise
			//  P1 calls
			new InfoString[] {
					new InfoString(new byte[] {
						InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_raise, false),
						InfoToken.factory(bytNums[0], bytNums[1], InfoToken.s_raise, false)})
			},
			// subtree g:
			//  P1 raise
			//  P2 re-raise
			//  P1 re-re-raise
			//  P2 calls
			new InfoString[] {
					new InfoString(new byte[] {
						InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_raise, false),
						InfoToken.factory(bytNums[0], bytNums[1], InfoToken.s_raise, false)}),
					new InfoString(new byte[] {
						InfoToken.factory(bytNums[0], bytNums[0], InfoToken.s_raise, false),
						InfoToken.factory(bytNums[0], bytNums[1], InfoToken.s_raise, false),
						InfoToken.factory(bytNums[0], bytNums[2], InfoToken.s_raise, false),
						InfoToken.factory(bytNums[0], bytNums[3], InfoToken.s_call, false)})
			}
	};

	public static void main(String[] args) throws Exception {
		double tTotal = System.currentTimeMillis();
		double tStage = System.currentTimeMillis();
		Object[] inData = LoadInputData.getTreeData(true, ROOT_INPUT_DIR);
		startPDT = (double[][]) inData[0];
		transition0to3 = (float[][][][]) inData[1];
		transition3to4 = (float[][][][]) inData[2];
		transition4to5 = (float[][][][]) inData[3];
		terminalValues = (double[][][]) inData[4];
		transition = new float[][][][][] {
				transition0to3, new float[][][][] {}, new float[][][][] {}, transition3to4, transition4to5
		};
		
		numClusters = new int[] {
				startPDT.length,				// 0 bc's
				-1,								// 1
				-1,								// 2
				transition3to4.length,			// 3 bc's
				transition4to5.length,			// 4 bc's
				terminalValues[0].length			// 5 bc's
		};

		String inSolP1 = ROOT_GAME_INPUT_DIR + "game.p1.sol.obj";
		String inSolP2 = ROOT_GAME_INPUT_DIR + "game.p2.sol.obj";
		rootSolP1 = ReadBinarySolutionMap.getSolutionMap(inSolP1, true);
		rootSolP2 = ReadBinarySolutionMap.getSolutionMap(inSolP2, false);

		String inNamesP1 = ROOT_GAME_INPUT_DIR + "nameMap.p1.obj";
		String inNamesP2 = ROOT_GAME_INPUT_DIR + "nameMap.p2.obj";
		rootNamesP1 = ReadBinaryNameMap.getNameMap(inNamesP1);
		rootNamesP2 = ReadBinaryNameMap.getNameMap(inNamesP2);
		
		System.out.println("done loading input files in time: " +
				(System.currentTimeMillis() - tStage));
		tStage = System.currentTimeMillis();
		
		
		
		
		// we want to compute P(p1=cluster_i AND p2=cluster_j | move-seq) for 
		//  various move sequences (inc. chance moves) that result in different continuations of 
		//  the game (e.g. check-check versus raise-call).  We do this for both 
		//  players and then combine results.
		
		// do this using Bayes' theorem:
		//  P(p1=cluster_i AND p2=cluster_j | move-seq) = 
		//   ( P(p1=cluster_i AND p2=cluster_j) * P(move-seq | p1=cluster_i AND p2=cluster_j) ) 
		//   / P(move-seq)
		// let alpha = 1 / P(move-seq), then
		//  P(p1=cluster_i AND p2=cluster_j | move-seq) 
		//   = alpha * P(p1=cluster_i AND p2=cluster_j) * P(move-seq | p1=cluster_i AND p2=cluster_j)
		// notice that alpha does not depend on i or j, so it will be normalized out.  
		// So we take P(move-seq) out.  P(p1=cluster_i AND p2=cluster_j) comes from 
		//  startPDT and P(move-seq | p1=cluster_i AND p2=cluster_j) comes form rootSolPx
		
		
		int numSubTrees = Constants.subtreeNames.length;
		if(p1Pots.length != numSubTrees || p2Pots.length != numSubTrees || 
				decisionsP2.length != numSubTrees || decisionsP1.length != numSubTrees) {
			throw new RuntimeException();
		}

		for(int subTree = 0; subTree < numSubTrees; subTree++) {
			double[][] clusterPdt = new double[numClusters[0]][numClusters[0]];
			double sum = 0;
			for(byte i = 0; i < numClusters[0]; i++) {
				for(byte j = i; j < numClusters[0]; j++) {
					double pMovesGivenIJ = 1;
					
					// p1's moves
					for(int k = 0; k < decisionsP1[subTree].length; k++) {
						pMovesGivenIJ *= probLastMove(decisionsP1[subTree][k].prepend(
								InfoToken.factory(bytNums[0], DoGT.s_player1, i, true)), true);
					}
					
					// p2's moves
					for(int k = 0; k < decisionsP2[subTree].length; k++) {
						pMovesGivenIJ *= probLastMove(decisionsP2[subTree][k].prepend(
								InfoToken.factory(bytNums[0], DoGT.s_player2, j, true)), false);
					}
					
					double pIJGivenMoves = startPDT[i][j] * pMovesGivenIJ;
					
					if(pIJGivenMoves == 0) {
//						System.out.println("Warning: zero probability event");
					}
					
					sum += pIJGivenMoves;
					clusterPdt[i][j] = pIJGivenMoves;
				}
			}
			if(sum == 0) {
				System.out.println("BIG WARNING: ZERO PROBABILITY SUBTREE");
//				throw new RuntimeException();
			}
			System.out.println("backprop'ed " + Constants.subtreeNames[subTree] + ":");
			for(byte i = 0; i < numClusters[0]; i++) {
				for(byte j = i; j < numClusters[0]; j++) {
					clusterPdt[i][j] /= sum;
					System.out.println("  [" + i + ", " + j + "] = " + clusterPdt[i][j]);
				}
			}
			
			// now we have 'backpropogated' the move seq info back to 
			//   the startPDT at the root.  but we want to write a startPDT
			//   for the subtree!!
			// to do this, use the transition probabilities for 0->3
			
			double subtreeStartPDT[][] = new double[numClusters[3]][numClusters[3]];
			sum = 0;
			for(byte i = 0; i < numClusters[3]; i++) {
				for(byte j = i; j < numClusters[3]; j++) {
					// find the prob of ending up in {i, j} OR {j, i}
					double outcomeAccum = 0;
					for(byte k = 0; k < numClusters[0]; k++) {
						for(byte l = 0; l < numClusters[0]; l++) {
							// {k, l} -> {i, j}
							outcomeAccum += clusterPdt[Math.min(k, l)][Math.max(k, l)] *
									(transition0to3[k][l][i][j] + 
									transition0to3[k][l][j][i]) / 2;
						}
					}
					subtreeStartPDT[i][j] = outcomeAccum;
					sum += outcomeAccum;
				}
			}
			System.out.println("subgame " + Constants.subtreeNames[subTree] + ":");
			for(byte i = 0; i < numClusters[3]; i++) {
				for(byte j = i; j < numClusters[3]; j++) {
					subtreeStartPDT[i][j] /= sum; // this is bothersome -- why do we have to normalize?
					System.out.println("  [" + i + ", " + j + "] = " + subtreeStartPDT[i][j]);
				}
			}
			
			String dirName = ROOT_OUTPUT_DIR + Constants.subtreeNames[subTree] + Constants.dirSep;
			new File(dirName).mkdir();
			String outFile = dirName + "subtreeGameDescription";
			Helper.prepFilePath(outFile);
			WriteBinarySubtreeGame.writeSubtreeGameDescription(outFile, subtreeStartPDT, 
					p1Pots[subTree], p2Pots[subTree]);
		}

		System.out.println("done generating subtree game descriptions in time: " +
				(System.currentTimeMillis() - tStage));
		System.out.println("");
		System.out.println("done program in time: " +
				(System.currentTimeMillis() - tTotal));
		
	}
	
	private static float probLastMove(InfoString x, boolean isP1) {
		Map weights = (isP1 ? rootSolP1 : rootSolP2);
		NameMap names = (isP1 ? rootNamesP1 : rootNamesP2);
		
		if(InfoToken.isChance(x.arr[x.arr.length-1])) {
			throw new RuntimeException();
		}
		
		InfoString beforeX = getNextToLastAction(x, isP1);
		Integer beforeXName = new Integer(names.getShort(beforeX, false));
		Float beforeXFloat = (Float) weights.get(beforeXName);
		float weightBeforeX;
		if(beforeXFloat == null) {
			// not found in solution...value is zero
			numZeroLookups++;
			weightBeforeX = 0;
		} else {
			weightBeforeX = beforeXFloat.floatValue();
		}
		
		if(weightBeforeX == 0) {
			if(isP1) {
				System.out.println(beforeXName);
			}
			return 0;
		}

		Float xFloat = (Float) weights.get(new Integer(names.getShort(x, false)));
		float weightX;
		if(xFloat == null) {
			numZeroLookups++;
			weightX = 0;
		} else {
			weightX = xFloat.floatValue();
		}
		
		return weightX / weightBeforeX;
	}
	
	private static InfoString getNextToLastAction(InfoString x, boolean isP1) {
		int actionIndex = -1; // -1 is a legal value if answer is empty seq!
		if(x.arr.length < 3) {
			return InfoString.emptyInfoString;
		}
		
		for(int i = x.arr.length-2; i >= 0; i--) {
			if(InfoToken.isChance(x.arr[i])) {
				continue;
			}
			
			if(isP1) {
				if(InfoToken.isP1(x.arr[i])) {
					actionIndex = i;
					break;
				}
			} else {
				if(InfoToken.isP2(x.arr[i])) {
					actionIndex = i;
					break;
				}
			}
		}
		
		byte[] newArr = new byte[actionIndex+1];
		System.arraycopy(x.arr, 0, newArr, 0, actionIndex+1);
		InfoString answer = new InfoString(newArr);
//		System.out.println(x.toString() + " -> " + answer.toString() + "    " + (isP1 ? "(P1)" : "(P2)"));
		return answer;
	}
}
