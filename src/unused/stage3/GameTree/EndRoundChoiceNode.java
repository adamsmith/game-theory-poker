/*
 * Created on Jun 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3.GameTree;

import java.util.HashSet;

import stage3.ContinueItem;
import stage3.InfoSet.InfoSetPair;
import stage3.InfoSet.InfoToken;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EndRoundChoiceNode extends Node implements ChoiceNode {

	public TerminalLeafNode gtnFold;
	public ContinueItem ciCall;
	
	public EndRoundChoiceNode(ContinueItem ciToFill, int infoType) {
		super(ciToFill, new HashSet());
		
		InfoSetPair ispThisNode = ciToFill.ispChild;
		
		// we now must create two outcomes corresponding to the player's 2 choices
		
		// Choice 1: Fold
		InfoSetPair ispFold = ispThisNode.addInfoToBoth(
				new InfoToken(infoType, InfoToken.s_fold));
		gtnFold = new TerminalLeafNode(new ContinueItem(ispFold, this));

		// Choice 2: Call
		ciCall = new ContinueItem(
				ispThisNode.addInfoToBoth(new InfoToken(infoType, InfoToken.s_call)),
				this);
	}
}
