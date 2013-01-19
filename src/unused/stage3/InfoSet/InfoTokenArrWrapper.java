/*
 * Created on Jul 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3.InfoSet;
import stage3.InfoSet.*;
import java.io.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InfoTokenArrWrapper implements Serializable {

	public final InfoToken[] itArray;
	
	public InfoTokenArrWrapper(InfoToken[] x) {
		itArray = x;
	}
	
	public int hashCode() {
		int result = 0;
		for(int i = 0; i < itArray.length; i++) {
			result ^= itArray[i].hashCode();
		}
		
		return result;
	}
	
	public boolean equals(Object obj) {
		InfoTokenArrWrapper other = (InfoTokenArrWrapper) obj;
		if(other.itArray.length != itArray.length) {
			return false;
		}
		for(int i = 0; i < itArray.length; i++) {
			if(!other.itArray[i].equals(itArray[i])) {
				return false;
			}
		}
		return true;
	}
	
}
