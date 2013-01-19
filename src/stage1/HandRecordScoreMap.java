/*
 * Created on May 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage1;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HandRecordScoreMap extends HandRecord {

	// CONTRACT SAYS THESE ARE for read only
	public Map scoreMap = null;
	public static final Integer one = new Integer(1);

	public HandRecordScoreMap(int numBoardCards, byte[] holeCards) {
		super(numBoardCards, holeCards);
		scoreMap = new HashMap();
	}
	

	public void advanceRecord() {
		super.advanceRecord();
		scoreMap = new HashMap();
	}
	
	public void addScore(short x) {
		Short score = new Short(x);
		if(scoreMap.containsKey(score)) {
			Integer oldCount = (Integer) scoreMap.get(score);
			scoreMap.put(score, new Integer(oldCount.intValue() + 1));
		} else {
			scoreMap.put(score, one);
		}
	}
	
	public void addScoreFromFile(short score, int count) {
		Short sScore = new Short(score);
		Integer iCount = new Integer(count);
		
		if(scoreMap.containsKey(sScore)) {
			throw new RuntimeException();
		}
		
		scoreMap.put(sScore, iCount);
	}
	
	public HandRecordScoreMap copy() {
		HandRecordScoreMap yo = new HandRecordScoreMap(this.numBoardCards, new byte[] {holeCards[0], holeCards[1]});
		super.copy(yo);

		if(this.scoreMap != null) {
			yo.scoreMap = new HashMap();
			yo.scoreMap.putAll(this.scoreMap); // don't have to copy values of key/value pairs -- all immutable
		}
		
		return yo;
	}
	
}
