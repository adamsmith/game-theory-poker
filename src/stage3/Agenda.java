/*
 * Created on Jun 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3;

import stage3.InfoSet.*;
import java.util.*;
import _misc.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Agenda {

	public static List getNextGameStates(GameState x) {
		List y = new ArrayList();
		
		if(x.isChance) {
			double sumOfProbabilities = 0; // should sum to one
			for(byte i = 0; i < DoGT.numClusters[x.bcCount]; i++) {
				for(byte j = 0; j < DoGT.numClusters[x.bcCount]; j++) {
					// players {1,2} are assigned to clusters {i,j}
					float transitionProbability;
					if(x.bcCount == (byte) GameState.startNumBoardCards) {
						// note: startPDTs were triangular w/ diagonals,
						//       now full (converted in LoadInputData)
						transitionProbability = (float)DoGT.startPDT[i][j];
					} else {
						switch(x.bcCount) {
							case 3:
								// note that this code is only executed
								//   for the rooted game tree, and not
								//   for the subtree games
								transitionProbability = DoGT.transition0to3
										[GameState.clusters[0]][GameState.clusters[1]][i][j];
								break;
							case 4:
								transitionProbability = DoGT.transition3to4
										[GameState.clusters[0]][GameState.clusters[1]][i][j];
								break;
							case 5:
								transitionProbability = DoGT.transition4to5
										[GameState.clusters[0]][GameState.clusters[1]][i][j];
								break;
							default:
								throw new RuntimeException();
						}
					}
					if(transitionProbability == -1) {
						throw new RuntimeException();
					}
					sumOfProbabilities += transitionProbability;
					y.add(new GameState(x.bcCount, (byte)0, (byte)0, false, 
							transitionProbability, false, 
							InfoToken.factory(x.bcCount, DoGT.s_player1, i, true),
							InfoToken.factory(x.bcCount, DoGT.s_player2, j, true), 0, 0,
							new byte[] {i, j}));
				}
			}
			if(sumOfProbabilities > 1.001 || sumOfProbabilities < 0.999) {
				throw new RuntimeException();
			}
		} else {
			// the identity of the chooser (p1 or p2) doesn't matter to us
			
			// Folding
			if(x.numRaises > 0 || (x.bcCount == 0 && x.brDepth == 0)) {
				// folding makes sense
				y.add(new GameState(x.bcCount, (byte)(x.brDepth+1), x.numRaises, false, -1, true,
						InfoToken.factory(x.bcCount, x.brDepth, InfoToken.s_fold, false), 0, 0, null));
			}
			
			// Checking/calling
			if(x.brDepth == 0) {
				// special case...if check then the other person can bet
				if(x.numRaises != 0) {
					throw new RuntimeException();
				}
				y.add(new GameState(x.bcCount, (byte)(x.brDepth+1), x.numRaises, false, -1, false, 
						InfoToken.factory(x.bcCount, x.brDepth, InfoToken.s_call, false),
						(x.bcCount == 0 ? (float)0.5 : 0), 0, null));
			} else {
				if(x.bcCount == GameState.endNumBoardCards) {
					// checking/calling ends game...unless this is the first action
					y.add(new GameState(x.bcCount, (byte)(x.brDepth+1), x.numRaises, false, -1, true,
							InfoToken.factory(x.bcCount, x.brDepth, InfoToken.s_call, false),
							Math.max(0, GameState.potP2 - GameState.potP1),
							Math.max(0, GameState.potP1 - GameState.potP2), null));
				} else {
					// otherwise, if they call then we move on to the next chance node etc.
					y.add(new GameState(Helper.getNextBoardCardCount(x.bcCount, false), (byte)-1, (byte)-1, true, -1, false, 
							InfoToken.factory(x.bcCount, x.brDepth, InfoToken.s_call, false),
							Math.max(0, GameState.potP2 - GameState.potP1),
							Math.max(0, GameState.potP1 - GameState.potP2), null));
				}
			}
			
			// Raising
			if(x.numRaises < InfoToken.s_maxNumRaises[x.bcCount]) {  // no raise allowed at certain point
				byte newInfo = InfoToken.factory(x.bcCount, x.brDepth, InfoToken.s_raise, false);
				y.add(new GameState(x.bcCount, (byte)(x.brDepth+1), (byte)(x.numRaises+1), false, -1, false, 
						newInfo,
						(InfoToken.isP1(newInfo) ? (GameState.potP2 - GameState.potP1) + InfoToken.raiseAmounts[x.bcCount] : 0),
						(InfoToken.isP1(newInfo) ? 0 : (GameState.potP1 - GameState.potP2) + InfoToken.raiseAmounts[x.bcCount]), 
						null));
			}
		}
		
		return y;
	}
}
