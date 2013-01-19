/*
 * Created on Jun 4, 2005
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
public class testHandEvaluator {

	private static final byte C2c = 0;
	private static final byte C3c = 1;
	private static final byte C4c = 2;
	private static final byte C5c = 3;
	private static final byte C6c = 4;
	private static final byte C7c = 5;
	private static final byte C8c = 6;
	private static final byte C9c = 7;
	private static final byte CTc = 8;
	private static final byte CJc = 9;
	private static final byte CQc = 10;
	private static final byte CKc = 11;
	private static final byte CAc = 12;
	
	private static final byte C2d = 13;
	private static final byte C3d = 14;
	private static final byte C4d = 15;
	private static final byte C5d = 16;
	private static final byte C6d = 17;
	private static final byte C7d = 18;
	private static final byte C8d = 19;
	private static final byte C9d = 20;
	private static final byte CTd = 21;
	private static final byte CJd = 22;
	private static final byte CQd = 23;
	private static final byte CKd = 24;
	private static final byte CAd = 25;
	
	private static final byte C2h = 26;
	private static final byte C3h = 27;
	private static final byte C4h = 28;
	private static final byte C5h = 29;
	private static final byte C6h = 30;
	private static final byte C7h = 31;
	private static final byte C8h = 32;
	private static final byte C9h = 33;
	private static final byte CTh = 34;
	private static final byte CJh = 35;
	private static final byte CQh = 36;
	private static final byte CKh = 37;
	private static final byte CAh = 38;
	
	private static final byte C2s = 39;
	private static final byte C3s = 40;
	private static final byte C4s = 41;
	private static final byte C5s = 42;
	private static final byte C6s = 43;
	private static final byte C7s = 44;
	private static final byte C8s = 45;
	private static final byte C9s = 46;
	private static final byte CTs = 47;
	private static final byte CJs = 48;
	private static final byte CQs = 49;
	private static final byte CKs = 50;
	private static final byte CAs = 51;

	public static void main(String[] args) {
		
		byte[] board = new byte[7];

		board[0] = C2d;
		board[1] = CAh;
		board[2] = CAc;
		board[3] = C9h;
		board[4] = C9c;
		board[5] = C2h;
		board[6] = C3h;
		System.out.println(HandEvaluator.rankHand_Java(board));
		
		board[0] = C2d;
		board[1] = CAs;
		board[2] = CAd;
		board[3] = C9h;
		board[4] = C9c;
		board[5] = C2h;
		board[6] = C4d;
		System.out.println(HandEvaluator.rankHand_Java(board));
		
		board[0] = CAs;
		board[1] = CKs;
		board[2] = CQs;
		board[3] = CJs;
		board[4] = CTs;
		board[5] = C2h;
		board[6] = C4d;
		System.out.println(HandEvaluator.rankHand_Java(board));
		
		board[0] = C2d;
		board[1] = C3h;
		board[2] = C4s;
		board[3] = C5c;
		board[4] = C7d;
		board[5] = C8h;
		board[6] = C9s;
		System.out.println(HandEvaluator.rankHand_Java(board));
	}
}
