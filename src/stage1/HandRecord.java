/*
 * Created on May 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage1;

import _misc.Combinations;
import _misc.Helper;


public class HandRecord {

	// CONTRACT SAYS THESE ARE for read only
	// (except boardCards is written to directly from Main.main(), which mean
	//   that you can't use the iterator with that HandRecord)
	public byte[] boardCards; // invariant: size of boardCards == numBoardCards
	public byte[] holeCards;
	public byte numBoardCards;
	public boolean hasMoreElements = true;
	
	private Combinations combo;
	
	public HandRecord(int numBoardCards, byte[] holeCards) {
		this.numBoardCards = (byte) numBoardCards;
		this.holeCards = new byte[] {holeCards[0], holeCards[1]};
		this.boardCards = new byte[numBoardCards];
		
		combo = new Combinations(
				Helper.getRemainingCards(holeCards), numBoardCards);
		this.hasMoreElements = combo.hasMoreElements();
	}
	
	public void advanceRecord() {
		boardCards = combo.nextElement();
		this.hasMoreElements = combo.hasMoreElements();
	}
		
	protected void copy(HandRecord yo) {
		for(int i = 0; i < numBoardCards; i++) {
			yo.boardCards[i] = this.boardCards[i];
		}
		yo.combo = combo.getCopy();
	}
	
}
