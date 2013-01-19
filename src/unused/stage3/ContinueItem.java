/*
 * Created on Jun 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3;

import stage3.GameTree.*;
import stage3.GameTree.ChanceNode;
import stage3.InfoSet.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ContinueItem {

	public InfoSetPair ispChild;
	public Node gtnParent;
	
	public ContinueItem(InfoSetPair ispChild, Node gtnParent) {
		this.ispChild = ispChild;
		this.gtnParent = gtnParent;
	}
}
