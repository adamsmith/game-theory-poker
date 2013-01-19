package _misc;

import java.math.BigInteger;
/**
 * The class Combinatoric contains methods for performing basic combinatoric operations
 * such as counting numbers of permutations and combinations.
 *
 * @author dave_hoag
 * @version $Id: Combinatoric.java,v 2.1 2002/08/10 15:12:24 dave_hoag Exp $
 */
public class Combinatoric
{
	/**
	 * @param n int
	 * @param m int
	 * @return BigInteger, the number of unordered subsets of m objects chosen from a group
	 *      of n objects.
	 * @exception Exception unless n >= m >= 0
	 */
	public static BigInteger c( int n, int m )
	{
		check( n, m );
		int r = Math.min( m, n - m );
		return p( n, r ).divide( factorial( r ) );
	}
	/**
	 * Check that 0 <= m <= n
	 *
	 * @param n int
	 * @param m int
	 * @exception Exception unless n >= m >= 0
	 */
	static void check( int n, int m )
	{
		if( n < 0 )
		{
			throw new RuntimeException( "n, the number of items, must be greater than 0" );
		}
		if( n < m )
		{
			throw new RuntimeException( "n, the number of items, must be >= m, the number selected" );
		}
		if( m < 0 )
		{
			throw new RuntimeException( "m, the number of selected items, must be >= 0" );
		}
	}
	/**
	 * @param n int
	 * @return BigInteger, the product of the numbers 1 ... n
	 * @exception Exception unless n >= 0
	 */
	public static BigInteger factorial( int n )
	{
		if( n < 0 )
		{
			throw new RuntimeException( "n must be >= 0" );
		}

		BigInteger factorial = new BigInteger( new byte[]{1} );
		for( int i = n; i > 1; i-- )
		{
			factorial =
					factorial.multiply( new BigInteger( new byte[]{( byte ) i} ) );
		}
		return factorial;
	}
	/**
	 * @param n int
	 * @return BigInteger, the number of possible ways of ordering n objects
	 * @exception Exception unless n >= 0
	 */
	public static BigInteger p( int n )
	{
		return factorial( n );
	}
	/**
	 * @param n int
	 * @param m int
	 * @return BigInteger, the number of possible arrangements, or orderings, of m objects
	 *      chosen from a group of n objects.
	 * @exception Exception unless n >= m >= 0
	 */
	public static BigInteger p( int n, int m )
	{
		check( n, m );

		BigInteger product = new BigInteger( new byte[]{1} );
		for( int i = n; i > n - m; i-- )
		{
			product =
					product.multiply( new BigInteger( new byte[]{( byte ) i} ) );
		}
		return product;
	}
}
