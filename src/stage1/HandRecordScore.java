/*
 * Created on May 13, 2005
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
public class HandRecordScore extends HandRecord {

	// CONTRACT SAYS THESE ARE for read only
	public short score;

	public HandRecordScore(int numBoardCards, byte[] holeCards) {
		super(numBoardCards, holeCards);
	}
	
	public HandRecordScore copy() {
		HandRecordScore yo = new HandRecordScore(this.numBoardCards, new byte[] {holeCards[0], holeCards[1]});
		super.copy(yo);

		yo.score = this.score;
		
		return yo;
	}
}