package io;

import application.AchievementManager;

import java.io.IOException;

/**
 * Component able to readState and writeState player's achievements from and to external data store.
 * 
 * @author Anastasia Radchenko
 */
public interface AchievementsReaderWriter {

	/**
	 * Read previously recorded achievements from external data store.
	 * 
	 * @return achievement manager filled up with player achievements.
	 * @throws IOException when reading fails for some reason.
	 */
	AchievementManager read() throws IOException;
	
	/**
	 * Write player achievements to external data store.
	 * 
	 * @param achiementManager that hold current player achievements.
	 * @throws IOException when writing fails for some reason.
	 */
	void write(AchievementManager achiementManager) throws IOException;
}
