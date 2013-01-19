/*
 * Created on Jul 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage4;

import poker.Action;
import poker.Card;
import poker.GameInfo;
import poker.*;
import poker.util.Preferences;
import _game.*;
import stage3.InfoSet.*;
import stage3.*;
import _misc.*;

import java.util.*;
import _io.*;
import java.io.*;
import java.nio.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MeerkatPlayer implements Player {
	
	// STATIC
	// ----------------------------------------

	private final static int s_indHearts = 0;
	private final static int s_indDiamonds = 1;
	private final static int s_indClubs = 2;
	private final static int s_indSpades = 3;
	private final static int s_indTwo = 0;
	private final static int s_indThree = 1;
	private final static int s_indFour = 2;
	private final static int s_indFive = 3;
	private final static int s_indSix = 4;
	private final static int s_indSeven = 5;
	private final static int s_indEight = 6;
	private final static int s_indNine = 7;
	private final static int s_indTen = 8;
	private final static int s_indJack = 9;
	private final static int s_indQueen = 10;
	private final static int s_indKing = 11;
	private final static int s_indAce = 12;
	
	private static byte getCardIndex(Card x) {
		// Don't rely on the backing values of the poker.Card class.
		//  Only use comparisons to its constants.
		int suitMultiplier = -1;
		switch(x.getSuit()) {
			case Card.HEARTS:
				suitMultiplier = s_indHearts;
				break;
			case Card.DIAMONDS:
				suitMultiplier = s_indDiamonds;
				break;
			case Card.CLUBS:
				suitMultiplier = s_indClubs;
				break;
			case Card.SPADES:
				suitMultiplier = s_indSpades;
				break;
			default:
				throw new RuntimeException();
		}
		
		int rank = -1;
		switch(x.getRank()) {
			case Card.TWO:
				rank = s_indTwo;
				break;
			case Card.THREE:
				rank = s_indThree;
				break;
			case Card.FOUR:
				rank = s_indFour;
				break;
			case Card.FIVE:
				rank = s_indFive;
				break;
			case Card.SIX:
				rank = s_indSix;
				break;
			case Card.SEVEN:
				rank = s_indSeven;
				break;
			case Card.EIGHT:
				rank = s_indEight;
				break;
			case Card.NINE:
				rank = s_indNine;
				break;
			case Card.TEN:
				rank = s_indTen;
				break;
			case Card.JACK:
				rank = s_indJack;
				break;
			case Card.QUEEN:
				rank = s_indQueen;
				break;
			case Card.KING:
				rank = s_indKing;
				break;
			case Card.ACE:
				rank = s_indAce;
				break;
			default:
				throw new RuntimeException();
		}
		
		return (byte) (_game.Card.NUM_RANKS * suitMultiplier + rank);
	}
	
	private static byte getCluster(byte[] holeCards, byte[] boardCards) {
		if(s_clusterIdLookup == null) {
			try {
				s_clusterIdLookup = ClusterIdResolver.getClusterIdResolver();
			} catch (Exception e) {
				System.out.println("RTE!  error initializing cluster id resolver");
				throw new RuntimeException(e);
			}
		}
		try {
			return (byte) s_clusterIdLookup.getCluster(holeCards, boardCards);
		} catch (Exception e) {
			System.out.println("RTE!  error getting cluster id (" 
					+ Helper.byteArrayToString(holeCards) + ", " 
					+ Helper.byteArrayToString(boardCards) + ")");
			throw new RuntimeException(e);
		}
	}
	
	private static float getWeight(InfoString longName, boolean isP1, String subtreeName) {
		if(s_WeightLookup == null) {
			try {
				s_WeightLookup = WeightResolver.getWeightResolver();
			} catch (Exception e) {
				System.out.println("RTE!  error initializing weight resolver");
				throw new RuntimeException(e);
			}
		}
		s_WeightLookup.setSubtreeName(subtreeName);
		try {
			return s_WeightLookup.getWeight(longName, isP1);
		} catch (Exception e) {
			System.out.println("RTE!  error getting weight (" + longName + ")");
			throw new RuntimeException(e);
		}
	}

	private static DataOutputStream out;
	private static String logFileName = "c:\\lovebot-" + _game.Card.GAME_NAME + ".log";
	
	private static void printLog(String x) {
		x = new Date().toString() + "  --  " + x;
		if(out == null) {
			try {
				out = new DataOutputStream(new FileOutputStream(logFileName));
			} catch (Exception e) {
				System.out.println("RTE!  error creating log file");
				throw new RuntimeException(e);
			}
		}

		System.out.println(x);
		try {
			out.writeBytes(x + "\n");
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
	
	public static final String ROOT_INPUT_DIR = Constants.DATA_FILE_REPOSITORY + 
			"stage3" + Constants.dirSep;
	
	private static final int MAX_SIMULT_FILES_OPEN = 1;

	private static ClusterIdResolver s_clusterIdLookup = null;
	private static WeightResolver s_WeightLookup = null;
	
	// NON-STATIC 
	// ---------------------------------------------
	
	private byte[] m_holeCards = null;
	private byte[] m_boardCards = null;
	private boolean m_isP1;
	private int m_seat;
	private GameInfo m_gi = null;
	private InfoString m_is = null;
	private byte m_numBoardCards = -1;
	private byte m_brDepth = -1;
	private String subtreeName;
	private Map m_actionWeights;
	private NameMap m_actionNames;
	
	public MeerkatPlayer() {
		printLog("constructed");
	}

	public void holeCards(Card c0, Card c1, int seat) {
		if(_game.Card.NUM_RANKS != 13 || _game.Card.NUM_SUITS != 4) {
			printLog("RTE!  wrong game parameters!");
			throw new RuntimeException();
		}
		
		printLog("got hole cards");
		
		m_boardCards = new byte[0];
		m_isP1 = (seat == m_gi.getButton());
		m_seat = seat;
		m_numBoardCards = 0;
		m_brDepth = 0;
		
		m_holeCards = new byte[] {getCardIndex(c0), getCardIndex(c1)};
		Arrays.sort(m_holeCards);
		
		m_is = new InfoString(new byte[0]);
		
		if(m_isP1) {
			printLog("I am the dealer");
		} else {
			printLog("I am not the dealer");
		}

		loadSolution(DoGT.rootName);

		updateMyCluster();
	}

	public Action getAction() {		
		float[] weights = new float[] {Float.NaN, Float.NaN, Float.NaN}; // fold, raise, call
		printLog("chosing action under conditions " + m_is + " (brDepth = " + m_brDepth + ")");
		byte action = InfoToken.s_fold;
		while(action >= 0) {
			InfoString augmented = m_is.push(InfoToken.factory(m_numBoardCards, m_brDepth,
					action, false));
			
//			Integer choiceName = new Integer(m_actionNames.getShort(augmented, false));
//			if(choiceName.intValue() != -1) {
//				Float tmpFloat = (Float) m_actionWeights.get(choiceName);
//				if(tmpFloat == null) {
//					printLog("action " + action + " not in solution file (" 
//							+ choiceName + ", " + augmented + ")");
//					weights[action] = 0;
//				} else {
//					weights[action] = tmpFloat.floatValue();
//				}
//			} else {
//				printLog("info string not recognized under action " 
//						+ action + " (" + augmented + ") -- assuming it's an illegal move");
//				weights[action] = 0;
//			}
			weights[action] = getWeight(augmented, m_isP1, this.subtreeName);
			
//			if(weights[action] != getWeight(augmented, m_isP1, this.subtreeName)) {
//				printLog("WeightResolver discrepancy...");
//				printLog("   subtree " + this.subtreeName);
//				printLog("   longname " + augmented);
//				printLog("   longname array " + Helper.byteArrayToString(augmented.arr));
//				printLog("   true shortname " + choiceName);
//				printLog("   true weight " + weights[action]);
//				printLog("   weightresolver weight " + getWeight(augmented, m_isP1, this.subtreeName));
//			}
			
			switch(action) {
				case InfoToken.s_fold:
					action = InfoToken.s_raise;	break;
				case InfoToken.s_raise:
					action = InfoToken.s_call; break;
				case InfoToken.s_call:
					action = -1; break;
				default:
					printLog("RTE!  unknown chosen action");
					throw new RuntimeException();
			}
		}
		
		// note that we're not dividing by the parent's weight..that will be taken
		//   care of in normalization

		printLog("chose action from " + Helper.floatArrayToString(weights));
		
		return pickActionFromUnnormalizedTriple(weights[0], weights[1], weights[2]);
	}
	
	private Action pickActionFromUnnormalizedTriple(float fold, float raise, float call) {
		float sum = fold + raise + call;
		fold /= sum;
		raise /= sum;
		call /= sum;
		printLog("choice sum " + sum);
		if(sum == 0) {
			printLog("RTE!  ill formed choice");
			throw new RuntimeException();
		}
		float magicNumber = (float) Math.random();
		printLog("magic number " + magicNumber);
		double toCall = m_gi.getAmountToCall(m_seat);
		if(magicNumber < fold) {
			return Action.foldAction(toCall);
		}
		if(magicNumber < fold + raise) {
			return Action.raiseAction(toCall, m_gi.getBetSize());
		}
        return Action.callAction(toCall);
	}

	public void actionEvent(int seat, Action act) {
		printLog("incoming action event (" + act + ")");
		
		InfoString oldIs = m_is;
		
		if(act.isCheckOrCall()) {
			m_is = m_is.push(InfoToken.factory(m_numBoardCards, m_brDepth, 
					InfoToken.s_call, false));
		} else if(act.isFoldOrMuck()) {
			m_is = m_is.push(InfoToken.factory(m_numBoardCards, m_brDepth, 
					InfoToken.s_fold, false));
		} else if(act.isBetOrRaise()) {
			m_is = m_is.push(InfoToken.factory(m_numBoardCards, m_brDepth, 
					InfoToken.s_raise, false));
		} else if(act.isBlind()) {
			return; // don't increment m_brDepth!
		} else {
			printLog("RTE!  unknown action (" + seat + ", " + act.toString() + ")");
			throw new RuntimeException();
		}

		printLog("action " + oldIs + " -> " + m_is);
		
		m_brDepth++;
	}
	
	private void loadSolution(String subtreeName) {
		this.subtreeName = subtreeName;
		
//		printLog("loading subtree solution...");
//		
//		String awName = ROOT_INPUT_DIR + subtreeName + Constants.dirSep;
//		String nmName = awName;
//		if(m_isP1) {
//			awName += "game.p1.sol.obj";
//			nmName += "nameMap.p1.obj";
//		} else {
//			awName += "game.p2.sol.obj";
//			nmName += "nameMap.p2.obj";
//		}
//		try {
//			printLog("loading " + awName);
//			m_actionWeights = ReadBinarySolutionMap.getSolutionMap(awName, m_isP1);
//			printLog("loading " + nmName);
//			m_actionNames = ReadBinaryNameMap.getNameMap(nmName);
//		} catch (Exception e) {
//			printLog("RTE!  problem reading solution files");
//			throw new RuntimeException(e);
//		}
//		
//		printLog("subtree solution loaded");
	}
	
	private static String findSubtreeName(InfoString rootBrConclusion) {
		byte lastChoice = rootBrConclusion.getLastElement();
		InfoString[][] subtreeDefs = (InfoToken.isP1(lastChoice) ? 
				DoSubtreeGames.decisionsP1 : DoSubtreeGames.decisionsP2);
		
		String subtreeName = null;
		// search...
		for(int i = 0; i < subtreeDefs.length; i++) {
			InfoString[] sequenceActions = subtreeDefs[i];
			InfoString lastActionSequence = sequenceActions[sequenceActions.length-1];
			
			if(lastActionSequence.arr.length != rootBrConclusion.arr.length - 1) {
				continue;
			}
			boolean match = true;
			for(int j = 0; j < lastActionSequence.arr.length; j++) {
				if(lastActionSequence.arr[j] != rootBrConclusion.arr[j+1]) {
					match = false;
					break;
				}
			}
			if(match) {
				if(subtreeName != null) {
					printLog("RTE!  multiple matching subtrees");
					throw new RuntimeException();
				}
				subtreeName = Constants.subtreeNames[i];
			}
		}
		
		if(subtreeName == null) {
			printLog("RTE!  subtree name not found");
			throw new RuntimeException();
		}
		
		return subtreeName;
	}
	
	private void updateMyCluster() {
		printLog("looking up cluster");
		byte cluster = getCluster(m_holeCards, m_boardCards);
		printLog("new cluster == " + cluster);
		m_is = m_is.push(InfoToken.factory(m_numBoardCards, 
				m_isP1 ? DoGT.s_player1 : DoGT.s_player2, cluster, true));
	}

	public void stageEvent(int stage) {
		printLog("new stage (" + stage + ")");
		m_brDepth = 0;
		switch(stage) {
			case Holdem.PREFLOP:
				m_numBoardCards = 0;
				break;
				
			case Holdem.FLOP:
				loadSolution(findSubtreeName(m_is));
				m_numBoardCards = 3;
				for(int i = 0; i < 3; i++) {
					m_boardCards = Helper.appendToByteArray(m_boardCards, 
							getCardIndex(m_gi.getBoardCard(i)));
				}
				Arrays.sort(m_boardCards);
				m_is = new InfoString(new byte[0]);
				updateMyCluster();
				break;
				
			case Holdem.TURN:
				m_numBoardCards = 4;
				m_boardCards = Helper.appendToByteArray(m_boardCards, 
						getCardIndex(m_gi.getBoardCard(3)));
				Arrays.sort(m_boardCards);
				updateMyCluster();
				break;
				
			case Holdem.RIVER:
				m_numBoardCards = 5;
				m_boardCards = Helper.appendToByteArray(m_boardCards, 
						getCardIndex(m_gi.getBoardCard(4)));
				Arrays.sort(m_boardCards);
				updateMyCluster();
				break;
				
			default:
				printLog("RTE!  unknown stage");
				throw new RuntimeException();
		}
		
	}

	public void gameStartEvent(GameInfo arg0) {
		printLog("");
		printLog("");
		printLog("game start");
		m_gi = arg0;
	}
	
	// UNUSED
	// --------------------------

	public void init(Preferences arg0) {

	}

	public void winEvent(int pos, double amount, String handName) {

	}

	public void showdownEvent(int arg0, Card arg1, Card arg2) {

	}

	public void gameOverEvent() {

	}

	public void gameStateChanged() {

	}

}
