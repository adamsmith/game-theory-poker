/*
 * Created on Jun 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _io;

import _misc.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BurstBufferedReader {

	private static final int BYTES_IN_DOUBLE = 8;
	private static final int BYTES_IN_FLOAT = 4;
	private static final int BYTES_IN_INT = 4;
	private static final int BYTES_IN_LONG = 8;
	private static final int BYTES_IN_SHORT = 2;

	String fileName;
	MappedByteBuffer buf;
	
	long fullFileSize;
	long nextPosition;
	int bufferSize;

	public BurstBufferedReader(String fileName, int bufferSize) throws IOException {
		this.fileName = fileName;
		this.bufferSize = bufferSize;
		
		File fileObj = new File(fileName);
		fullFileSize = fileObj.length();
		int initLength = (int) Math.floor(Math.random() * bufferSize);
		initLength = (int) Math.min(initLength, fullFileSize);
		this.nextPosition = initLength;
		
		FileInputStream fis = new FileInputStream(fileName);
		FileChannel fc = fis.getChannel();
		
		try {
			buf = fc.map(FileChannel.MapMode.READ_ONLY, 0, initLength);
		} catch (IOException ioe) {
//			System.out.println("!Map failed in BurstBufferedReader -- retrying");
			Helper.tryToFreeMemory();
			buf = fc.map(FileChannel.MapMode.READ_ONLY, 0, initLength);
		}
			
		if(!buf.isLoaded()) {
			buf.load();
		}
		
		fc.close();
		fis.close();
	}
	
	public byte readByte() throws IOException {
		try {
			return buf.get();
		} catch (BufferUnderflowException bue) {
			renewBuffer();
			return readByte();
		}
	}
	
	public short readShort() throws IOException {
		try {
			return buf.getShort();
		} catch (BufferUnderflowException bue) {
			byte[] leftOvers = renewBuffer();
			
			byte[] toReturn = new byte[BYTES_IN_SHORT];
			for(int i = 0; i < leftOvers.length; i++) {
				toReturn[i] = leftOvers[i];
			}
			for(int i = leftOvers.length; i < BYTES_IN_SHORT; i++) {
				toReturn[i] = buf.get();
			}
			
			return ByteBuffer.wrap(toReturn).getShort();
		}
	}
	
	public int readInt() throws IOException {
		try {
			return buf.getInt();
		} catch (BufferUnderflowException bue) {
			byte[] leftOvers = renewBuffer();
			
			byte[] toReturn = new byte[BYTES_IN_INT];
			for(int i = 0; i < leftOvers.length; i++) {
				toReturn[i] = leftOvers[i];
			}
			for(int i = leftOvers.length; i < BYTES_IN_INT; i++) {
				toReturn[i] = buf.get();
			}
			
			return ByteBuffer.wrap(toReturn).getInt();
		}
	}
	
	public long readLong() throws IOException {
		try {
			return buf.getLong();
		} catch (BufferUnderflowException bue) {
			byte[] leftOvers = renewBuffer();
			
			byte[] toReturn = new byte[BYTES_IN_LONG];
			for(int i = 0; i < leftOvers.length; i++) {
				toReturn[i] = leftOvers[i];
			}
			for(int i = leftOvers.length; i < BYTES_IN_LONG; i++) {
				toReturn[i] = buf.get();
			}
			
			return ByteBuffer.wrap(toReturn).getLong();
		}
	}
	
	public double readDouble() throws IOException {
		try {
			return buf.getDouble();
		} catch (BufferUnderflowException bue) {
			byte[] leftOvers = renewBuffer();
			
			byte[] toReturn = new byte[BYTES_IN_DOUBLE];
			for(int i = 0; i < leftOvers.length; i++) {
				toReturn[i] = leftOvers[i];
			}
			for(int i = leftOvers.length; i < BYTES_IN_DOUBLE; i++) {
				toReturn[i] = buf.get();
			}
			
			return ByteBuffer.wrap(toReturn).getDouble();
		}
	}
	
	public float readFloat() throws IOException {
		try {
			return buf.getFloat();
		} catch (BufferUnderflowException bue) {
			byte[] leftOvers = renewBuffer();
			
			byte[] toReturn = new byte[BYTES_IN_FLOAT];
			for(int i = 0; i < leftOvers.length; i++) {
				toReturn[i] = leftOvers[i];
			}
			for(int i = leftOvers.length; i < BYTES_IN_FLOAT; i++) {
				toReturn[i] = buf.get();
			}
			
			return ByteBuffer.wrap(toReturn).getFloat();
		}
	}
	
	// returns the left over bytes from the last buffer
	private byte[] renewBuffer() throws IOException {
		byte[] leftOvers = new byte[buf.remaining()];
		for(int i = 0; i < leftOvers.length; i++) {
			leftOvers[i] = buf.get();
		}
		if(buf.hasRemaining()) {
			throw new RuntimeException();
		}
		
		// assume things like the file is still available, hasn't been changed
		// since last time, etc.
		
//		System.out.println("renewing BurstBufferedReader buffer...");
		buf = null;
		
		long len = Math.min(bufferSize, fullFileSize - nextPosition);

		FileInputStream fis = new FileInputStream(fileName);		
		FileChannel fc = fis.getChannel();
		
		try {
			buf = fc.map(FileChannel.MapMode.READ_ONLY, nextPosition, len);
		} catch (IOException ioe) {
//			System.out.println("!Map failed in BurstBufferedReader -- retrying");
			Helper.tryToFreeMemory();
			buf = fc.map(FileChannel.MapMode.READ_ONLY, nextPosition, len);
		}
		
//		if(!buf.isLoaded()) {
			buf.load();
//		}
		
		nextPosition += len;

		fc.close();
		fis.close();
		
		return leftOvers;
	}
	
	public void close() {
		buf = null;
	}
	
	protected void finalize() throws Throwable {
	    try {
	        close();
	    } finally {
	        super.finalize();
	    }
	}
}
