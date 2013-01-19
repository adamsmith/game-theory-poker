/*
 * Created on Jun 20, 2005
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
public class TerminalLeafNode extends Node {

	public TerminalLeafNode(ContinueItem ciTerminal) {
		super(ciTerminal, null);
	}
	
	public static Set terminateSet(Set ciLeafs) {
		Set terminalNodes = new HashSet();
		
		for(Iterator i = ciLeafs.iterator(); i.hasNext(); ) {
			terminalNodes.add(new TerminalLeafNode((ContinueItem) i.next()));
		}
		
		return terminalNodes;
	}
}
