/*
 * Created on May 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage2;


/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestHandClustering {
	
	private static final int numBoardCards = 4;

	private static final int POINTS_PER_CURVE = 50;
	private static final int AVG_COUNTS_PER_CURVE = 1000;
	private static final int NUM_SAMPLES = 1000;
	
	public static void main(String[] args) {
		// initialize pre-computed array
		DoClustering.initWeightArray(numBoardCards);

		// init our sample pool (random)
		int[][][] curves = new int[NUM_SAMPLES][POINTS_PER_CURVE][2];
		for(int i = 0; i < NUM_SAMPLES; i++) {
			for(int j = 0; j < POINTS_PER_CURVE; j++) {
				curves[i][j][0] = (int) Math.floor(Math.random()*DoClustering.MAX_IDoClusteringStep2[numBoardCards]); // random position
				curves[i][j][1] = (int) Math.floor(Math.random()*AVG_COUNTS_PER_CURVE*2);
			}
		}
		int[][] smoothCurves = new int[NUM_SAMPLES][];
		for(int i = 0; i < NUM_SAMPLES; i++) {
			smoothCurves[i] = DoClustering.smooth(curves[i], DoClustering.MAX_IDoClusteringStep2[numBoardCards]);
		}
		
		int loopsToRun = 1;
		
		double times[] = new double[loopsToRun];
		for(int tries = 0; tries < loopsToRun; tries++) {
			double timer1 = System.currentTimeMillis();
			ClustererStream clusterer = new ClustererStream((byte) DoClustering.NUM_CLUSTERS[numBoardCards], DoClustering.MAX_IDoClusteringStep2[numBoardCards]+1);
			while(true) {
				clusterer.beginSampleRound();
				for(int i = 0; i < NUM_SAMPLES; i++) {
					clusterer.addSample(smoothCurves[i]);
				}
				
				boolean done = clusterer.doneSampleRound();
				System.out.println(clusterer.getError());
				
				if(done) {
					break;
				}
			}
			
			times[tries] = System.currentTimeMillis() - timer1;
			System.out.println(times[tries]);
		}
		float timeAvg = 0;
		for(int i = 0; i < loopsToRun; i++) {
			timeAvg += times[i];
		}
		timeAvg /= loopsToRun;
		System.out.println(timeAvg);
	}
	
	private static void printMATLAB(float samples[][], float clusters[][], int memberOfCluster[]) {
		// print session to MATLAB
		String matlab = "clear all; samples = zeros(" + samples.length + "," + samples[0].length + "); " +
		"clusters = zeros(" + clusters.length + "," + clusters[0].length + "); " +
		"memberOfCluster = zeros(" + samples.length + ",1); ";

		for(int i = 0; i < samples.length; i++) {
			matlab += "samples(" + (i+1) + ",:)=[";
			for(int j = 0; j < samples[i].length; j++) {
				matlab += samples[i][j] + ",";
			}
			matlab += "]; ";
		}
		for(int i = 0; i < clusters.length; i++) {
			matlab += "clusters(" + (i+1) + ",:)=[";
			for(int j = 0; j < clusters[i].length; j++) {
				matlab += clusters[i][j] + ",";
			}
			matlab += "]; ";
		}
		for(int i = 0; i < samples.length; i++) {
			matlab += "memberOfCluster(" + (i+1) + ")=" + (memberOfCluster[i]+1) + "; ";
		}
		
		System.out.println(matlab);
	}
}
