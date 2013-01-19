/*
 * Created on Jul 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ConstraintMatrixColumn implements Serializable {
	
	// this is interesting because this can be used to represent
	// a column from the constraint matrix (corresponding to a 
	// node in the info set tree, which can have a parent and/or
	// children), or 
	

	public int[] rowIdsParentOf = new int[4];
	public int numRowIdsParentOf = 0;
	
	public int rowIdChildOf = -1;
	
	private final static int arraySizeIncrements = 5;
	
	public void addRowIdsParentOf(int x) {
		if(rowIdsParentOf.length == numRowIdsParentOf) {
			int[] newChildNames = new int[rowIdsParentOf.length+arraySizeIncrements];
			System.arraycopy(rowIdsParentOf, 0, newChildNames, 0, numRowIdsParentOf);
			rowIdsParentOf = newChildNames;
		}
		rowIdsParentOf[numRowIdsParentOf++] = x;
	}
}
