game-theory-poker
=====

This is a game-theoretic, heads-up limit Texas Hold'em poker player I wrote in 2005, based on the paper [_Approximating Game-Theoretic Optimal Strategies for Full-scale Poker_](https://www.google.com/search?q=Approximating+Game-Theoretic+Optimal+Strategies+for+Full-scale+Poker).  It's about 6,000 lines of Java.


### Theory of Operation
Playing the optimal strategy for any game guarantees that you'll achieve at least the expected value of that game.  In poker, assuming no rake, the expected value is zero, so if you can compute the optimal strategy then you're _guaranteed_ not to lose on average.

Consider rock-paper-scissors.  The optimal strategy for that game is to throw `rock` 1/3'rd of the time, `paper` 1/3'rd, and `scissors` 1/3'rd.  If you play by this strategy you are guaranteed not to lose on average, regardless of what strategy your opponent plays by.  (Note that even if your opponent throws `rock` every time, you'll still just tie (not lose) on average.  But there are other games where the optimal strategy can dominate some opponents' strategies, and poker is one of them.)

It's just too hard to compute the optimal strategy for poker, even two player limit poker.  But we can compute the optimal strategy for an abstract version of poker that, for example, during pre-flop betting treats pairs of aces the same way as pairs of kings.

This code computes the optimal strategy for an abstraction of poker that it builds based on the strength of different hands in different situations.

A more detailed description of this idea is given in the paper _Approximating Game-Theoretic Optimal Strategies for Full-scale Poker_ (Billings, 2003).


### How Good Is It?
Okay, so how good is the resulting player?  Well, you can tweak how abstract the game should be at each stage.  The less abstract the game, the better the player will be, but you'll hit computation and memory constraints.

The furthest I got was computing five clusters worth of representation for each turn.  This took about a month to compute using three machines in my apartment living room.  As a median-skilled poker player I could beat the resulting player, but not that easily.


### Opportunities For Improvement
Our abstraction of the game is fairly coarse.  We can build a less abstract version of the game by leveraging the dramatic reduction in game size post-flop.  The whole game tree has 10^18 nodes, but after you have your two hole cards and after the flop, there are just three rounds of betting and (47 choose 2) more cards that are coming.  It's possible to compute the _exact_ optimal strategy for such a narrowed-down game.

So what we should do is: for every (52 choose 2) * (50 choose 3) combination of post-flop states, compute the exact optimal strategy.  Each result will also tell you the expected value of that sub-game.  Then you compute the starting strategy for each (52 choose 2) initial states substituting the expected value from the sub-games as terminal nodes in the game tree.

This isn't an exact version of the game, but it's _far_ better.

The problem is it'd take about 400,000 CPU-hours to compute.

So I started working on this during MIT's Independent Activities Period in January 2006, but I never finished the code.  I stopped because classes started, but also because I didn't have access to 400k cpu-hours.  (Coincidentally, I now have access to that kind of compute through [Gridspot](http://gridspot.com/compute), but alas working on this is no longer feasible given my opportunity costs.)


### Notes On Using The Code
* All calculations are done up-front.  The resulting multi-gigabyte files fully specify the game strategies, so the actual player just has to do a few disk reads to know what to do at each turn.
* Each step creates new data files that are used in later steps.  There are 16 types of files all-in-all.
* All of this code is written to process data sequentially, so that you can compute a multi-gigabyte strategy with comparatively little memory.
* If you want to play around with this code you can compute a strategy for reduced version of poker.  For example, the code defaults to computing the strategy for 14-card poker (2 suits, 7 card ranks).  Use `_game.Cards` to control the parameters of the poker game you're computing for (_eg_ 14 versus 52 card poker) and how many clusters you want at each turn.


### Usage Instructions
1. Compute showdown rankings
	* You should configure `Constants.DATA_FILE_REPOSITORY` first and create the subdirectory `{{ GAME_NAME }}/5`, before running.
	* Run `stage1.DoShowdowns`


1. Create files sorted on hole cards, and counts of `winCounts` (`+ tiecount / 2`) for hole cards during pre-flop, post-flop, post-turn, plus a new data format
	* Run `stage1.DoBacktracking`


1. Calculate clustering into abstract game
	* Create `stage2` directory in `{{ GAME_NAME }}` subdir
	* Run `stage2.DoClusteringStep1` with command line argument `0`
	* Run `stage2.DoClusteringStep2` with command line arguments `0 5`


1. Calculate transition probabilities for abstract game
	* Run `stage2.DoStartClusterPDT`
	* Run `stage2.DoTransitionPDTStep1 0 5`
	* Run `stage2.DoTransitionPDTStep2 5`
	* Run `stage2.DoTerminalClusterValuesStep1`
	* Run `stage2.DoTerminalClusterValuesStep2`


1. Construct game tree, convert game tree to sequence form (single pass), solve sequence form LP problem
	* Run `stage3.DoGT root`
	* Run `stage3.DoPreprocessRm mem-heavy root`
	* Run `stage3.DoWriteLP root`
	* from `{{ GAME_NAME }}\stage3\root`, run `bpmpd game.p1.mps` and `bpmpd game.p2.mps`
	* the last few steps overlap with the first few steps in the following script, which executes everything else needed to get to the MeerkatPlayer.  (Meerkat is a poker player API.)  In the steps below, `/net1/poker/poker_data` is the `Constants.DATA_FILE_REPOSITORY`, and `52-5-1.1` is the `{{ GAME_NAME }}`.  `bpmpd` resides in `/net1`.
	* note that there are other LP-solvers available.  I just happened to use bpmpd.  The MPS file format is supported by most.

		```
		java -Xms400M -Xmx1700M -Xincgc -classpath bin stage3.DoPreprocessRm mem-heavy root
		java -Xms400M -Xmx1700M -Xincgc -classpath bin stage3.DoWriteLP root
		cd /net1/bpmpd
		ln -s /net1/poker/poker_data/52-5-1.1/stage3/root/game.p1.mps rootp1.mps
		ln -s /net1/poker/poker_data/52-5-1.1/stage3/root/game.p2.mps rootp2.mps
		rm -f *.out
		./bpmpd rootp1
		cp rootp1.out /net1/poker/poker_data/52-5-1.1/stage3/root/game.p1.sol
		./bpmpd rootp2
		cp rootp2.out /net1/poker/poker_data/52-5-1.1/stage3/root/game.p2.sol
		cd /net1/poker
		java -Xms400M -Xmx1700M -Xincgc -classpath bin stage3.DoParseClpOutput root
		java -Xms400M -Xmx1700M -Xincgc -classpath bin stage3.DoSubtreeGames
		java -Xms400M -Xmx1700M -Xincgc -classpath bin stage3.DoGT all-subtrees
		java -Xms400M -Xmx1700M -Xincgc -classpath bin stage3.DoPreprocessRm mem-heavy all-subtrees
		java -Xms400M -Xmx1700M -Xincgc -classpath bin stage3.DoWriteLP all-subtrees
		cd /net1/bpmpd
		rm -f ???.mps
		ln -s /net1/poker/poker_data/52-5-1.1/stage3/a/game.p1.mps ap1.mps
		ln -s /net1/poker/poker_data/52-5-1.1/stage3/a/game.p2.mps ap2.mps
		ln -s /net1/poker/poker_data/52-5-1.1/stage3/b/game.p1.mps bp1.mps
		ln -s /net1/poker/poker_data/52-5-1.1/stage3/b/game.p2.mps bp2.mps
		ln -s /net1/poker/poker_data/52-5-1.1/stage3/c/game.p1.mps cp1.mps
		ln -s /net1/poker/poker_data/52-5-1.1/stage3/c/game.p2.mps cp2.mps
		ln -s /net1/poker/poker_data/52-5-1.1/stage3/d/game.p1.mps dp1.mps
		ln -s /net1/poker/poker_data/52-5-1.1/stage3/d/game.p2.mps dp2.mps
		ln -s /net1/poker/poker_data/52-5-1.1/stage3/e/game.p1.mps ep1.mps
		ln -s /net1/poker/poker_data/52-5-1.1/stage3/e/game.p2.mps ep2.mps
		ln -s /net1/poker/poker_data/52-5-1.1/stage3/f/game.p1.mps fp1.mps
		ln -s /net1/poker/poker_data/52-5-1.1/stage3/f/game.p2.mps fp2.mps
		ln -s /net1/poker/poker_data/52-5-1.1/stage3/g/game.p1.mps gp1.mps
		ln -s /net1/poker/poker_data/52-5-1.1/stage3/g/game.p2.mps gp2.mps
		rm -f *.out
		./bpmpd ap1
		./bpmpd ap2
		./bpmpd bp1
		./bpmpd bp2
		./bpmpd cp1
		./bpmpd cp2
		./bpmpd dp1
		./bpmpd dp2
		./bpmpd ep1
		./bpmpd ep2
		./bpmpd fp1
		./bpmpd fp2
		./bpmpd gp1
		./bpmpd gp2
		cp ap1.out /net1/poker/poker_data/52-5-1.1/stage3/a/game.p1.sol
		cp ap2.out /net1/poker/poker_data/52-5-1.1/stage3/a/game.p2.sol
		cp bp1.out /net1/poker/poker_data/52-5-1.1/stage3/b/game.p1.sol
		cp bp2.out /net1/poker/poker_data/52-5-1.1/stage3/b/game.p2.sol
		cp cp1.out /net1/poker/poker_data/52-5-1.1/stage3/c/game.p1.sol
		cp cp2.out /net1/poker/poker_data/52-5-1.1/stage3/c/game.p2.sol
		cp dp1.out /net1/poker/poker_data/52-5-1.1/stage3/d/game.p1.sol
		cp dp2.out /net1/poker/poker_data/52-5-1.1/stage3/d/game.p2.sol
		cp ep1.out /net1/poker/poker_data/52-5-1.1/stage3/e/game.p1.sol
		cp ep2.out /net1/poker/poker_data/52-5-1.1/stage3/e/game.p2.sol
		cp fp1.out /net1/poker/poker_data/52-5-1.1/stage3/f/game.p1.sol
		cp fp2.out /net1/poker/poker_data/52-5-1.1/stage3/f/game.p2.sol
		cp gp1.out /net1/poker/poker_data/52-5-1.1/stage3/g/game.p1.sol
		cp gp2.out /net1/poker/poker_data/52-5-1.1/stage3/g/game.p2.sol
		cd /net1/poker
		java -Xms400M -Xmx1700M -Xincgc -classpath bin stage3.DoParseClpOutput all-subtrees
		```


1. Build your player data files around the resulting mixed strategy
	* Run `stage4.DoConvertSolsForRndAccess root`
	* Run `stage4.DoConvertSolsForRndAccess all-subtrees`

1. Use your player
	* Use the `stage4.MeerkatPlayer` class
	* I originally used the player in PokerAcademy but it now seems that there are open source platforms that can use Meerkat players.


### License

This code is released under the public domain / no license.
