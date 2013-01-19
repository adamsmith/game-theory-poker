/*
 * Created on Jul 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stage3.InfoSet;

import _misc.*;
import java.io.*;
import java.util.*;

/**
 * @author Adam
 *
 * Immutable.
 */
public class InfoString implements Serializable {
	
	public final static InfoString emptyInfoString = new InfoString(new byte[0]);

	public final byte[] arr;
	
	private int m_hashCode = -1;
	private boolean m_hashCodeSet = false;
	
	public InfoString(byte[] x) {
		arr = x;
	}
	
	public int hashCode() {
		if(!m_hashCodeSet) {
			m_hashCode = Helper.hashByteArray(arr);
		}
		return m_hashCode;
	}
	
	public boolean equals(Object obj) {
		InfoString other = (InfoString) obj;
		if(other.arr.length != arr.length) {
			return false;
		}
		for(int i = arr.length-1; i >= 0 ; i--) {
			if(other.arr[i] != arr[i]) {
				return false;
			}
		}
		return true;
	}
	
	public byte getLastElement() {
		return arr[arr.length-1];
	}
	
	public InfoString duplicate() {
		InfoString y = new InfoString(new byte[this.arr.length]);
		System.arraycopy(this.arr, 0, y.arr, 0, this.arr.length);
		return y;
	}
	
	public InfoString prepend(byte newBeginning) {
		InfoString y = new InfoString(new byte[this.arr.length+1]);
		System.arraycopy(this.arr, 0, y.arr, 1, this.arr.length);
		y.arr[0] = newBeginning;
		return y;
	}
	
	public InfoString push(byte toAppend) {
		InfoString y = new InfoString(new byte[this.arr.length+1]);
		System.arraycopy(this.arr, 0, y.arr, 0, this.arr.length);
		y.arr[this.arr.length] = toAppend;
		return y;
	}
	
	public InfoString pop() {
		InfoString y = new InfoString(new byte[this.arr.length-1]);
		System.arraycopy(this.arr, 0, y.arr, 0, this.arr.length-1);
		return y;
	}
	
	public String toString() {
		String y = "";
		for(int i = 0; i < arr.length; i++) {
			y = y.concat(InfoToken.toString(arr[i]));
		}
		return y;
	}
	
}
