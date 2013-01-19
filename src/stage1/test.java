/*
 * Created on May 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage1;

import _io.*;
import _misc.*;
import stage3.*;
import stage3.InfoSet.*;


/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class test {

	public static void main(String[] args) throws Exception {
		
//		HandRecord yo = new HandRecord(3, new byte[] {3, 10});
		
//		yo.advanceRecord();
//		yo.advanceRecord();

//		Combinations combo = new Combinations(Card.ALLCARDSINDEX, 0);
//		System.out.println(combo.hasMoreElements());
//		byte[] boardCards = combo.nextElement();
//		System.out.println(boardCards[1]);
		
//		double timer1 = System.currentTimeMillis();
//		for(int i = 0; i < 10000000; i++) {
//			int a = i % 20;
//		}
//		System.out.println(System.currentTimeMillis() - timer1);
//		timer1 = System.currentTimeMillis();
//		byte[] a = new byte[] {0,0,0,0,0};
//		for(int i = 0; i < 10000000; i++) {
//			for(int j = 0; j < 5; j++) {
//				if(a[j] != 0) {
//					throw new RuntimeException();
//				}
//			}
//		}
//		System.out.println(System.currentTimeMillis() - timer1);
		

//		int[] a = new int[] { 0, 1, 2, 5 };
//		int[] b = new int[] { 0, 1, 2, 5 };
//		Map yob = new HashMap();
//		yob.put(new Integer(Arrays.hashCode(a)), new String("yoo"));
//		yob.put(new Integer(Arrays.hashCode(b)), new String("hii"));
//		throw new RuntimeException();
		
		
//		Combinations combo = new Combinations(Card.ALLCARDSINDEX, 3);
//		Map yo = new HashMap();
//		while(combo.hasMoreElements()) {
//			byte[] bt = combo.nextElement();
//			int hashcode = ScoreMaps.getComboArrayIndex(52,3,bt);
//			if(yo.containsKey(new Integer(hashcode))) {
//				throw new RuntimeException();
//			} else {
//				yo.put(new Integer(hashcode), null);
//			}
//		}
		
		

//		String outFile4 = "c:" + Constants.dirSep + "ftproot" + Constants.dirSep + "4" + Constants.dirSep + "18_20";
//		try {
//			double timer1 = System.currentTimeMillis();
//			ScoreMaps ver = ReadBinaryScoreMaps.getScoreMap(outFile4, 4);
//			System.out.println(System.currentTimeMillis() - timer1);
//			ver.print();
//		} catch (IOException e) {
//			throw new RuntimeException();
//		}
//		
//		System.out.println("done");
		
		
//		Combinations combo = new Combinations(Card.ALLCARDSINDEX, 2);
//		for(int i = 0; i < 1326/2; i++) {
//			combo.nextElement();
//		}
//		byte[] yo = combo.nextElement();
//		System.out.println(yo[0] + "-" + yo[1]);
		
//		System.out.println(Constants.choose(Card.NUM_CARDS-5, 2));
		
//		String yo = "c:" + Constants.dirSep + "yo" + Constants.dirSep + "se" + Constants.dirSep + "hi.txt";
//		System.out.println();

//		System.out.println(java.util.Arrays.equals((new byte[] {1,11}),(new byte[]{1,11})));
//		System.out.println(java.util.Arrays.equals((new byte[] {1,11}),(new byte[]{1,11})));
		
//		NameMap nm = ReadBinaryNameMap.getScoreMap(Constants.DATA_FILE_REPOSITORY + 
//				"stage3" + Constants.dirSep + "nameMap.obj");
//		nm.printLongToShort();

//		double[] vals = new double[] {0.00001, -0.00001, -4.9906273, 1, 1.0, -1, -1.0, -0.0, .1, -.1};
//		for(int i = 0; i < vals.length; i++) {
//			float randomVal = (float)vals[i];//(float) (Math.random()*.01*(Math.random() > .5 ? 1 : -1));
//			String str = WriteMPS.floatToTrimString(randomVal);
//			if(Math.abs(randomVal) < 0.001) {
//				System.out.println("hey: " + randomVal + " --> " + str + " (" + str.length() + ")");
//			} else {
//				System.out.println(str + " " + str.length());
//			}
//		}
		
//		int yo = Integer.MAX_VALUE;
//		System.out.println(yo + " -- " + overflowedIntVal(yo));
//		yo++;
//		System.out.println(yo + " -- " + overflowedIntVal(yo));
//		yo++;
//		System.out.println(yo + " -- " + overflowedIntVal(yo));
		
//		int[] x = new int[] {-970596808,
//				-636909994,
//				1091131248,
//				209120,
//				9006068,
//				1274612874,
//				...
//		};
//		long[][][][] y = new long[7][7][7][7];
//		long sum = 0;
//		int p = 0;
//		for(int i = 0; i < 7; i++) {
//			for(int j = 0; j < 7; j++) {
//				for(int k = 0; k < 7; k++) {
//					for(int l = 0; l < 7; l++) {
//						y[i][j][k][l] = Helper.overflowedIntVal(x[p++]);
//						sum += y[i][j][k][l];
////						System.out.println(y[i][j][k][l]);
//					}
//				}
//			}
//		}
//		if(p != x.length) {
//			throw new RuntimeException();
//		}
//		System.out.println(sum);
		
//		String inDir = "Z:\\poker\\poker_data\\24-card holdem lite\\stage3\\root\\";
//
//		ConstraintMatrix cmP1 = (ConstraintMatrix)((Object[])ReadBinaryConstraintMatrix.getCm(inDir + "constraints.p1.obj"))[0];
//		ConstraintMatrix cmP2 = (ConstraintMatrix)((Object[])ReadBinaryConstraintMatrix.getCm(inDir + "constraints.p2.obj"))[0];
//
//		ConstraintMatrixColumn yo = cmP2.getColumn(new Integer(109));
//		
//		ConstraintMatrixRow r =cmP2.getRow(34);
		
//		cmP1.printColumnMatrix();
//		System.out.println("");
//		System.out.println("");
//		System.out.println("");
//		cmP2.printColumnMatrix();
		
//		NameMap nm1 = ReadBinaryNameMap.getNameMap("Z:\\poker\\poker_data\\52-card holdem\\stage3\\root\\nameMap.p1.obj");
//		NameMap nm2 = ReadBinaryNameMap.getNameMap("Z:\\poker\\poker_data\\52-card holdem\\stage3\\root\\nameMap.p2.obj");
//		System.out.println(nm.getLong(0));
//		System.out.println(nm1.getShort(new InfoString(new byte[] {
//				InfoToken.factory((byte)0, DoGT.s_player1, (byte)0, true),
//				InfoToken.factory((byte)0, (byte)0, InfoToken.s_call, false)})));
		

//		System.out.println(nm1.getLong(1));
//		System.out.println(nm1.getLong(698));
//		System.out.println(nm1.getLong(109));
//		System.out.println(nm2.getLong(1179));
//		System.out.println("");
//		System.out.println(nm1.getLong(61));
//		System.out.println(nm1.getLong(9));
//		System.out.println(nm1.getLong(16488));
		

//		System.out.println("");
//		System.out.println(nm2.getLong(4));
//		System.out.println(nm1.getLong(16881));
//		System.out.println(nm.getLong(4711));
//		System.out.println(nm.getLong(8244));
//		System.out.println(nm.getLong(12954));
//		System.out.println(nm.getLong(16487));
//		System.out.println(nm.getLong(21197));

//		for(int i = 0; i < 50000000; i++) {
//			System.out.println(i);
//		}
		

		InfoString isTst = new InfoString(new byte[] {
				InfoToken.factory((byte)3, (byte)0, (byte)3, true), 
				InfoToken.factory((byte)3, (byte)0, InfoToken.s_call, false)});
		
		String inFile = Constants.DATA_FILE_REPOSITORY + "stage3" + Constants.dirSep
				+ "root" + Constants.dirSep + "nameMap.p1.obj";

		NameMap nm = ReadBinaryNameMap.getNameMap(inFile);
		
		InfoString yo = (InfoString) nm.shortToLong.get(new Integer(22552));
		System.out.println(yo);
		System.out.println(Helper.byteArrayToString(yo.arr));
//		System.out.println(nm.getShort(isTst, false));
	}
}
