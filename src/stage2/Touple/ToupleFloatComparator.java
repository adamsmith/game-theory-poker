/*
 * Created on Jun 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage2.Touple;

import java.util.Comparator;
import java.util.Map;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ToupleFloatComparator implements Comparator {

	public int compare(Object arg0, Object arg1) {
		// these are ToupleFloatInts...sort on float value
		float thisV = ((ToupleFloatInt) arg0).o1;
		float thatV = ((ToupleFloatInt) arg1).o1;
		if(thisV < thatV) {
			return -1;
		} else if(thisV > thatV) {
			return 1;
		} else {
			return 0;
		}
	}
}
