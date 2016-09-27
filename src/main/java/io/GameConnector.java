package io;

import java.io.IOException;

/**
 * Component that allows two players connect and play the same 
 * game over the network.
 *  
 * @author Anastasia Radchenko
 */
public interface GameConnector {

	/**
	 * Connect this connector to the other one.
	 * 
	 * @param hostNameOrIPAddress the hostname or IP address of the other connector.
	 * @throws IOException when connection fails for some reason.
	 */
	void connect(String hostNameOrIPAddress) throws IOException;
	
	/**
	 * Disconnect this connector.
	 */
	void disconnect();
	
	/**
	 * Read integer value from the underlying connection.
	 * @return the int value read.
	 * @throws IOException on reading operation errors.
	 */
	int readInt() throws IOException;

	/**
	 * Read string value from the underlying connection.
	 * @return the int value read.
	 * @throws IOException on reading operation errors.
	 */
	String readString() throws IOException;

	/**
	 * Write integer value into the underlying connection.
	 * @param value the int value to write.
	 * @throws IOException on writing operation errors.
	 */
	void writeInt(int value) throws IOException;

	/**
	 * Write string value into the underlying connection.
	 * @param value the string value to write.
	 * @throws IOException on writing operation errors.
	 */
	void writeString(String value) throws IOException;
}
