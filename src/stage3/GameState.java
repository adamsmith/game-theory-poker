/*
 * Created on Jun 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3;

import stage3.InfoSet.*;
import java.util.*;

import _game.Card;
import _io.*;
import _misc.Constants;

import java.io.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GameState {
	
	// Game history
	//	 take advantage of depth-first enum.
	private static InfoString infoP1; 
	private static InfoString infoP2;
	private static InfoString lastChoiceP1;
	private static InfoString lastChoiceP2;
	public static float potP1 = Float.NaN;
	public static float potP2 = Float.NaN;
	public static float chanceSoFar = Float.NaN;
	public static byte[] clusters;

	// the results of all the game tree execution
	public static WriteBinaryRmeStream rewardMatrixOut;  // instantiated by DoGT iff DoGT.writeToDisk
	private static ConstraintMatrix constraintP1;
	private static ConstraintMatrix constraintP2;
	private static NameMap nameMapP1;
	private static NameMap nameMapP2;
	
	// handled before releasing name maps
	public static int numRows = Integer.MIN_VALUE;
	public static int numCols = Integer.MIN_VALUE;

	public static int startNumBoardCards = Integer.MIN_VALUE; // recursion terminating condition
	public static int endNumBoardCards = Integer.MIN_VALUE; // recursion terminating condition
	
	public static long leafCount = Long.MIN_VALUE;
	public static long nnzLeafCount = Long.MIN_VALUE; // nnz = num. of nonzero (notation from MATLAB)
	
	public static void initStaticState(int startNumBoardCards, int endNumBoardCards, float initialPotP1, float initialPotP2) {
		infoP1 = new InfoString(new byte[0]); 
		infoP2 = new InfoString(new byte[0]);
		lastChoiceP1 = new InfoString(new byte[0]);
		lastChoiceP2 = new InfoString(new byte[0]);
		potP1 = initialPotP1; 
		potP2 = initialPotP2;
		chanceSoFar = 1;
		clusters = new byte[] {-1, -1};
		
		constraintP1 = new ConstraintMatrix();
		constraintP2 = new ConstraintMatrix();
		nameMapP1 = new NameMap();
		nameMapP2 = new NameMap();

		GameState.startNumBoardCards = startNumBoardCards;
		GameState.endNumBoardCards = endNumBoardCards;
		numRows = -1;
		numCols = -1;
		
		leafCount = 0;
		nnzLeafCount = 0;
	}
	
	

	public byte bcCount; // if this is a chance node, this is bcCount after node
	public boolean isChance;
	public float chance = (float)-1.0;
	public byte brDepth; // br = Betting Round...N/A iff(isChance)
	public byte numRaises;
	public boolean isLeaf;
	public byte[] newClusters;

	public byte newInfoP1;
	public byte newInfoP2;
	public float potAddP1;
	public float potAddP2;

	public InfoString infoSetNameP1 = new InfoString(new byte[0]); // these init values will be overwriten for everyone
	public InfoString infoSetNameP2 = new InfoString(new byte[0]);  // other than the root (empty sequence)
	
	public static void releaseNameMaps() {
		ensureInitRoots();
		ensureNumRowsAndColsPopulated();
		nameMapP1 = null;
		nameMapP2 = null;
	}
	
	public void expand() throws IOException {
		List nextStates = Agenda.getNextGameStates(this);
		
		boolean isP1Choosing = false;
		boolean isP2Choosing = false;
		byte choiceType = ((GameState)nextStates.get(0)).newInfoP1;
		if(!InfoToken.isChance(choiceType)) {
			if(InfoToken.isP1(choiceType)) {  // it's not going to be both
				isP1Choosing = true;
			} else {
				isP2Choosing = true;
			}
		}
		
		
		InfoString lastChoiceP1Backup = lastChoiceP1.duplicate();
		InfoString lastChoiceP2Backup = lastChoiceP2.duplicate();
		
		int[] childNames = new int[nextStates.size()];
		
		for(int i = 0; i < nextStates.size(); i++) {
			GameState nextState = (GameState) nextStates.get(i);
			nextState.infoSetNameP1 = this.infoSetNameP1.push(nextState.newInfoP1);
			nextState.infoSetNameP2 = this.infoSetNameP2.push(nextState.newInfoP2);
			
			if(isP1Choosing) {
				childNames[i] = nameMapP1.getShort(nextState.infoSetNameP1, true);
				lastChoiceP1 = nextState.infoSetNameP1;
			} else if(isP2Choosing) {
				childNames[i] = nameMapP2.getShort(nextState.infoSetNameP2, true);
				lastChoiceP2 = nextState.infoSetNameP2;
			}

			infoP1 = infoP1.push(nextState.newInfoP1); // commit my child's action to InfoToken log
			infoP2 = infoP2.push(nextState.newInfoP2); // commit my child's action to InfoToken lo

			if((nextState.potAddP1 > 0 && nextState.potAddP2 > 0) || 
					nextState.potAddP1 < 0 || nextState.potAddP2 < 0) {
				throw new RuntimeException();
			}
			potP1 += nextState.potAddP1;
			potP2 += nextState.potAddP2;
			
			byte[] oldClustersCopy = null;
			float oldChanceSoFarCopy = Float.NaN;
			if(nextState.newClusters != null) {
				oldClustersCopy = new byte[] {clusters[0], clusters[1]};
				clusters = nextState.newClusters;
				oldChanceSoFarCopy = chanceSoFar;
				chanceSoFar *= nextState.chance;
			}
			
			if(!nextState.isLeaf) {
				nextState.expand();
			} else {
				// this only occurs on choice nodes
				
				if(DoGT.writeToDisk) {
					byte active = -1;
					if(isP1Choosing) {
						active = nextState.newInfoP1;
					} else if(isP2Choosing) {
						active = nextState.newInfoP2;
					} else {
						throw new RuntimeException();
					}
					float leafValue = Float.MIN_VALUE; // leafValue is set in every branch of this if statement
					if(InfoToken.value(active) == InfoToken.s_fold) {
						if(isP1Choosing) {
							leafValue = -1 * potP1; // p2 gets p1's pot
						} else if(isP2Choosing) {
							leafValue = potP2; // p1 gets p2's pot
						} else {
							throw new RuntimeException();
						}
					} else if(InfoToken.value(active) == InfoToken.s_call) {
						// showdown
						// invariant: potP1 == potP2
						if(clusters[0] == clusters[1]) {
							leafValue = 0; // expectation: net zero
						} else if(clusters[0] < clusters[1]) {
							float pWin = (float)DoGT.terminalValues[bcCount][clusters[0]][clusters[1]];
							leafValue = potP1 * (pWin*2-1); // second factor gives -1..1 range
						} else {
							float pWin = (float) (1-DoGT.terminalValues[bcCount][clusters[1]][clusters[0]]);
							leafValue = potP1 * (pWin*2-1); // second factor gives -1..1 range
						}
					} else {
						throw new RuntimeException();
					}
					leafValue *= chanceSoFar;
					
					int rowId = nameMapP1.getShort(lastChoiceP1, true);
					int columnId = nameMapP2.getShort(lastChoiceP2, true);
					
					if(leafValue != 0) {
						nnzLeafCount++;
						rewardMatrixOut.writeRme(rowId, columnId, leafValue);
					}
				}
				
				leafCount++;
				

				if (leafCount % DoGT.fiveThousandths == 0) {
					System.out.println("  " +
							(System.currentTimeMillis() - DoGT.tTotal) + 
							": " + (new Double((double)leafCount / 
							DoGT.numLeafNodes)).toString() + "% done");
				}
				
				
				// write out results
//				System.out.println("pot: {" + potP1 + ", " + potP2 + "}");
//				if(isP1Choosing) {
//					System.out.println("infoSets: [" + nextState.infoSetNameP1 + ", " + infoSetNameP2 + "]");
//				} else {
//					System.out.println("infoSets: [" + infoSetNameP1 + ", " + nextState.infoSetNameP2 + "]");
//				}
//				System.out.println("chance: " + chanceSoFar);
//				System.out.println("leafValue: " + leafValue);
//				System.out.println("Adjusted leafValue: " + (leafValue * chanceSoFar));
//				System.out.println("");
			}
			
			infoP1 = infoP1.pop();
			infoP2 = infoP2.pop();

			lastChoiceP1 = lastChoiceP1Backup;
			lastChoiceP2 = lastChoiceP2Backup;
			
			potP1 -= nextState.potAddP1;
			potP2 -= nextState.potAddP2;
			
			if(nextState.newClusters != null) {
				clusters = oldClustersCopy;
				chanceSoFar = oldChanceSoFarCopy;
			}
		}

		if(isP1Choosing) {
			constraintP1.addConstraint(nameMapP1.getShort(lastChoiceP1, true), childNames);
		} else if(isP2Choosing) {
			constraintP2.addConstraint(nameMapP2.getShort(lastChoiceP2, true), childNames);		
		}
	}
	

	public GameState(byte bcCount, byte brDepth, byte numRaises, boolean isChance, float chance, boolean isLeaf,
			byte newInfoP1, byte newInfoP2, float potAddP1, float potAddP2, byte[] newClusters) {
		this.bcCount = bcCount;
		this.brDepth = brDepth;
		this.numRaises = numRaises;
		this.isChance = isChance;
		this.chance = chance;
		this.isLeaf = isLeaf;
		this.newInfoP1 = newInfoP1;
		this.newInfoP2 = newInfoP2;
		this.potAddP1 = potAddP1;
		this.potAddP2 = potAddP2;
		this.newClusters = newClusters;
	}
	
	public GameState(byte bcCount, byte brDepth, byte numRaises, boolean isChance, float chance, boolean isLeaf,
			byte newInfoForAll, float potAddP1, float potAddP2, byte[] newClusters) {
		this(bcCount, brDepth, numRaises, isChance, chance, isLeaf, newInfoForAll, 
				newInfoForAll, potAddP1, potAddP2, newClusters);
	}
	
//	private static void ensureRewardMatrixMoved() {
//		if(arrRewardMatrix != null) {
//			return;
//		}
//		
//		if(leafCount != DoGT.numLeafNodes) {
//			throw new RuntimeException();
//		}
//		
//		arrRewardMatrix = new RewardMatrixElement[rewardMatrix.size()];
//		for(int i = 0; i < arrRewardMatrix.length; i++) {
//			arrRewardMatrix[i] = (RewardMatrixElement) rewardMatrix.get(i);
//		}
//		
//		rewardMatrix = null;
//		System.runFinalization();
//		System.gc();
//		System.gc();
//		System.gc();
//		System.gc();
//	}
	
//	public static RewardMatrixElement[] getRewardMatrix() {
//		ensureRewardMatrixMoved();
//		
//		return arrRewardMatrix;
//	}
	
	public static void ensureInitRoots() {
		if(!constraintP1.rootInitialized) {
			constraintP1.addConstraint(-1, new int[] {nameMapP1.emptySequenceName});
			constraintP1.rootInitialized = true;
		}
		if(!constraintP2.rootInitialized) {
			constraintP2.addConstraint(-1, new int[] {nameMapP2.emptySequenceName});
			constraintP2.rootInitialized = true;
		}
	}
	
	private static void ensureNumRowsAndColsPopulated() {
		if(numRows == -1) {
			numRows = nameMapP1.numUniqueNames();
			numCols = nameMapP2.numUniqueNames();
		}
	}
	
	public static ConstraintMatrix[] getConstraintMatrices() {
		ensureInitRoots();
		return new ConstraintMatrix[] { constraintP1, constraintP2 };
	}
	
	public static int[] getNumRowsAndCols() {
		ensureNumRowsAndColsPopulated();
		return new int[] {numRows, numCols};
	}
	
	public static NameMap[] getNameMaps() {
		return new NameMap[] { nameMapP1, nameMapP2 };
	}
	
}
