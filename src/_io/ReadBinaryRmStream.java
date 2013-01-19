/*
 * Created on Jul 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _io;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.List;

import stage3.RewardMatrixElement;
import _misc.Constants;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ReadBinaryRmStream {
	
	BurstBufferedReader in;
	public int maxNumBoardCards;
	public int[] numClusters;

	public RewardMatrixElement container;
	public boolean useContainer;
	
	public ReadBinaryRmStream(String fileName, int bufferSize, boolean useContainer) 
			throws IOException {
		
		in = new BurstBufferedReader(fileName, bufferSize);
		
		// read and verify format ID
		short formatID = in.readShort();
		if(formatID != Constants.vidRewardMatrixElementList) {
			throw new RuntimeException();
		}

		// read header from file
		short header[] = new short[10];
		for(int i = 0; i < 10; i++) {
			header[i] = in.readShort();
		}
		
		// process header
		numClusters = new int[6];
		for(int i = 0; i < 6; i++) {
			numClusters[i] = header[i];
		}
		maxNumBoardCards = header[6];
		
		if(useContainer) {
			container = new RewardMatrixElement(0, 0, 0);
		}
		this.useContainer = useContainer;
	}
	
	public void getRmeViaContainer() throws IOException {
		if(!useContainer) {
			throw new RuntimeException();
		}
		
		try {

			// be careful about calling in.readXyz() once per field
			container.firstDim = in.readInt();
			container.secondDim = in.readInt();
			container.value = in.readFloat();
			
			return;
			
		} catch (BufferUnderflowException bue) {
			container = null;
			return;
		}
		
	}
	
	public RewardMatrixElement getRme() throws IOException {
		if(useContainer) {
			throw new RuntimeException();
		}
		
		try {
			
			int firstDim;
			int secondDim;
			float value;

			// be careful about calling in.readXyz() once per field
			firstDim = in.readInt();
			secondDim = in.readInt();
			value = in.readFloat();
			
			return new RewardMatrixElement(firstDim, secondDim, value);
			
		} catch (BufferUnderflowException bue) {
			return null;
		}
		
	}
	
	public void close() {
		in.close();
	}
}
