/*
 * Created on May 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import _game.Card;
import _misc.Combinations;
import _misc.Constants;
import _misc.Helper;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ScoreMaps {

	private byte numBoardCards;
	private byte[] holeCards;
	
	private Map[] maps;  // NOT STATIC!
	private int[][][][] indexes4;  // not thread safe
	private int[][][] indexes3;
	
//	private Map indexMap = new HashMap(); // for each hand, we keep track of its scores+counts
	
	private static final Integer one = new Integer(1);
	
	public ScoreMaps(byte numBoardCards, byte[] holeCards) {
		this.holeCards = new byte[] {holeCards[0], holeCards[1]};
		this.numBoardCards = numBoardCards;
		
		maps = new Map[Constants.choose(52, numBoardCards)];
		
		if(numBoardCards == 4) {
			indexes4 = new int[52][52][52][52];
		}
		if(numBoardCards == 3) {
			indexes3 = new int[52][52][52];
		}
		
		HandRecord hr = new HandRecord(numBoardCards, holeCards);
		while(hr.hasMoreElements) {
			hr.advanceRecord();
			if(numBoardCards == 4) {
				indexes4[hr.boardCards[0]][hr.boardCards[1]][hr.boardCards[2]][hr.boardCards[3]] = Helper.getComboArrayIndex(52, numBoardCards, hr.boardCards);
				maps[getIndex(hr.boardCards)] = new HashMap();
			} else if(numBoardCards == 3) {
				indexes3[hr.boardCards[0]][hr.boardCards[1]][hr.boardCards[2]] = Helper.getComboArrayIndex(52, numBoardCards, hr.boardCards);
				maps[getIndex(hr.boardCards)] = new HashMap();
			} else if(numBoardCards == 0) {
				maps[0] = new HashMap();
			}
		}
	}
	
	public void addScore(HandRecord cardsToMatch, short x) {
		Short score = new Short(x);
		
		// bcc = Board Card Combinations
		Combinations bcc = new Combinations(cardsToMatch.boardCards, numBoardCards);
		
		while(bcc.hasMoreElements()) {
			byte[] cards = bcc.nextElement();
			Map scoreMap = maps[getIndex(cards)];
			
			if(scoreMap.containsKey(score)) {
				Integer oldCount = (Integer) scoreMap.get(score);
				scoreMap.put(score, new Integer(oldCount.intValue() + 1));
			} else {
				scoreMap.put(score, one);
			}
		}
	}
	
	public void addScoreCountFromFile(HandRecord cards, short score, int count) {
		Map scoreMap = maps[getIndex(cards.boardCards)];
		scoreMap.put(new Short(score), new Integer(count));
	}
	
	public int getNumUniqueScores() {
		int sum = 0;
		for(int i = 0; i < maps.length; i++) {
			if(maps[i] != null) {
				sum += maps[i].size();
			}
		}
		
		return sum;
	}
	
	public void print() {
		// asumme numBC == 4
		Map scoreMap = maps[getIndex(new byte[] {0,1,2,3})];
		for(Iterator i = scoreMap.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry yo = (Map.Entry) i.next();
			System.out.println(yo.getKey() + " - " + yo.getValue());
		}
		System.out.println("---");
		scoreMap = maps[getIndex(new byte[] {0,21,22,39})];
		for(Iterator i = scoreMap.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry yo = (Map.Entry) i.next();
			System.out.println(yo.getKey() + " - " + yo.getValue());
		}
	}
	
//	public boolean equal(ScoreMaps that) {
//		HandRecord hr = new HandRecord(numBoardCards, holeCards);
//		while(hr.hasMoreElements) {
//			hr.advanceRecord();
//			Map s1 = this.getScoreMapWithCards(hr);
//			Map s2 = that.getScoreMapWithCards(hr);
//			for(Iterator i = s1.entrySet().iterator(); i.hasNext(); ) {
//				Map.Entry entry = (Map.Entry) i.next();
//				Short score = (Short) entry.getKey();
//				Integer count = (Integer) entry.getValue();
//				if(!((Integer) s2.get(score)).equals(count)) {
//					throw new RuntimeException();
//				}
//			}
//		}
//		return true;
//	}
	
	public byte getNumberBoardCards() {
		return numBoardCards;
	}
	
	public byte[] getHoleCards() {
		return holeCards;
	}
	
	public Map getScoreMapWithCards(HandRecord cardsToMatch) {
//		for(int i = 0; i < 2; i++) {
//			if(holeCards[i] != cardsToMatch.holeCards[i]) {
//				throw new RuntimeException();
//			}
//		}
		
		// do lookup, return HandRecord
		int mapIndex = getIndex(cardsToMatch.boardCards);
		Map map = maps[mapIndex];
		if(maps[mapIndex] == null) {
			throw new RuntimeException();
		}
		maps[mapIndex] = null;
		return map;
	}
	
	private int getIndex(byte[] yo) {
		if(numBoardCards == 4) {
			return indexes4[yo[0]][yo[1]][yo[2]][yo[3]];
		} else if(numBoardCards == 3) {
			return indexes3[yo[0]][yo[1]][yo[2]];
		} else { 
			// numBoardCards == 0
			return 0;
		}
	}
	
	private static void testIndexMethods() {
		Map ind = new HashMap();
		Combinations combo = new Combinations(Card.ALLCARDSINDEX, 4);
		int[][][][] direct = new int[52][52][52][52];

		double timer1 = System.currentTimeMillis();

		List trials = new ArrayList();
		while(combo.hasMoreElements()) {
			byte[] yo = combo.nextElement();
			byte[] yo2 = new byte[] { 0, yo[0], yo[1], yo[2], yo[3] };
			ind.put(new Integer(Helper.hashByteArray(yo2)), new Integer(Helper.getComboArrayIndex((byte)52, (byte)4, yo2)));
			direct[yo[0]][yo[1]][yo[2]][yo[3]] = Helper.getComboArrayIndex((byte)52, (byte)4, yo);
			if(Math.random() < .3) {
				trials.add(yo2);
			}
		}
		
		System.out.println(System.currentTimeMillis() - timer1);

		timer1 = System.currentTimeMillis();
		for(Iterator i = trials.iterator(); i.hasNext(); ) {
			int answer = ((Integer) ind.get(new Integer(Helper.hashByteArray((byte[])i.next())))).intValue();
			if(answer < 0) {
				throw new RuntimeException();
			}
		}

		System.out.println(System.currentTimeMillis() - timer1);
		timer1 = System.currentTimeMillis();
		
		for(Iterator i = trials.iterator(); i.hasNext(); ) {
			int answer = Helper.getComboArrayIndex((byte)52, (byte)4, (byte[]) i.next());
			if(answer < 0) {
				throw new RuntimeException();
			}
		}

		System.out.println(System.currentTimeMillis() - timer1);
		timer1 = System.currentTimeMillis();
		
		for(Iterator i = trials.iterator(); i.hasNext(); ) {
			byte[] yo = (byte[]) i.next();
			int answer = direct[yo[0]][yo[1]][yo[2]][yo[3]];
			if(answer < 0) {
				throw new RuntimeException();
			}
		}

		System.out.println(System.currentTimeMillis() - timer1);
		System.out.println(trials.size());
	}
}
