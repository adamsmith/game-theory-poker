/*
 * Created on Jun 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3;

import java.util.*;
import java.io.*;
import stage3.InfoSet.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NameMap implements Serializable {

//	private static NameMap singletonNameMapP1;
//	private static NameMap singletonNameMapP2;

	public Map longToShort = new HashMap();
	public Map shortToLong = new HashMap();
	int nextShortName;
	public final int emptySequenceName;
	
//	public static NameMap getNameMapP1() {
//		if(singletonNameMapP1 == null) {
//			singletonNameMapP1 = new NameMap();
//		}
//		return singletonNameMapP1;
//	}
//	
//	public static NameMap getNameMapP2() {
//		if(singletonNameMapP2 == null) {
//			singletonNameMapP2 = new NameMap();
//		}
//		return singletonNameMapP2;
//	}
	
	public NameMap() {
		nextShortName = 0;
		emptySequenceName = getShort(InfoString.emptyInfoString, true);
	}
	
	public int numUniqueNames() {
		return nextShortName;
	}
	
	public void printLongToShort() {
		for(Iterator i = longToShort.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry entry = (Map.Entry) i.next();
			System.out.println(entry.getKey() + " --> " + entry.getValue());
		}
	}
	
	public InfoString getLong(int x) {
		return (InfoString) shortToLong.get(new Integer(x));
	}
	
	public int getShort(InfoString longName, boolean addIfAbsent) {
		Integer shortName = (Integer) longToShort.get(longName);
		int intShortName;
		if(shortName == null) {
			if(addIfAbsent) {
				intShortName = nextShortName++;
				shortName = new Integer(intShortName);
				longToShort.put(longName, shortName);
				shortToLong.put(shortName, longName);
			} else {
				return -1;
			}
		} else {
			intShortName = shortName.intValue();
		}
		return intShortName;
	}
}
