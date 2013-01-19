/*
 * Created on Jun 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3.InfoSet;

import _misc.*;
import stage3.*;
import java.io.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InfoToken implements Serializable {

	// an info token is always atomic.  You can't have an InfoToken
	//   that contains multiple atomic info's.
	// immutable, by agreement
	

	public final static String[] s_choicesAsString = new String[] {"F", "R", "C"};

	public final static float bigBlind = 1;
	public final static float smallBlind = (float)0.5;
	
	public final static int[] s_maxNumRaises = new int[] {3, -1, -1, 4, 4, 4};

	public final static boolean[][] s_isP1Choice = new boolean[][] {
		new boolean[] {true, false, true, false, true},				// choices for bc=0 betting round
		new boolean[] {},
		new boolean[] {},
		new boolean[] {false, true, false, true, false, true},
		new boolean[] {false, true, false, true, false, true},		// bc=4 (p2, p1, p2, p1, p2, p1)
		new boolean[] {false, true, false, true, false, true}		// bc=5 (p2, p1, p2, p1, p2, p1)
	};
	public final static boolean[][] s_isP1Chance = new boolean[][] {
		new boolean[] {true, false},						// chance node for bc0 cards (p1, p2)
		new boolean[] {},
		new boolean[] {},
		new boolean[] {true, false},
		new boolean[] {true, false},
		new boolean[] {true, false}							// chance node for river (p1, p2)
	};
	
	public final static float[] raiseAmounts = new float[] {1, -10000, -10000, 1, 2, 2};

	public final static int[] numLeafNodes = new int[] {6, -1, -1, 8, 8, 17};
	public final static int[] numBrContinuations = new int[] {7, -1, -1, 9, 9, 0};
	
	public final static int[] numPlayerDecisions = new int[] {4, -1, -1, 5, 5, 5};
	

	public final static byte s_fold = 0;
	public final static byte s_raise = 1;  // except s_raise not legal if this is 
										  //    from an EndRoundChoiceNode
	public final static byte s_call = 2;   // except s_call semantically means "check" 
										  //    if this is from a BeginRoundChoiceNode

	// IMMUTABLE (by policy)
	
//	public byte token;
		// bit 7: isChance
		// bit 5,6: numBoardCards (0==00, 3==01, 4==10, 5==11)
		// bit 0-4: if(isChance)-> bit4==isP1, bit0-3==clusterID (0xF disallowed)
		//			if(!isChance)-> bit3-4==choice, bit0-2==brDepth
		// 0xFF is reserved!
	
	public final static byte reservedToken = (byte) 0xFF;
	
	private final static int isChanceTokenPattern = 128;

	private final static int numBoardCardsTokenMask = 96;
	private final static int[] numBoardCardsTokenPattern = new int[] {0, -1, -1, 32,
			64, 96};
	
	private final static int isP1TokenPattern = 16;

	private final static int actionTokenMask = 24;
	private final static int[] actionTokenPattern = new int[] {s_fold << 3, s_raise << 3,
			s_call << 3};

	private final static int brDepthMask = 7;

	private final static int clusterIdMask = 15;
	
	public static byte factory(byte numBoardCards, byte secondIndex, 
			byte value, boolean isChance) {
		int intToken;
		intToken = (isChance ? isChanceTokenPattern : 0);
		intToken |= numBoardCardsTokenPattern[numBoardCards];
		if(isChance) {
			if(secondIndex == DoGT.s_player1) {
				intToken |= isP1TokenPattern;
			} else if(secondIndex != DoGT.s_player2) {
				throw new RuntimeException();
			}
			if(value > 15 || value < 0) {
				throw new RuntimeException();
			}
			intToken |= value;
		} else {
			intToken |= actionTokenPattern[value];
			if(secondIndex > 7 || secondIndex < 0) {
				throw new RuntimeException();
			}
			
			intToken |= secondIndex; // brDepth
		}
		if(intToken > 255 || intToken < 0) {
			throw new RuntimeException();
		}
		return (byte)intToken;
	}
	
	public static boolean isChance(byte token) {
		int intToken = token;
		if((intToken & isChanceTokenPattern) == isChanceTokenPattern) {
			return true;
		}
		return false;
	}
	
	public static int numBoardCards(byte token) {
		int intToken = token;
		intToken &= numBoardCardsTokenMask;
		if(intToken == numBoardCardsTokenPattern[0]) {
			return 0;
		} else if(intToken == numBoardCardsTokenPattern[3]) {
			return 3;
		} else if(intToken == numBoardCardsTokenPattern[4]) {
			return 4;
		} else if(intToken == numBoardCardsTokenPattern[5]) {
			return 5;
		}
		return -1;
	}
	
	public static int secondIndex(byte token) {
		int intToken = token;
		if(isChance(token)) {
			if((intToken & isP1TokenPattern) == isP1TokenPattern) {
				return DoGT.s_player1;
			} else {
				return DoGT.s_player2;
			}
		} else {
			return intToken & brDepthMask;
		}
	}
	
	public static int value(byte token) {
		int intToken = token;
		if(isChance(token)) {
			return intToken & clusterIdMask;
		} else {
			intToken &= actionTokenMask;
			if(intToken == actionTokenPattern[0]) {
				return s_fold;
			} else if(intToken == actionTokenPattern[1]) {
				return s_raise;
			} else if(intToken == actionTokenPattern[2]) {
				return s_call;
			}
		}
		return -1;
	}
	
	public static boolean isP1(byte token) {
		if(isChance(token)) {
			return s_isP1Chance[numBoardCards(token)][secondIndex(token)];
		} else {
			return s_isP1Choice[numBoardCards(token)][secondIndex(token)];
		}
	}
	
	public static boolean isP2(byte token) {
		return !isP1(token);
	}
	
	public static String toString(byte token) {
		if(isChance(token)) {
			// chance result info
			return numBoardCards(token) + "" + (isP1(token) ? 1 : 2) + "" + value(token) + "-";
		} else {
			// choice
			return numBoardCards(token) + "" + (isP1(token) ? 1 : 2) + "" + s_choicesAsString[value(token)] + "-";
		}
	}
	
//	// does not override Object.equals()
//	public boolean equals(InfoToken obj) {
//		if(obj.token != this.token) {
//			return false;
//		}
//		
//		return true;
//	}
//	
//	public int hashCode() {
//		return token;
//	}
//	
//	public boolean equals(Object obj) {
//		return equals((InfoToken) obj); // we want to get ClassCastException if obj !instanceof InfoToken
//	}
}
