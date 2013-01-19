/*
 * Created on Jun 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3.GameTree;

import stage3.*;
import java.util.*;

import stage3.InfoSet.InfoSet;
import stage3.InfoSet.InfoSetPair;
import stage3.InfoSet.InfoToken;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChanceNode extends Node {

	// note that the only chance nodes are for dealing cards (or,
	//   in the abstract game, jumping from a pair of cluster 
	//   assignments to the next)
	
	public Set ciNext;
	
	public ChanceNode(ContinueItem ciAppend, int numClusters) {
		super(ciAppend, new HashSet());
		
		ciNext = new HashSet();
		
		for(int i = 0; i < numClusters; i++) {
			for(int j = 0; j < numClusters; j++) {
				// players {1,2} are assigned to clusters {i,j}
				
				InfoToken iP1 = new InfoToken(InfoToken.s_holeCardsCluster
						[DoGT.s_bc0][DoGT.s_player1], i);
				
				InfoToken iP2 = new InfoToken(InfoToken.s_holeCardsCluster
						[DoGT.s_bc0][DoGT.s_player2], j);
				
				InfoSetPair ispNew = isp.addInfoToOnePlayer(iP1, DoGT.s_player1)
						.addInfoToOnePlayer(iP2, DoGT.s_player2);
				
				ciNext.add(new ContinueItem(ispNew, this));
			}
		}
	}
	
	
}
