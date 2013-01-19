/*
 * Created on Jun 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RewardMatrixElement implements Comparable {
	
	public final static int RECORD_SIZE = 4 * 2 + 4 * 1;

	public int secondDim;
	public float value;
	public int firstDim;
	
	public static boolean compareOnFirstDim = true;
	
	public RewardMatrixElement(int rowAsP1, int columnAsP2, float value) {
		this.firstDim = rowAsP1;
		this.secondDim = columnAsP2;
		this.value = value;
	}
	
	public String toString() {
		return "[" + firstDim + ", " + secondDim + ", " + value + "]";
	}
	
	public int compareTo(Object obj) {
		// assume obj instanceof RewardMatrixElement
		RewardMatrixElement toCompare = (RewardMatrixElement) obj;

		if(compareOnFirstDim) {
			if(firstDim > toCompare.firstDim) {
				return 1;
			} else if (firstDim < toCompare.firstDim) {
				return -1;
			}
			
			if(secondDim > toCompare.secondDim) {
				return 1;
			} else if (secondDim < toCompare.secondDim) {
				return -1;
			}
			
			return 0;
		} else {
			if(secondDim > toCompare.secondDim) {
				return 1;
			} else if (secondDim < toCompare.secondDim) {
				return -1;
			}
			
			if(firstDim > toCompare.firstDim) {
				return 1;
			} else if (firstDim < toCompare.firstDim) {
				return -1;
			}
			
			return 0;
		}
	}
	
	public boolean equals(RewardMatrixElement rme) {
		if(firstDim != rme.firstDim) {
			return false;
		}
		if(secondDim != rme.secondDim) {
			return false;
		}
		if(value != rme.value) {
			throw new RuntimeException("inconsistency");
		}
		return true;
	}
	
	public boolean equals(Object obj) {
		return equals((RewardMatrixElement) obj);  // assume...
	}
}
