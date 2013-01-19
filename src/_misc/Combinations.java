package _misc;


/**
 * The Combinations class provides an enumeration of all subsets of a group of 
 * n objects taken r at a time. The constructor for Combinations accepts the group as 
 * an array of Objects, along with the number to select.
 * <p>
 * For example, to choose 3 boys from a list of 5, begin with an array
 * of names: 
 * <p>
 * <blockquote><pre>
 * Object[] boys = {Alfred, Ben, Carl, Drew, Edwin};
 * </pre></blockquote>
 * To see all combinations of these 5 names taken 3 at a time, create 
 * and use a Combinations enumeration: 
 * <blockquote><pre>
 *  Combinations c = new Combinations(boys, 3);
 * while (c.hasMoreElements()) {
 *  Object[] combo = (Object[])c.nextElement();
 *  for (int i = 0; i < combo.length; i++) {
 *   System.out.print((String)combo[i] +  );
 * }
 * System.out.println();
 * }
 * </pre></blockquote>
 * <p>
 * This will print out a 10 line list: 
 * <blockquote><pre>
 * Alfred Ben Carl 
 * Alfred Ben Drew 
 * Alfred Ben Edwin 
 * Alfred Carl Drew 
 * Alfred Carl Edwin 
 * Alfred Drew Edwin 
 * Ben Carl Drew 
 * Ben Carl Edwin 
 * Ben Drew Edwin 
 * Carl Drew Edwin 
 * </pre></blockquote>
 * 
 */
public class Combinations
{
    private byte[] inArray;
    private int n, m;
    private int[] index;
    private boolean hasMore = true;
    
    public Combinations getCopy() {
    	Combinations yo = new Combinations(inArray, m); // assume inArray immutable
    	yo.n = this.n;
    	yo.index = new int[m];
    	for(int i = 0; i < m; i++) {
    		yo.index[i] = this.index[i];
    	}
    	yo.hasMore = this.hasMore;
    	return yo;
    }
    
    
    /**
    * Create a Combination to enumerate through all subsets of the 
    * supplied Object array, selecting m at a time.
    *
    * @param Object[] inArray the group to choose from
    * @param m int the number to select in each choice
    * @exception CombinatoricException if m is greater than 
    * the length of inArray, or less than 0.
    */
    public Combinations(byte[] inArray, int m)
    {
        this.inArray = inArray;
        this.n = inArray.length;
        this.m = m;

        // throw exception unless n >= m >= 0
        Combinatoric.check(n, m);

        /**
        * index is an array of ints that keep track of the next combination to return. 

        * For example, an index on 5 things taken 3 at a time might contain {0 3 4}. 
        * This index will be followed by {1 2 3}. Initially, the index is {0 ... m - 1}.
        */
        index = new int[m];
        for (int i = 0; i < m; i++)
            index[i] = i;
    }
    /**
    * @return true, unless we have already returned the last combination.
    */
    public boolean hasMoreElements()
    { 
        return hasMore;
    }
    /**
    * Move the index forward a notch. The algorithm finds the rightmost
    * index element that can be incremented, increments it, and then 
    * changes the elements to the right to each be 1 plus the element on their left. 
    * <p>
    * For example, if an index of 5 things taken 3 at a time is at {0 3 4}, only the 0 can
    * be incremented without running out of room. The next index is {1, 1+1, 1+2) or
    * {1, 2, 3}. This will be followed by {1, 2, 4}, {1, 3, 4}, and {2, 3, 4}.
    * <p>
    * The algorithm is from Applied Combinatorics, by Alan Tucker.
    *
    */
    private void moveIndex()
    {
        int i = rightmostIndexBelowMax();
        if (i >= 0)
        {    
            index[i] = index[i] + 1; 
            for (int j = i + 1; j < m; j++)
                index[j] = index[j-1] + 1;
        }
        else
            hasMore = false;

    }
    /**
    * @return java.lang.Object, the next combination from the supplied Object array. 
    * <p>
    * Actually, an array of Objects is returned. The declaration must say just Object,
    * because the Combinations class implements Enumeration, which declares that the
    * nextElement() returns a plain Object. Users must cast the returned object to (Object[]).
    */
    public byte[] nextElement()
    {
        if (!hasMore)
            return null;

        byte[] out = new byte[m];
        for (int i = 0; i < m; i++)
            out[i] = inArray[index[i]];

        moveIndex();
        return out;
    }
    /**
    * @return int, the index which can be bumped up.
    */
    private int rightmostIndexBelowMax()
    {
        for (int i = m - 1; i >= 0; i--)
        {
            if (index[i] < n - m + i)
            return i;
        }
        return -1;
    }
}