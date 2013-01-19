/*
 * Created on Jun 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3.GameTree;

import java.util.*;

import stage3.InfoSet.*;
import stage3.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class Node {			
	
	// IMMUTABLE (?)
	// represents a node in the game tree
	

	// tree information
	protected Node parent;  // null if this is the root
	protected Set children;  // a set of Nodes
	
	// corresponding infoset information
	//   (the global information set is the union of iP1 and iP2)
	protected InfoSetPair isp;

	
	protected Node(ContinueItem ciAppend, Set children) {
		this.parent = ciAppend.gtnParent;
		this.children = children;
		this.isp = ciAppend.ispChild;
		isp.addGtnToBoth(this);
		parent.children.add(this);
	}

	// root node constructor
	protected Node(Set children) {
		this.children = children;
	}

}
