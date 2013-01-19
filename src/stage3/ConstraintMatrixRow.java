/*
 * Created on Jul 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3;

import java.util.*;
import _misc.*;
import java.io.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ConstraintMatrixRow implements Serializable {

	// a row (corresponding to a family, including
	//   a parent and any nonzero number of children)

	public int parentName = -1;
	public int[] childNames = new int[4];
	public int numChildren = 0;
	
	private final static int arraySizeIncrements = 10;
	
	private int hashCodeCache;
	private boolean hashCodeCached = false;
	
	public void addChildren(int[] x) {
		if(childNames.length - numChildren < x.length) {
			int[] newChildNames = new int[Math.max(childNames.length+arraySizeIncrements,
					childNames.length+x.length)];
			System.arraycopy(childNames, 0, newChildNames, 0, numChildren);
			childNames = newChildNames;
		}
		System.arraycopy(x, 0, childNames, numChildren, x.length);
		numChildren += x.length;
	}
	
	public boolean equals(Object obj) {
		ConstraintMatrixRow cmr = (ConstraintMatrixRow) obj; // assume this
		if(parentName != cmr.parentName) {
			return false;
		}
		if(childNames.length != cmr.childNames.length) {
			return false;
		}
		for(int i = childNames.length-1; i >= 0 ; i--) {
			if(cmr.childNames[i] != childNames[i]) {
				return false;
			}
		}
		return true;
	}
	
	public int hashCode() {
		if(!hashCodeCached) {
			hashCodeCache = Helper.hashIntArray(childNames);
			hashCodeCache += parentName;
			
			hashCodeCached = true;
		}
		
		return hashCodeCache;
	}
}
