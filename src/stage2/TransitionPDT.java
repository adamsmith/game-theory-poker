/*
 * Created on Jun 6, 2005
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
public class TransitionPDT {
	
	private float[][] pdt;
	private int numClustersBc1;
	private int numClustersBc2;
	
	public TransitionPDT(int numClustersBc1, int numClustersBc2) {
		// two players

		this.numClustersBc1 = numClustersBc1;
		this.numClustersBc2 = numClustersBc2;
		
		pdt = new float[sqr(numClustersBc1)][sqr(numClustersBc2)];
	}
	
	private int sqr(int x) {
		return x*x;
	}
}
