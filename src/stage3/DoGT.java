/*
 * Created on Jun 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3;


import java.util.*;
import java.io.*;

import _misc.*;
import stage3.InfoSet.*;
import _io.*;
import _misc.Constants;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DoGT {
	
	public final static byte s_player1 = 0;  // SENSITIVE: USED AS ARRAY INDICES
	public final static byte s_player2 = 1;  // DO NOT TOUCH

	public final static byte s_bc0 = 0;  // SENSITIVE: USED AS ARRAY INDICES
	public final static byte s_bc3 = 3;  // DO NOT TOUCH
	public final static byte s_bc4 = 4;  // DO NOT TOUCH
	public final static byte s_bc5 = 5;  // DO NOT TOUCH

	public static double[][] startPDT;
	public static float[][][][] transition0to3;
	public static float[][][][] transition3to4;
	public static float[][][][] transition4to5;
	public static double[][][] terminalValues;
	public static int[] numClusters;
	
	public static long numLeafNodes = -1;
	public static long fiveThousandths = -1;
	public static double tTotal = Double.NaN;
	
	public final static String rootName = "root";
	public final static int rootNumBoardCards = 4;
	
	public static boolean writeToDisk = true;
	
	public static final String ROOT_INPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
		"stage2" + Constants.dirSep;
	
	public static final String ROOT_SUBTREES_INPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
		"stage3" + Constants.dirSep;
	
	public static final String ROOT_OUTPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
		"stage3" + Constants.dirSep;
	
	private static final int MAX_SIMULT_FILES_OPEN = 1;
	
	private static void doWork(String subtreeName, float initialPotP1, 
			float initialPotP2) throws Exception {
		System.out.println("BUILDING GAME TREE FOR SUBTREE " + subtreeName);
		System.out.println("-----------");
		
		if(!writeToDisk) {
			System.out.println("Warning: writing results to disk is disabled");
		}
		
		boolean isRoot = subtreeName.equals(rootName);
		
		int startNumBoardCards;
		int endNumBoardCards;
		if(isRoot) {
			startNumBoardCards = 0;
			endNumBoardCards = rootNumBoardCards;
		} else {
			startNumBoardCards = 3;
			endNumBoardCards = 5;
		}
		
		populateNumLeafNodes(endNumBoardCards, isRoot);
		
		String outDir = ROOT_OUTPUT_DIR + subtreeName + Constants.dirSep;
		
		// let's roll
		DoGT.tTotal = System.currentTimeMillis();
		GameState.initStaticState(startNumBoardCards, endNumBoardCards, 
				initialPotP1, initialPotP2);
		GameState.ensureInitRoots();
		if(writeToDisk) {
			String rmeFileName = outDir + "rewardMatrix";
			Helper.prepFilePath(rmeFileName);
			GameState.rewardMatrixOut = new WriteBinaryRmeStream(rmeFileName, 
					Helper.getBufferSize(MAX_SIMULT_FILES_OPEN), numClusters, endNumBoardCards);
		}
		GameState subtreeRoot = new GameState((byte)startNumBoardCards, 
				(byte)-1, (byte)-1, true, Float.NaN, 
				false, (byte)-1, (byte)-1, Float.NaN, Float.NaN, null);
		subtreeRoot.expand();
		
		if(writeToDisk) {
			GameState.rewardMatrixOut.close();
		}
		
		int[] numRowsAndCols = GameState.getNumRowsAndCols();

		System.out.println("done computation: " + (System.currentTimeMillis() - tTotal));
		System.out.println("number of p1 choices: " + numRowsAndCols[0]);
		
		if(writeToDisk) {
			System.out.println("writing rest of data to disk...");
			// write constraint matrices to disk
			double tStage = System.currentTimeMillis();
			ConstraintMatrix[] constraintMatrices = GameState.getConstraintMatrices();
			
			String constraintMatrixFileName = outDir + "constraints.p1.obj";
			Helper.prepFilePath(constraintMatrixFileName);
			WriteBinaryConstraintMatrix.writeConstraintMatrix(constraintMatrixFileName, 
					constraintMatrices[0], numClusters, endNumBoardCards,
					numRowsAndCols[0]);
			
			constraintMatrixFileName = outDir + "constraints.p2.obj";
			Helper.prepFilePath(constraintMatrixFileName);
			WriteBinaryConstraintMatrix.writeConstraintMatrix(constraintMatrixFileName, 
					constraintMatrices[1], numClusters, endNumBoardCards,
					numRowsAndCols[1]);
			
			System.out.println("done writing constraint matrices in time: " + 
					(System.currentTimeMillis() - tStage));
			
			// write name map to disk
			tStage = System.currentTimeMillis();
			NameMap[] nameMaps = GameState.getNameMaps();
			String nameMapFileName = outDir + "nameMap.p1.obj";
			Helper.prepFilePath(nameMapFileName);
			WriteBinaryNameMap.writeNameMap(nameMapFileName, nameMaps[0], 
					numClusters, endNumBoardCards);
			
			nameMapFileName = outDir + "nameMap.p2.obj";
			Helper.prepFilePath(nameMapFileName);
			WriteBinaryNameMap.writeNameMap(nameMapFileName, nameMaps[1], 
					numClusters, endNumBoardCards);
			
			System.out.println("done writing name maps in time: " + 
					(System.currentTimeMillis() - tStage));
		}

		System.out.println("total subtree time: " + (System.currentTimeMillis() - tTotal));
		System.out.println("");
		System.out.println("");
		System.out.println("");
		DoGT.tTotal = Double.NaN;
	}

	public static void main(String[] args) throws Exception {
		Object[] inData = LoadInputData.getTreeData(true, ROOT_INPUT_DIR);
		startPDT = (double[][]) inData[0];
		transition0to3 = (float[][][][]) inData[1];
		transition3to4 = (float[][][][]) inData[2];
		transition4to5 = (float[][][][]) inData[3];
		terminalValues = (double[][][]) inData[4];
		
		numClusters = new int[] {
				startPDT.length,				// 0 bc's
				-1,								// 1
				-1,								// 2
				transition3to4.length,			// 3 bc's
				transition4to5.length,			// 4 bc's
				terminalValues[0].length			// 5 bc's
		};
		
		String[] subtreesToWalk;
		if(args.length == 0) {
			System.out.println("specify trees to walk.  [all-subtrees | some space-delimited subset of {a-g, root}]");
			throw new RuntimeException();
		}
		if(args[0].equals("all-subtrees")) {
			subtreesToWalk = Constants.subtreeNames;
		} else {
			subtreesToWalk = args;
		}
		
		if(subtreesToWalk[0].equals(rootName)) {
			doWork(rootName, InfoToken.smallBlind, InfoToken.bigBlind);
		} else {
			double tTotal = System.currentTimeMillis();
			for(int ixSubtree = 0; ixSubtree < subtreesToWalk.length; ixSubtree++) {
				String subtreeName = subtreesToWalk[ixSubtree];
				String descFileName = ROOT_SUBTREES_INPUT_DIR + subtreeName
						+ Constants.dirSep + "subtreeGameDescription";
				Object[] in = ReadBinarySubtreeGame.getSubtreeGame(descFileName);
				float[] pots = (float[]) in[0];
				startPDT = (double[][]) in[1];
				
				doWork(subtreeName, pots[0], pots[1]);
			}
			System.out.println("Entire program done in time: " + (System.currentTimeMillis() - tTotal));
		}
	}
	
	private static void populateNumLeafNodes(int maxNumBoardCards, boolean isRoot) {
		// calculate numLeafNodes
		int tmpBc = maxNumBoardCards;
		numLeafNodes = InfoToken.numLeafNodes[tmpBc] +
				InfoToken.numBrContinuations[tmpBc];
		while(true) {
			numLeafNodes *= Helper.sqr(numClusters[tmpBc]);
			
			tmpBc = Helper.getPreviousBoardCardCount(tmpBc, true);
			if((isRoot && tmpBc == Byte.MIN_VALUE) || (!isRoot && tmpBc == 0)) {
				break;
			}
			
			numLeafNodes *= InfoToken.numBrContinuations[tmpBc];
			numLeafNodes += InfoToken.numLeafNodes[tmpBc];
		}
		fiveThousandths = (int) Math.floor(numLeafNodes / 200);
	}
}
