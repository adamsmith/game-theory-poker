/*
 * Created on Jun 24, 2005
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
public class HandRecordScoreGroups extends HandRecord {

	public int[] scoreGroups;
	public int numScoreGroups;

	public HandRecordScoreGroups(int numBoardCards, byte[] holeCards, int numScoreGroups) {
		super(numBoardCards, holeCards);
		scoreGroups = new int[numScoreGroups];
		this.numScoreGroups = numScoreGroups;
	}
	
	public HandRecordScoreGroups copy() {
		HandRecordScoreGroups yo = new HandRecordScoreGroups(
				this.numBoardCards, new byte[] {holeCards[0], holeCards[1]}, numScoreGroups);
		super.copy(yo);

		for(int i = 0; i < scoreGroups.length; i++) {
			yo.scoreGroups[i] = this.scoreGroups[i];
		}
		
		return yo;
	}

}
