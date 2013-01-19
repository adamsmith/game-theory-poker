/*
 * Created on Jun 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage1;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HandRecordClusterId extends HandRecord {

	// CONTRACT SAYS THESE ARE for read only
	public byte clusterId;

	public HandRecordClusterId(int numBoardCards, byte[] holeCards) {
		super(numBoardCards, holeCards);
	}
	
	public HandRecordClusterId copy() {
		HandRecordClusterId yo = new HandRecordClusterId(this.numBoardCards, new byte[] {holeCards[0], holeCards[1]});
		super.copy(yo);

		yo.clusterId = this.clusterId;
		
		return yo;
	}

}
