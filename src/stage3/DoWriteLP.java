/*
 * Created on Jul 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3;

import _misc.Constants;
import _misc.Helper;

import java.io.*;

import _io.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DoWriteLP {

	public static double[][] startPDT;
	public static float[][][][] transition0to3;
	public static float[][][][] transition3to4;
	public static float[][][][] transition4to5;
	public static float[][][][][] transition;
	public static double[][][] terminalValues;
	public static int[] numClusters;

	private static int maxNumBoardCards;
	
	// LP constants
	private final static String rowObjective = " N";
	private final static String rowEqual = " E";
	private final static String rowGreater = " G";
	private final static String rewardConstraintPrefix = "r";
	private final static String planConstraintPrefix = "p";
	private final static String xPrefix = "x";
	private final static String yPrefix = "y";
	private final static String zPrefix = "z";
	private final static String rhsColumnName = "mRHS";
	
	// file access constants
	
	public static final String ROOT_INPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
			"stage2" + Constants.dirSep;
	
	public static final String ROOT_GAME_INPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
			"stage3" + Constants.dirSep;
	
	public static final String ROOT_OUTPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
			"stage3" + Constants.dirSep;
	
	private static final int MAX_SIMULT_FILES_OPEN = 2;

	public static void main(String[] args) throws Exception {
		double tTotal = System.currentTimeMillis();
		double tStage = System.currentTimeMillis();
		Object[] inData = LoadInputData.getTreeData(true, ROOT_INPUT_DIR);
		startPDT = (double[][]) inData[0];
//		transition0to3 = (float[][][][]) inData[1];
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
		
		int n;
		int m;

		String[] subtreesToWrite;
		if(args.length == 0) {
			System.out.println("specify trees to preprocess.  [all-subtrees | some space-delimited subset of {a-g, root}]");
			throw new RuntimeException();
		}
		if(args[0].equals("all-subtrees")) {
			subtreesToWrite = Constants.subtreeNames;
		} else {
			subtreesToWrite = args;
		}
		
		for(int ixSubtree = 0; ixSubtree < subtreesToWrite.length; ixSubtree++) {
			System.out.println("WRITING PROBLEM FOR SUBTREE " + subtreesToWrite[ixSubtree]);
			
			String inDir = ROOT_GAME_INPUT_DIR + subtreesToWrite[ixSubtree]
					+ Constants.dirSep;
			String outDir = ROOT_OUTPUT_DIR + subtreesToWrite[ixSubtree]
					+ Constants.dirSep;
			double tSubtree = System.currentTimeMillis();
			
			// load up constraint matrices
			String inFile = inDir + "constraints.p1.obj";
			Object[] cmInfo = ReadBinaryConstraintMatrix.getCm(inFile);
			maxNumBoardCards = ((Integer) cmInfo[1]).intValue();
			n = ((Integer) cmInfo[2]).intValue();
			ConstraintMatrix cmP1 = (ConstraintMatrix) cmInfo[0];
			
			inFile = inDir + "constraints.p2.obj";
			cmInfo = ReadBinaryConstraintMatrix.getCm(inFile);
			m = ((Integer) cmInfo[2]).intValue();
			ConstraintMatrix cmP2 = (ConstraintMatrix) cmInfo[0];
			
			// prep reward matrix streams
			inFile = inDir + "rewardMatrix.preprocessed.p1";
			ReadBinaryRmStream rmStreamP1 = new ReadBinaryRmStream(inFile, 
					Helper.getBufferSize(MAX_SIMULT_FILES_OPEN), false);
			
			inFile = inDir + "rewardMatrix.preprocessed.p2";
			// negate the sign for P2 (boolean arg)
			ReadBinaryRmStream rmStreamP2 = new ReadBinaryRmStream(inFile, 
					Helper.getBufferSize(MAX_SIMULT_FILES_OPEN), false);
			

			// get started
			String outFile;
			System.out.println("Files loaded in time: " + (System.currentTimeMillis() - tStage));
			System.out.println("");
			System.out.println("Writing LP file for P1 problem...");
			tStage = System.currentTimeMillis();

			outFile = outDir + "game.p1.mps";
			writeLP(rmStreamP1, new ConstraintMatrix[] {cmP1, cmP2}, n, m, 
					outFile, xPrefix);
			rmStreamP1.close();
			
			System.out.println("Done writing LP file in time: " + (System.currentTimeMillis() - tStage));
			System.out.println("");
			System.out.println("Writing LP file for P2 problem...");
			tStage = System.currentTimeMillis();
			
			outFile = outDir + "game.p2.mps";
			// note that numCols and numRows are switched!
			writeLP(rmStreamP2, new ConstraintMatrix[] {cmP2, cmP1}, m, n, 
					outFile, yPrefix);
			rmStreamP2.close();

			System.out.println("Done writing LP file in time: " + (System.currentTimeMillis() - tStage));
			System.out.println("");
			System.out.println("Done subtree in time: " + (System.currentTimeMillis() - tSubtree));
			System.out.println("");
			System.out.println("");
		}
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("Done program in time: " + (System.currentTimeMillis() - tTotal));
	}
	
	private static void writeLP(ReadBinaryRmStream rmIn, 
			ConstraintMatrix[] constraintMatrices, int numPriDim, int numSecDim, 
			String fileName, String varSolveForPrefix) throws IOException {
		
		// numRows and numCol refers to rewardMatrix
		System.out.println("  numRows = " + numPriDim);
		System.out.println("  numCols = " + numSecDim);
		
		
		ConstraintMatrix constraintPri = constraintMatrices[0];
		ConstraintMatrix constraintSec = constraintMatrices[1];
		int[] numConstraints = new int[] { constraintPri.getNumConstraints(),
				constraintSec.getNumConstraints() };
		
		Helper.prepFilePath(fileName);
		WriteMPS out = new WriteMPS(fileName, Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
		
		out.print(new String[] {"NAME", null, "POKER"});

		// ROWS section
		out.print("ROWS");
		out.print(new String[] {rowObjective, "OBJ"});
		for(int i = 0; i < numSecDim; i++) {
			String intName = Integer.toString(i);
			String rewardConstraintName = rewardConstraintPrefix + intName;
			String[] toWrite = new String[] {rowGreater, rewardConstraintName};
			out.print(toWrite);
		}
		for(int i = 0; i < numConstraints[0]; i++) {
			out.print(new String[] {rowEqual, 
					planConstraintPrefix.concat(new Integer(i).toString())});
		}
		
		
		// COLUMNS section
		out.print("COLUMNS");
		// one set of entries for each (x|y)[0..numPriDim-1]
		int rewardMatrixPointer = 0;
		RewardMatrixElement element;
		RewardMatrixElement rme = rmIn.getRme();
		for(int i = 0; i < numPriDim; i++) { 
			Integer iInt = new Integer(i);
			String columnName = null;
			// rme is null if we're at the end of the file.  This condition can hold
			// even when there are columns left if the touple had value zero and was
			// pruned from the RM file.
			if(rme != null) {
				int iColumnName = rme.secondDim;
				columnName = varSolveForPrefix.concat(Integer.toString(iColumnName));
			}
			
			// retrieve A[i][*] (row vector)
			// (rewardMatrix is a flattened representation of A[][], sorted on row index)
			while((rme != null) && (rme.secondDim==i)) {
				
				out.addElement(columnName, 
						rewardConstraintPrefix.concat(Integer.toString(rme.firstDim)), rme.value);
				
				rme = rmIn.getRme();
			}
			
			// retrieve E_x[*][i] (column vector)
			ConstraintMatrixColumn col = constraintPri.getColumn(iInt);
			columnName = varSolveForPrefix.concat(iInt.toString());
			for(int j = 0; j < col.numRowIdsParentOf; j++) {
				int rowId = col.rowIdsParentOf[j];
				out.addElement(columnName, 
						planConstraintPrefix.concat(Integer.toString(rowId)), -1);
			}
			if(col.rowIdChildOf != -1) {
				out.addElement(columnName, 
						planConstraintPrefix.concat(Integer.toString(col.rowIdChildOf)), 1);
			}
		}

		// one set of rows for each z[0..numConstraints[1]-1]
		int rootZIndex = constraintSec.getRootRowIndex();
		for(int i = 0; i < numConstraints[1]; i++) {
			// retrieve E_y[i][*] (row vector)
			String columnName = zPrefix.concat(new Integer(i).toString());

			ConstraintMatrixRow row = constraintSec.getRow(i);
			
			if(row.parentName != -1) {
				// this isn't the row making empty sequence == 1
				out.addElement(columnName, 
						rewardConstraintPrefix.concat(Integer.toString(row.parentName)), -1);
			}
			for(int j = 0; j < row.numChildren; j++) {
				out.addElement(columnName, 
						rewardConstraintPrefix.concat(Integer.toString(row.childNames[j])), 1);
			}
			
			if(i == rootZIndex) {
				out.addElement(columnName, "OBJ", 1);
			}
		}
		
		out.flushAddElement();
		

		// add RHS
		out.print("RHS");
		out.addElement(rhsColumnName, planConstraintPrefix.concat(
				new Integer(constraintPri.getRootRowIndex()).toString()), 1);
		out.flushAddElement();
		

		// add BOUNDS
		out.print("BOUNDS");
		for(int i = 0; i < numConstraints[1]; i++) {
			String columnName = zPrefix.concat(new Integer(i).toString());

			out.print(new String[] {" FR", "BND1", columnName});
		}

		
		// end
		out.print("ENDATA");
		
		out.close();
		
	}
}
