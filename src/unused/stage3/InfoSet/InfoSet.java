/*
 * Created on Jun 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3.InfoSet;

import java.util.*;

import stage3.GameTree.Node;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InfoSet {		
	
	// represents the info set FOR ONE PLAYER
	// there are two infoset trees -- one for each player

	// info set tree data
	private InfoSet parent;  // null if this is a root node
	private Set children; // a set of InfoSets

	// the real meat and potatoes
	private InfoToken info; // each child can only contain ONE more InfoToken
	
	protected InfoSet addInfo(InfoToken info) {
		InfoSet aChild;
		for(Iterator i = children.iterator(); i.hasNext(); ) {
			aChild = (InfoSet) i.next();
			if(aChild.info.equals(info)) {
				return aChild;
			}
		}
		
		// this is a ginuinely new InfoSet
		InfoSet isnNew = new InfoSet(this);
		isnNew.info = info;
		
		// this's info is expressed by inheritance
		return isnNew;
	}
	
	protected InfoSet(InfoSet parent) {
		this.parent = parent;
		children = new HashSet();
		
//		treeNodes = new HashSet();
	}
	
	public boolean equals(Object obj) {
		throw new RuntimeException();
	}
}
