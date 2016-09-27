package io;

import models.GameState;

import java.io.IOException;

/**
 * A component that can readState and writeState the game state to external data store.
 * 
 * @author Anastasia Radchenko
 */
public interface GameReaderWriter {

	/**
	 * Reads previously recorded game state from external data store.
	 * 
	 * @return the game state read from external data store.
	 * @throws IOException when reading fails for some reason.
	 */
	GameState readState() throws IOException;
	
	/**
	 * Writes the game state to external data store.
	 * 
	 * @param gameState the game state to writeState to external data store.
	 * @throws IOException when writing fails for some reason.
	 */
	void writeState(GameState gameState) throws IOException;
}
