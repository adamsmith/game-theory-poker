/*
 * Created on Jun 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage2;

import _game.Card;
import _io.beans.*;
import _io.ReadBinaryClusterIdStream;
import _io.*;
import _misc.Constants;
import java.io.*;
import _misc.Helper;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DoStartClusterPDT {
	
	private static final String ROOT_INPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
	"stage2" + Constants.dirSep;
	
	private static final String ROOT_OUTPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
	"stage2" + Constants.dirSep;
	
	private static final int MAX_SIMULT_FILES_OPEN = Constants.choose(Card.NUM_CARDS, 2);

	
	public static StartClusterBean getStartClusterInfo(String inputDir) throws IOException {
		int numHoleCards = Constants.choose(Card.NUM_CARDS, 2);

		// ------------------------------------------------------------
		// load up all input files
		// ------------------------------------------------------------
		ReadBinaryClusterIdStream[] in = 
			new ReadBinaryClusterIdStream[numHoleCards];
		
		int inPointer = 0;
		for(int i = 0; i < (Card.NUM_CARDS-1); i++) {
			for(int j = i+1; j < Card.NUM_CARDS; j++) {
				String path = inputDir + new Integer(i).toString() + "_" + new Integer(j).toString();
				in[inPointer++] = new ReadBinaryClusterIdStream(
						path, 
						0, 
						Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));
			}
		}
		if(inPointer != numHoleCards) {
			throw new RuntimeException();
		}
		
		// ------------------------------------------------------------
		// do it
		// ------------------------------------------------------------
		byte[][] holeCards = new byte[numHoleCards][];
		int holeCardsPointer = 0;
		for(byte i = 0; i < (Card.NUM_CARDS-1); i++) {
			for(byte j = (byte)(i+1); j < Card.NUM_CARDS; j++) {
				holeCards[holeCardsPointer++] = new byte[] {i, j};
			}
		}

		int numClusters = in[0].getNumClusters();
		int[] clusterIds = new int[numHoleCards];
		
		for(int i = 0; i < numHoleCards; i++) {
			clusterIds[i] = in[i].readRecord().clusterId;
			in[i].close(); // there's only one initial cluster
		}
		
		StartClusterBean out = new StartClusterBean();
		out.clusterIds = clusterIds;
		out.holeCards = holeCards;
		out.numClusters = numClusters;
		out.numHoleCards = numHoleCards;
		
		return out;
	}
	
	public static void main(String[] args) throws IOException {
		double tTotal = System.currentTimeMillis();

		StartClusterBean info = getStartClusterInfo(ROOT_INPUT_DIR 
				+ "clustering_step2_0" + Constants.dirSep);
		int numClusters = info.numClusters;
		int numHoleCards = info.numHoleCards;
		byte[][] holeCards = info.holeCards;
		int[] clusterIds = info.clusterIds;
		
		int[][] initialCounts = new int[numClusters][numClusters];
		int sum = 0;
		for(int i = 0; i < (numHoleCards-1); i++) {
			for(int j = i+1; j < numHoleCards; j++) {
				// {i,j} and {k,l} are the hole cards
				if(holeCards[i][0] == holeCards[j][0] || 
						holeCards[i][0] == holeCards[j][1] || 
						holeCards[i][1] == holeCards[j][0] || 
						holeCards[i][1] == holeCards[j][1]) {
					continue;
				}
				
				int clusterId1 = clusterIds[i];
				int clusterId2 = clusterIds[j];
				if(clusterId1 == -1 || clusterId2 == -1) {
					throw new RuntimeException();
				}
				
				initialCounts
						[Math.min(clusterId1, clusterId2)]
						[Math.max(clusterId1, clusterId2)]++;
				sum++;
			}
		}
		System.out.println(sum);
		
		double[][] normalizedCounts = new double[numClusters][numClusters];
		for(int i = 0; i < numClusters; i++) {
			for(int j = 0; j < numClusters; j++) {
				normalizedCounts[i][j] = ((double) initialCounts[i][j] / sum);
				System.out.println("[" + i + "," + j + "] = " + normalizedCounts[i][j]);
			}
		}
		
		// for first hole cards deal, p(i), where i is the cluster Id, is equal to
		//   sum(normalizedCounts[i][x], x, i, numClusters) + sum(normalizedCounts[x][i], x, 0, i-1)
		
		// for second hole card deal -- given i -- p(j), the prob dist over cluster id j's, is:
		//   normalizedCounts[i][j], i <= j
		//   normalizedCounts[j][i], otherwise
		
		
		// output to file
		WriteBinaryStartClusterPDT.writeStartPDT(
				ROOT_OUTPUT_DIR + "start_pdt_0",
				normalizedCounts, numClusters, Helper.getBufferSize(MAX_SIMULT_FILES_OPEN));

		System.out.println("All start values computed+recorded in time: " + (System.currentTimeMillis() - tTotal));
		
		double[] firstHoleCardPD = new double[numClusters];
		for(int i = 0; i < numClusters; i++) {
			double x = 0;
			for(int j = 0; j < i; j++) {
				x += normalizedCounts[j][i];
			}
			for(int j = i; j < numClusters; j++) {
				x += normalizedCounts[i][j];
			}
			firstHoleCardPD[i] = x;
			System.out.println("[" + i + "] = " + firstHoleCardPD[i]);
		}	
	}
}
