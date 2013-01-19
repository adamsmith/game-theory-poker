/*
 * Created on Jun 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3.GameTree;

import stage3.InfoSet.*;
import stage3.*;

import java.util.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BeginRoundChoiceNode extends Node implements ChoiceNode {

	public TerminalLeafNode gtnFold;
	public ContinueItem ciRaise;
	public ContinueItem ciCheck;
	
	public BeginRoundChoiceNode(ContinueItem ciToFill, int infoType) {
		super(ciToFill, new HashSet());
		
		InfoSetPair ispThisNode = ciToFill.ispChild;
		
		// we now must create three outcomes corresponding to the player's 3 choices
		
		// Choice 1: Fold
		InfoSetPair ispFold = ispThisNode.addInfoToBoth(
				new InfoToken(infoType, InfoToken.s_fold));
		gtnFold = new TerminalLeafNode(new ContinueItem(ispFold, this));

		// Choice 2: Raise
		ciRaise = new ContinueItem(
				ispThisNode.addInfoToBoth(new InfoToken(infoType, InfoToken.s_raise)), 
				this);

		// Choice 3: Check
		ciCheck = new ContinueItem(
				ispThisNode.addInfoToBoth(new InfoToken(infoType, InfoToken.s_call)),
				this);
	}
}
