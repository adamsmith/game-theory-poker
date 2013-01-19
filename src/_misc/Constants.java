/*
 * Created on May 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _misc;

import _game.Card;

/**
 * @author Adam
 *0
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Constants {
	
	public final static String dirSep = 
		(System.getProperty("os.name").substring(0, 5).equals("Windo") ?
				"\\" :
				"/");

	public final static String DATA_FILE_REPOSITORY = 
		(System.getProperty("os.name").substring(0, 5).equals("Windo") ?
			"z:" + Constants.dirSep + "adam" + Constants.dirSep + "tmp" + Constants.dirSep + "poker_data" + Constants.dirSep + Card.GAME_NAME + "" + Constants.dirSep :
			"/net1/poker/poker_data/" + Card.GAME_NAME + "/");
	
	
	public final static int FILE_HEADER_LENGTH_BYTES = 22;
	
	// versions
	public final static short vidScoreOnly_holeCardsConstant_boardCardsImplicit = 1;
	public final static short vidScoreMap_holeCardsConstant_boardCardsImplicit = 11;
	public final static short vidClusterIDs_holeCardsConstant_boardCardsImplicit = 21;

	public final static short vidClusterGroupsStep_holeCardsConstant_boardCardsImplicit = 71;
	public final static short vidClusterIDTables_holeCardsConstant_boardCardsImplicit = 31;

	public final static short vidTransitionPDT = 41;
	public final static short vidTransitionPDTLongCounts = 42;
	public final static short vidTerminalClusterValues = 51;
	public final static short vidStartClusterPDT = 61;

	public final static short vidTreeNodeNameMap = 81;
	public final static short vidRewardMatrixElementList = 91;
	public final static short vidConstraintMatrix = 101;
	public final static short vidSolutionMap = 111;
	public final static short vidSubtreeGameDescription = 121;

	public final static short vidSolutionList = 131;
	public final static short vidNameList = 141;

	public final static int TOTAL_BUFFER_AVAILABLE = 300000000; // 300MB
	public final static int MAX_SINGLE_FILE_BUFFER = 10000000; // 10MB
	
	public final static int HEADER_SIZE = 22;
	
	public static int choose(int n, int k) {
		// assume k small (< 10)
		// assume n not too bad (< 60)
		
		if(k > 5 || n > 52) {
			throw new RuntimeException("not getting a legal answer");
		}
		
		int answer = 1;
		for(int i = n-k+1; i <= n; i++) {
			answer *= i;
		}
		
		return answer/factTo12[k];
	}
	
	
	public final static int[] factTo12 = new int[]
												 {
			1, // define fact(0) = 1
			1,
			2,
			6,
			24,
			120,
			720,
			5040,
			40320,
			362880,
			3628800,
			39916800,
			479001600};

	public final static String[] subtreeNames = new String[] {"a", "b", "c", "d", "e", "f", "g"};
	
}
