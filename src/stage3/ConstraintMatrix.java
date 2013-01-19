/*
 * Created on Jun 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3;

import java.util.*;
import java.io.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ConstraintMatrix implements Serializable {

	// used for column oriented view
	private int idCounter = -1;
	private Map columns;
	private int rootRowIndexColumnView = -1;
	
	// used for row oriented view
	private List rows;
	private int rootRowIndexRowView = -1;
	
	// used for both views
	private Set rowSet;
	public boolean rootInitialized = false;
	
	public ConstraintMatrix() {
		rowSet = new HashSet();
		
		// column view
		columns = new HashMap();
		idCounter = 0;
		
		//row view
		rows = new ArrayList();
	}
	
	private ConstraintMatrixRow addIfNew(int parentName, int[] childNames) {
		ConstraintMatrixRow newRow = new ConstraintMatrixRow();
		newRow.parentName = parentName;
		newRow.addChildren(childNames);

		if(rowSet.add(newRow)) {
			return newRow;
		} else {
			return null;
		}
	}
	
	public void addConstraint(int parentName, int[] childNames) {
		ConstraintMatrixRow cmrAddIfNew = addIfNew(parentName, childNames);
		
		addColumnConstraint(new Integer(parentName), childNames, cmrAddIfNew);
		addRowConstraint(parentName, childNames, cmrAddIfNew);
	}
	
	private void addRowConstraint(int parentName, int[] childNames, 
			ConstraintMatrixRow newRow) {

		if(parentName == -1) {
			// root!
			if(newRow == null) {
				throw new RuntimeException();
			}
			rootRowIndexRowView = rows.size();
		}
		
		if(newRow != null) {
			rows.add(newRow);
		}
	}
	
	private void addColumnConstraint(Integer parentName, int[] childNames, 
			ConstraintMatrixRow newRow) {
		
		if(newRow == null) {
			return;
		}
		
		int thisRowId = idCounter++;
		ConstraintMatrixColumn columnAlready;
		
		// handle parent
		if(parentName.intValue() != -1) {
			// this isn't the root node constraint
			columnAlready = (ConstraintMatrixColumn) columns.get(parentName);
			if(columnAlready == null) {
				columnAlready = new ConstraintMatrixColumn();
				columnAlready.addRowIdsParentOf(thisRowId);
				columns.put(parentName, columnAlready);
			} else {
				columnAlready.addRowIdsParentOf(thisRowId);
			}
		} else {
			// root!
			if(rootRowIndexColumnView >= 0) {
				throw new RuntimeException();
			}
			rootRowIndexColumnView = thisRowId;
		}
		
		// handle children
		for(int i = 0; i < childNames.length; i++) {
			Integer objChildName = new Integer(childNames[i]);
			columnAlready = (ConstraintMatrixColumn) columns.get(objChildName);
			if(columnAlready == null) {
				columnAlready = new ConstraintMatrixColumn();
				columnAlready.rowIdChildOf = thisRowId;
				columns.put(objChildName, columnAlready);
			} else {
				if(columnAlready.rowIdChildOf != -1) {
					throw new RuntimeException();
				}
				columnAlready.rowIdChildOf = thisRowId;
			}
		}
	}
	
	public ConstraintMatrixColumn getColumn(Integer name) {
		return (ConstraintMatrixColumn) columns.get(name);
	}
	
	public ConstraintMatrixRow getRow(int rowNumber) {
		return (ConstraintMatrixRow) rows.get(rowNumber);
	}
	
	public int getNumConstraints() {
		if(idCounter != rows.size()) {
			// both views should have the same num of constraints
			throw new RuntimeException();
		}
		return idCounter;
	}
	
	public int getRootRowIndex() {
		if(rootRowIndexRowView != rootRowIndexColumnView) {
			// root is added at same time for both, should have
			// same index
			
			throw new RuntimeException();
		}
		
		if(rootRowIndexRowView < 0) {
			// cover -1 case
			throw new RuntimeException(); 
		}
		
		return rootRowIndexRowView;
	}
	
	public void printColumnMatrix() {
		for(int i = 0; i < columns.size(); i++) {
			ConstraintMatrixColumn entry = (ConstraintMatrixColumn) columns.get(new Integer(i));
			System.out.println("Col " + i + ": " + entry.toString());
		}
	}
}
