/*
 * Created on Jun 5, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package _io;

import java.io.IOException;
import stage1.*;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface ReadBinaryData {
	public void close() throws IOException ;
	public HandRecord readRecord() throws IOException ;
}
