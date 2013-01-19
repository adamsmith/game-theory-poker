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
import java.util.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChanceRound {

	public static Set chanceRound(Set ciSet, int numClusters) {

		Set newIspLeaves = new HashSet();
		
		for(Iterator i = ciSet.iterator(); i.hasNext(); ) {
			
			ContinueItem ciAppend = (ContinueItem) i.next();
			ChanceNode cn = new ChanceNode(ciAppend, numClusters);
			newIspLeaves.addAll(cn.ciNext);
			
		}
		
		return newIspLeaves;
	}
}
