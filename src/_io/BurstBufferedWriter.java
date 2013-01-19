/*
 * Created on Jun 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BurstBufferedWriter {

	String fileName;
	ByteBuffer buf;
	
	int bufferSize;
	
	public BurstBufferedWriter(String fileName, int bufferSize) throws IOException {
		this.fileName = fileName;
		this.bufferSize = bufferSize;
		
		int initSize = (int) Math.floor(Math.random() * bufferSize);
		
		buf = ByteBuffer.allocate(initSize);
	}
	
	public void writeShort(short x) throws IOException  {
		try {
			buf.putShort(x);
		} catch (BufferOverflowException boe) {
			recycleBuffer();
			buf.putShort(x);
		}
	}
	
	public void writeByte(byte x) throws IOException  {
		try {
			buf.put(x);
		} catch (BufferOverflowException boe) {
			recycleBuffer();
			buf.put(x);
		}
	}
	
	public void writeInt(int x) throws IOException  {
		try {
			buf.putInt(x);
		} catch (BufferOverflowException boe) {
			recycleBuffer();
			buf.putInt(x);
		}
	}
	
	public void writeLong(long x) throws IOException  {
		try {
			buf.putLong(x);
		} catch (BufferOverflowException boe) {
			recycleBuffer();
			buf.putLong(x);
		}
	}
	
	public void writeDouble(double x) throws IOException  {
		try {
			buf.putDouble(x);
		} catch (BufferOverflowException boe) {
			recycleBuffer();
			buf.putDouble(x);
		}
	}
	
	public void writeFloat(float x) throws IOException  {
		try {
			buf.putFloat(x);
		} catch (BufferOverflowException boe) {
			recycleBuffer();
			buf.putFloat(x);
		}
	}
	
	private void recycleBuffer() throws IOException {
		FileOutputStream foe = new FileOutputStream(fileName, true); // append mode!
		FileChannel fc = foe.getChannel();
		
		buf.flip();
		fc.write(buf);
		
		if(buf.hasRemaining()) {
			throw new RuntimeException();
		}
		
		fc.force(true);
		
		fc.close();
		foe.close();
		
		buf = ByteBuffer.allocate(bufferSize);
	}
	
	public void close() throws IOException {
		recycleBuffer();
		buf = null;
	}
	
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	
}
