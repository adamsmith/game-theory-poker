/*
 * Created on Jun 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage2;

import _misc.*;
import stage2.Touple.*;
import java.util.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClustererStream {

	private int m_numClusters;
	private int m_dimensions;
	
	private int m_state;
	private int[][] m_clusters;
	private long m_error;
	
	private long[][] m_tmpClusterAccum;
	private int[] m_tmpClusterCount;

	private Set m_uniqueInitSamplesSet;
	private List m_uniqueInitSamplesList;
	private int[][] m_initSamples;
	private int m_initCounter;
	
	private final static int initSampleSizeMultiplier = 1000;

	private final static int state_created = 0;
	private final static int state_initializingClusters = 1;
	private final static int state_updating = 2;
	private final static int state_doneRound = 3;
	private final static int state_doneAll = 4;
	private final static int state_doneAllComputingError = 5;
	
	private static int[] sqrLookup = null;
	private final static int sizeSqrLookup = 100000; // 400kB's
	
	
	public ClustererStream(int numClusters, int dimensions) {
		m_numClusters = numClusters;
		m_dimensions = dimensions;

		m_tmpClusterAccum = new long[m_numClusters][m_dimensions];
		m_tmpClusterCount = new int[m_numClusters];
		m_clusters = new int[m_numClusters][m_dimensions];
		
		m_initSamples = new int[numInitSamples()][];

		m_state = state_created;
		
		ClustererStream.initSqrLookup();
	}
	
	public void addSample(int[] sample) {
		if(m_state == state_initializingClusters) {
			
			m_initSamples[m_initCounter++] = sample;
			IntTouple it = new IntTouple(sample);
			if(m_uniqueInitSamplesSet.add(it)) {
				// new element
				m_uniqueInitSamplesList.add(it);
			}
			if(m_initCounter == numInitSamples()) {
				initializeClusters();
			}
			
		} else if (m_state == state_updating) {
			
			Object[] pairing = determineClusterMembership(sample);
			int memberOf = ((Integer) pairing[0]).intValue();
			for(int i = 0; i < m_dimensions; i++) {
				m_tmpClusterAccum[memberOf][i] += sample[i];
			}
			m_tmpClusterCount[memberOf]++;

			m_error += ((Long) pairing[1]).longValue();
			
		} else if (m_state == state_doneAllComputingError) {

			Object[] pairing = determineClusterMembership(sample);
			m_error += ((Long) pairing[1]).longValue();
			
		} else {
			
			throw new RuntimeException();
			
		}
	}
	
	public boolean doneSampleRound() {
		if(m_state == state_initializingClusters) {
			initializeClusters();
		}
		if(m_state != state_updating) {
			throw new RuntimeException();
		}
		
		boolean done = !computeNewMeans();
		
		if(done) {
			m_state = state_doneAll;
		} else {
			m_state = state_doneRound;
		}
		
		return done;
	}
	
	public void beginSampleRound() {
		if(m_state != state_doneRound && m_state != state_created) {
			throw new RuntimeException();
		}
		
		initRound();
		if(m_state == state_created) {
			m_state = state_initializingClusters;
		} else if (m_state == state_doneRound) {
			m_state = state_updating;
		}
	}
	
	public long getError() {
		if(m_state != state_doneRound && m_state != state_doneAll) {
			throw new RuntimeException();
		}
		
		return m_error;
	}
	
	public void startErrorComputeRound() {
		if(m_state != state_doneAll) {
			throw new RuntimeException();
		}
		
		initRound();
		m_state = state_doneAllComputingError;
	}
	
	public void endErrorComputeRound() {
		if(m_state != state_doneAllComputingError) {
			throw new RuntimeException();
		}
		
		m_state = state_doneAll;
	}
	
	public byte getMembership(int[] sample) {
		if(m_state != state_doneAll) {
			throw new RuntimeException();
		}
		
		Object[] pairing = determineClusterMembership(sample);
		return ((Integer) pairing[0]).byteValue();
	}
	
	public void print() {
		if(m_state != state_doneAll) {
			throw new RuntimeException();
		}
		
		for(int i = 0; i < m_numClusters; i++) {
			String yo = "[ ";
			for(int j = 0; j < m_dimensions; j++) {
				yo += (new Integer(m_clusters[i][j])).toString() + " ";
			}
			System.out.println(yo + " ]");
		}
	}
	
	
	// ----UTILITY FUNCTIONS------------------------------------
	// ---------------------------------------------------------
	
	
	private boolean computeNewMeans() {
		boolean changed = false;
		double avg_delta = 0;
		for(int i = 0; i < m_numClusters; i++) {
			long delta = 0;

			int sampleToCopyFrom = (int) Math.floor(Math.random()*m_initSamples.length);

			if(m_tmpClusterCount[i] == 0) {
				System.out.println("    Warning: zero sized cluster");
			}
			
			for(int j = 0; j < m_dimensions; j++) {
				int newValue = 0;
				
				if(m_tmpClusterCount[i] != 0) {
					newValue = (int) Math.round((float)m_tmpClusterAccum[i][j] / m_tmpClusterCount[i]);
				} else {
					try {
						newValue = m_initSamples[sampleToCopyFrom][j];
					} catch (NullPointerException npe) {
						int count = 0;
						do {
							sampleToCopyFrom = (int) Math.floor(Math.random()*m_initSamples.length);
							count++;
							if(count == 50) {
								throw new RuntimeException();
							}
						} while (m_initSamples[sampleToCopyFrom] == null);
					}
				}
				
				if(!changed) {
					if(newValue != m_clusters[i][j]) {
						changed = true;
					}
				}
				
				delta += sqrLookup[Math.abs(newValue - m_clusters[i][j])];
				
				m_clusters[i][j] = newValue;
			}
			avg_delta += (delta / m_dimensions);
		}
		avg_delta /= m_numClusters;
//		System.out.println("avg_delta per dimension: " + avg_delta);
		
		return changed;
	}
	
	private void initRound() {
		m_tmpClusterAccum = new long[m_numClusters][m_dimensions];
		m_tmpClusterCount = new int[m_numClusters];
		m_uniqueInitSamplesSet = new HashSet();
		m_uniqueInitSamplesList = new ArrayList();
		
//		m_initSamples = new int[numInitSamples()][];
		m_initCounter = 0;
		m_error = 0;
	}
	
	private void initializeClusters() {	
		if(m_uniqueInitSamplesList.size() == 0) {
			throw new RuntimeException();
		}
		for(int i = 0; i < m_numClusters; i++) {
			int randomIndex = (int)Math.floor(m_uniqueInitSamplesList.size()*Math.random());
			m_clusters[i] = initSmoothClusterRandomly(
					(IntTouple) m_uniqueInitSamplesList.get(randomIndex));
			m_uniqueInitSamplesList.remove(randomIndex);
		}
		
		m_state = state_updating;
		
		for(int i = 0; i < m_initCounter; i++) {
			addSample(m_initSamples[i]);
		}
	}
	
	private boolean array1dEquals(int[] x, int[] y) {
		if(x.length != y.length) {
			return false;
		}
		for(int i = 0; i < x.length; i++) {
			if(x[i] != y[i]) {
				return false;
			}
		}
		return true;
	}
	
	private int[] initSmoothClusterRandomly(IntTouple it) {		
		int[] answer = new int[m_dimensions];
		for(int i = 0; i < m_dimensions; i++) {
			answer[i] = it.x[i];
		}
		return answer;
	}
	
	// first object in return: Integer giving memberOf
	// second object in return: Float giving error
	private Object[] determineClusterMembership(int[] sample) {
		int memberOf = 0;
		long minDist = curveDistance(sample, m_clusters[memberOf], Long.MAX_VALUE);
		long tmp;
		
		for(int i = 1; i < m_numClusters; i++) {
			tmp = curveDistance(sample, m_clusters[i], minDist);
			if(tmp < minDist) {
				minDist = tmp;
				memberOf = i;
			}
		}
		
		return new Object[] {new Integer(memberOf), new Long(minDist)};
	}
	
	private static long curveDistance(int x[], int y[], long abortIfGreaterThan) {
		long answer = 0;

//		for(int i = 0; i < x.length; i++) {
		for(int i = x.length; --i >= 0; ) {
			answer += sqrLookup[Math.abs(x[i] - y[i])];
			if(answer > abortIfGreaterThan) {
				return Long.MAX_VALUE;
			}
		}
		
		return answer;
	}
	
	private int numInitSamples() {
		return m_numClusters*initSampleSizeMultiplier;
	}
	
	private static void initSqrLookup() {
		if(sqrLookup == null) {
			sqrLookup = new int[sizeSqrLookup];
			for(int i = 0; i < sizeSqrLookup; i++) {
				sqrLookup[i] = Helper.sqr(i);
			}
		}
	}
}
