/*
 * Created on Jun 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3.GameTree;

import java.util.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RootNode extends Node {

	
	private static RootNode gtnRoot = null;
	
	public static RootNode getRoot() {
		if(gtnRoot == null) {
			gtnRoot = new RootNode(new HashSet());
		}
		return gtnRoot;
	}
	
	private RootNode(Set children) {
		super(children);
	}
}
