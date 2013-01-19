package stage1;

import _game.Card;


public class HandEvaluator {

/**********************************************************************/
// DENIS PAPP'S HAND RANK IDENTIFIER CODE:
/**********************************************************************/

	private static final int POKER_HAND = 5;

	private static final int  HIGH = 0;
	private static final int  PAIR = 1;
	private static final int  TWOPAIR = 2;
	private static final int  THREEKIND = 3;
	private static final int  STRAIGHT = 4;
	private static final int  FLUSH = 5;
	private static final int  FULLHOUSE = 6;
	private static final int  FOURKIND = 7;
	private static final int  STRAIGHTFLUSH = 8;
	private static final int  FIVEKIND = 9;
	
	private static final int  ID_GROUP_SIZE  = (Card.NUM_RANKS*Card.NUM_RANKS*Card.NUM_RANKS*Card.NUM_RANKS*Card.NUM_RANKS);
	
	private static boolean ID_ExistsStraightFlush(byte[] h, Byte straight_high, byte major_suit) {
	        int i;
	        int straight;
	        byte high;
	        boolean[] present = new boolean[Card.NUM_RANKS];
	        for (i=0;i<Card.NUM_RANKS;i++) present[i]=false;

	        for (i=0;i<h.length;i++)
			if (HandEvaluator.getSuit(h[i]) == major_suit)
	        	        present[HandEvaluator.getRank(h[i])] = true;

	        straight = present[Card.ACE] ? 1 : 0;
	        high = 0;
	        for (i=0;i<Card.NUM_RANKS;i++) {
	                if (present[i]) {
	                        if ( (++straight) >= POKER_HAND)
	                                high = (byte)i;
	                } else straight = 0;
	        }
	        if (high == 0) return false;
	        straight_high = new Byte(high);
	        return true;
	}

	// suit: Card.NUM_SUITS means any
	// not_allowed: Card.NUM_RANKS means any
	// returns ident value
	private static int ID_KickerValue(byte[] paired, int kickers, byte[] not_allowed) {
	        int i = Card.ACE;
	        int value=0;
	        while (kickers != 0) {
	                while ( paired[i]==0 || i==not_allowed[0] || i==not_allowed[1])
	                        i--;
	                kickers--;
	                value+=pow(Card.NUM_RANKS,kickers)*i;
	                i--;
	        }
	        return value;
	}

	private static int ID_KickerValueSuited(byte[] h, int kickers, byte suit) {
	        int i;
	        int value=0;

	        boolean[] present = new boolean[Card.NUM_RANKS];
	        for (i=0;i<Card.NUM_RANKS;i++) present[i] = false;

	        for (i=0;i<h.length;i++)
	 	       if (HandEvaluator.getSuit(h[i]) == suit)
	                	present[HandEvaluator.getRank(h[i])] = true;

	        i = Card.ACE;
	        while (kickers != 0) {
	                while (present[i] == false) i--;
	                kickers--;
	                value += pow(Card.NUM_RANKS,kickers)*i;
	                i--;
	        }
	        return value;
	}
	 
	/**
	 * Get a numerical ranking of this hand.
	 * Uses java based code, so may be slower than using the native
	 * methods, but is more compatible this way.
	 *
	 * Based on Denis Papp's Loki Hand ID code (id.cpp)
	 * Given a 1-9 card hand, will return a unique rank 
 	 * such that any two hands will be ranked with the 
 	 * better hand having a higher rank. 
	 *
	 * @param h a 1-9 card hand
	 * @return a unique number representing the hand strength of the best 
	 * 5-card poker hand in the given 7 cards. The higher the number, the better
	 * the hand is.
	 */
	public static int rankHand_Java(byte[] h) {
	        boolean straight = false;
	        boolean flush = false;
	        byte max_hand = (byte)(h.length >= POKER_HAND ? POKER_HAND : h.length);
			int r,c;
	        byte rank,suit;
	        
			// pair data
	        byte[] group_size = new byte[POKER_HAND+1];
	        byte[] paired = new byte[Card.NUM_RANKS];
	        byte[][] pair_rank = new byte[POKER_HAND+1][2];
	        // straight
	        byte straight_high = 0;
	        byte straight_size;
	        // flush
	        byte[] suit_size = new byte[Card.NUM_SUITS];
	        byte major_suit = 0;

	        // determine pairs, dereference order data, check flush
	        for (r=0;r<Card.NUM_RANKS;r++) paired[r] = 0;
	        for (r=0;r<Card.NUM_SUITS;r++) suit_size[r] = 0;
	        for (r=0;r<=POKER_HAND;r++) group_size[r] = 0;
	        for (r=0;r<h.length;r++) {
	        	int tmpint = r;
	                rank = (byte)HandEvaluator.getRank(h[r]);
	                suit = (byte)HandEvaluator.getSuit(h[r]);
	               
	                paired[rank]++;
	                group_size[paired[rank]]++;
	                if (paired[rank] != 0)
	                        group_size[paired[rank]-1]--;
	                if ((++suit_size[suit]) >= POKER_HAND) {
	                        flush = true;
	                        major_suit = suit;
	                }
	        }
	        // Card.ACE low?
	        straight_size = (byte)(paired[Card.ACE] != 0 ? 1 : 0);

			for (int i=0;i<(POKER_HAND+1);i++) {
				pair_rank[i][0] = (byte)Card.NUM_RANKS;
				pair_rank[i][1] = (byte)Card.NUM_RANKS;
			}

	        // check for straight and pair data
	        for (r=0;r<Card.NUM_RANKS;r++) {
	                // check straight
	                if (paired[r]!=0) {
	                        if ( (++straight_size)>=POKER_HAND ) {
	                                straight = true;
	                                straight_high = (byte)r;
	                        }
	                } else
	                        straight_size = 0;

	                // get pair ranks, keep two highest of each
	                c = paired[r];
	                if ( c != 0 ) {
	                        pair_rank[c][1] = pair_rank[c][0];
	                        pair_rank[c][0] = (byte)r;
	                }
	        }

	        // now id type
	        int ident;
		
			Byte str_hi = new Byte(straight_high);
			
				
	        if (group_size[POKER_HAND]!=0) {
	    	    	ident = FIVEKIND*ID_GROUP_SIZE;
	                ident+=pair_rank[POKER_HAND][0];
	        } else if ( straight && flush && ID_ExistsStraightFlush(h,str_hi,major_suit)) {
	        		straight_high = str_hi.byteValue();
	    	    	ident = STRAIGHTFLUSH*ID_GROUP_SIZE;
	                ident+=straight_high;
	        } else if (group_size[4] != 0) {
	    	    	ident = FOURKIND*ID_GROUP_SIZE;
	                ident+=pair_rank[4][0]*Card.NUM_RANKS;
	                pair_rank[4][1] = (byte)Card.NUM_RANKS;    // just in case 2 sets quads
	                ident+=ID_KickerValue(paired,1,pair_rank[4]);
	        } else if (group_size[3]>=2) {
	    	    	ident = FULLHOUSE*ID_GROUP_SIZE;
	                ident+=pair_rank[3][0]*Card.NUM_RANKS;
	                ident+=pair_rank[3][1];
	        } else if (group_size[3]==1 && group_size[2]!=0) {
	    	    	ident = FULLHOUSE*ID_GROUP_SIZE;
	                ident+=pair_rank[3][0]*Card.NUM_RANKS;
	                ident+=pair_rank[2][0];
	        } else if (flush) {
	    	    	ident = FLUSH*ID_GROUP_SIZE;
	                ident+=ID_KickerValueSuited(h,5,major_suit);
	        } else if (straight) {
	    	    	ident = STRAIGHT*ID_GROUP_SIZE;
	                ident+=straight_high;
	        } else if (group_size[3]==1) {
	    	    	ident = THREEKIND*ID_GROUP_SIZE;
	                ident+=pair_rank[3][0]*Card.NUM_RANKS*Card.NUM_RANKS;
	                ident+=ID_KickerValue(paired,max_hand-3,pair_rank[3]);
	        } else if (group_size[2]>=2) {
	    	    	ident = TWOPAIR*ID_GROUP_SIZE;
	                ident+=pair_rank[2][0]*Card.NUM_RANKS*Card.NUM_RANKS;
	                ident+=pair_rank[2][1]*Card.NUM_RANKS;
	                ident+=ID_KickerValue(paired,max_hand-4,pair_rank[2]);
	        } else if (group_size[2]==1) {
	    	    	ident = PAIR*ID_GROUP_SIZE;
	                ident+=pair_rank[2][0]*Card.NUM_RANKS*Card.NUM_RANKS*Card.NUM_RANKS;
	                ident+=ID_KickerValue(paired,max_hand-2,pair_rank[2]);
	        } else {
	    	    	ident = HIGH*ID_GROUP_SIZE;
	                ident+=ID_KickerValue(paired,max_hand,pair_rank[2]);
	        }
	        return ident;
	}


	private static int pow(int n, int p) {
		int res=1;
        while (p-- > 0)
                res *= n;
        return res;
	}

	public static byte getRank(byte x) {
		return (byte)(x%Card.NUM_RANKS);
	}

	public static byte getSuit(byte x) {
		return (byte)(x/Card.NUM_RANKS);
	}
}
