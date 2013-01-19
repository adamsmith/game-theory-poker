package _game;




public class Card {

	// 52
//	public final static String GAME_NAME = "52-card holdem";
//	public final static byte NUM_SUITS = 4;
//	public final static byte NUM_RANKS = 13;
//	public final static byte NUM_CARDS = NUM_SUITS * NUM_RANKS; //52
//	public final static int ACE = NUM_RANKS - 1; // for normal deck ACE=12 (TWO=0)
//	public static final byte[] ALLCARDSINDEX = new byte[] {
//			0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 
//			11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 
//			21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 
//			31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 
//			41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 
//			51};
//	public final static byte[] NUM_CLUSTERS = new byte[] {7, -1, -1, 7, 7, 7};

//	// 52
//	public final static String GAME_NAME = "52-card holdem lite6";
//	public final static byte NUM_SUITS = 4;
//	public final static byte NUM_RANKS = 13;
//	public final static byte NUM_CARDS = NUM_SUITS * NUM_RANKS; //52
//	public final static int ACE = NUM_RANKS - 1; // for normal deck ACE=12 (TWO=0)
//	public static final byte[] ALLCARDSINDEX = new byte[] {
//			0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 
//			11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 
//			21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 
//			31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 
//			41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 
//			51};
//	public final static byte[] NUM_CLUSTERS = new byte[] {6, -1, -1, 6, 6, 6};

	// 52
//	public final static String GAME_NAME = "52-card holdem lite5";
//	public final static byte NUM_SUITS = 4;
//	public final static byte NUM_RANKS = 13;
//	public final static byte NUM_CARDS = NUM_SUITS * NUM_RANKS; //52
//	public final static int ACE = NUM_RANKS - 1; // for normal deck ACE=12 (TWO=0)
//	public static final byte[] ALLCARDSINDEX = new byte[] {
//			0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 
//			11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 
//			21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 
//			31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 
//			41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 
//			51};
//	public final static byte[] NUM_CLUSTERS = new byte[] {5, -1, -1, 5, 5, 5};

	// 52
//	public final static String GAME_NAME = "52-5-1.1";
//	public final static byte NUM_SUITS = 4;
//	public final static byte NUM_RANKS = 13;
//	public final static byte NUM_CARDS = NUM_SUITS * NUM_RANKS; //52
//	public final static int ACE = NUM_RANKS - 1; // for normal deck ACE=12 (TWO=0)
//	public static final byte[] ALLCARDSINDEX = new byte[] {
//			0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 
//			11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 
//			21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 
//			31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 
//			41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 
//			51};
//	public final static byte[] NUM_CLUSTERS = new byte[] {5, -1, -1, 5, 5, 5};

	
	// 24
//	public final static String GAME_NAME = "24-card holdem";
//	public final static byte NUM_SUITS = 2;
//	public final static byte NUM_RANKS = 12;
//	public final static byte NUM_CARDS = NUM_SUITS * NUM_RANKS; //14
//	public final static int ACE = NUM_RANKS - 1; // for normal deck ACE=12 (TWO=0)
//	public final static byte[] ALLCARDSINDEX = new byte[] {
//			0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 
//			11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 
//			21, 22, 23};
//	public final static byte[] NUM_CLUSTERS = new byte[] {7, -1, -1, 7, 7, 7};
	
	
	// 24-lite
//	public final static String GAME_NAME = "24-card holdem lite";
//	public final static byte NUM_SUITS = 2;
//	public final static byte NUM_RANKS = 12;
//	public final static byte NUM_CARDS = NUM_SUITS * NUM_RANKS; //14
//	public final static int ACE = NUM_RANKS - 1; // for normal deck ACE=12 (TWO=0)
//	public final static byte[] ALLCARDSINDEX = new byte[] {
//			0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 
//			11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 
//			21, 22, 23};
//	public final static byte[] NUM_CLUSTERS = new byte[] {3, -1, -1, 3, 3, 3};
	
	
	// 14
	public final static String GAME_NAME = "14-card holdem";
	public final static byte NUM_SUITS = 2;
	public final static byte NUM_RANKS = 7;
	public final static byte NUM_CARDS = NUM_SUITS * NUM_RANKS; //14
	public final static int ACE = NUM_RANKS - 1; // for normal deck ACE=12 (TWO=0)
	public static final byte[] ALLCARDSINDEX = new byte[] {
			0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 
			11, 12, 13};
	public final static byte[] NUM_CLUSTERS = new byte[] {3, -1, -1, 3, 3, 3};
	
	
	
}



/* fully explicit card to integer conversions :

2c =  0    2d = 13    2h = 26    2s = 39
3c =  1    3d = 14    3h = 27    3s = 40
4c =  2    4d = 15    4h = 28    4s = 41
5c =  3    5d = 16    5h = 29    5s = 42
6c =  4    6d = 17    6h = 30    6s = 43
7c =  5    7d = 18    7h = 31    7s = 44
8c =  6    8d = 19    8h = 32    8s = 45
9c =  7    9d = 20    9h = 33    9s = 46
Tc =  8    Td = 21    Th = 34    Ts = 47
Jc =  9    Jd = 22    Jh = 35    Js = 48
Qc = 10    Qd = 23    Qh = 36    Qs = 49
Kc = 11    Kd = 24    Kh = 37    Ks = 50
Ac = 12    Ad = 25    Ah = 38    As = 51

*/