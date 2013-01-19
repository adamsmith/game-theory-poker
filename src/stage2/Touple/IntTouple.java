/*
 * Created on Jun 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage2.Touple;

import java.util.*;
import _misc.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IntTouple {

	public int[] x;
	
	public IntTouple(int[] x) {
		this.x = x;
	}
	
	public int hashCode() {
		return Helper.hashIntArray(x);
	}
	
	public boolean equals(Object obj) {
		// assume obj is an IntTouple
		return Arrays.equals(x, ((IntTouple) obj).x);
	}

}
