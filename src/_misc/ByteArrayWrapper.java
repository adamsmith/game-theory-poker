/*
 * Created on Jun 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _misc;

import java.util.Arrays;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ByteArrayWrapper {
	public byte[] array;
	
	public ByteArrayWrapper(byte[] array) {
		this.array = array;
	}
	
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(!(obj instanceof ByteArrayWrapper)) {
			return false;
		}
		return Arrays.equals(array, ((ByteArrayWrapper)obj).array);
	}
	
	public int hashCode() {
		return Helper.hashByteArray(array);
	}
}
