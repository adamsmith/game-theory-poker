/*
 * Created on Jun 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3;

import _misc.*;
import _io.*;
import java.io.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LoadInputData {
	
	public static Object[] getTreeData(boolean loadExtendedTerminalValues,
			String rootInputDir) throws IOException {
		
		double[][] startPDTTriangularNormalized = 
				ReadBinaryStartClusterPDT.getStartPDT(
				rootInputDir + "start_pdt_0",
				Helper.getBufferSize(1));
		double[][] startPDTFullNormalized = normalizeTriangular(
				startPDTTriangularNormalized);
		
		long[][][][] transition0to3 = ReadBinaryTransitionPDT.readPDT(
				rootInputDir + "transitions_0",
				Helper.getBufferSize(1));
		float[][][][] nTransition0to3 = normalize(transition0to3);
		
		long[][][][] transition3to4 = ReadBinaryTransitionPDT.readPDT(
				rootInputDir + "transitions_3",
				Helper.getBufferSize(1));
		float[][][][] nTransition3to4 = normalize(transition3to4);
		
		long[][][][] transition4to5 = ReadBinaryTransitionPDT.readPDT(
				rootInputDir + "transitions_4",
				Helper.getBufferSize(1));
		float[][][][] nTransition4to5 = normalize(transition4to5);
		
		double[][][] terminalValues = new double[6][][];
		terminalValues[5] = ReadBinaryTerminalClusterValues.getTerminalValues(
				rootInputDir + "terminal_values_5", Helper.getBufferSize(1));
		if(loadExtendedTerminalValues) {
			terminalValues[4] = ReadBinaryTerminalClusterValues.getTerminalValues(
					rootInputDir + "terminal_values_4", Helper.getBufferSize(1));
			terminalValues[3] = ReadBinaryTerminalClusterValues.getTerminalValues(
					rootInputDir + "terminal_values_3", Helper.getBufferSize(1));
			terminalValues[0] = ReadBinaryTerminalClusterValues.getTerminalValues(
					rootInputDir + "terminal_values_0", Helper.getBufferSize(1));
		}
		
		return new Object[] {startPDTFullNormalized, nTransition0to3, nTransition3to4, nTransition4to5,
				terminalValues};
	}
	
	private static float[][][][] normalize(long[][][][] x) {
		int sizeDim1 = x.length;
		int sizeDim2 = x[0][0].length;
		float[][][][] y = new float[sizeDim1][sizeDim1][sizeDim2][sizeDim2];
		
		for(int i = 0; i < sizeDim1; i++) {
			for(int j = 0; j < sizeDim1; j++) {
				long sum = 0;
				for(int k = 0; k < sizeDim2; k++) {
					for(int l = 0; l < sizeDim2; l++) {
						sum += x[i][j][k][l];
					}
				}
				for(int k = 0; k < sizeDim2; k++) {
					for(int l = 0; l < sizeDim2; l++) {
						y[i][j][k][l] = ((float) x[i][j][k][l] / sum);
					}
				}
			}
		}
		return y;
	}
	
	private static double[][] normalizeTriangular(double[][] x) {
		if(x.length != x[0].length) {
			throw new RuntimeException();
		}

		// pass 1 - sum totals
		double sum = 0;
		for(int i = 0; i < x.length; i++) {
			for(int j = i; j < x.length; j++) {
				if(j == i) {
					sum += x[i][j];
				} else {
					sum += 2*x[i][j];
				}
			}
		}
		
		// pass 2 - make full (not triangular) and normalized
		double[][] y = new double[x.length][x.length];
		for(int i = 0; i < x.length; i++) {
			for(int j = 0; j < x.length; j++) {
				y[i][j] = x[Math.min(i, j)][Math.max(i, j)] / sum;
			}
		}
		
		return y;
	}
}
