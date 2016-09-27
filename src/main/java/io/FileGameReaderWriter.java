package io;

import models.BaseGameState;
import models.GameState;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * An implementation of GameStateReaderWriter that reads and writes the state of the game
 * from and to a file in current user home directory.
 * 
 * @author Anastasia Radchenko
 */
public class FileGameReaderWriter implements GameReaderWriter {

	private static final String USER_HOME_DIRECTORY = System.getProperty("user.home")+System.getProperty("file.separator"); 
	private static final String SAVED_GAME_FILENAME = USER_HOME_DIRECTORY+"order-and-chaos.saved";

	@Override
	public GameState readState() throws IOException {
		GameState gameState = null;
		Scanner scanner = null;
		try {
			File file = new File(SAVED_GAME_FILENAME);
			if (file.exists()) {
				FileReader fileReader = new FileReader(file);
				scanner = new Scanner(fileReader);
				scanner.useDelimiter("[,\\n]");
				gameState = BaseGameState.readGame(scanner);
			}
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		return gameState;
	}

	@Override
	public void writeState(GameState gameState) throws IOException {
		PrintWriter writer = null;
		try {
			FileWriter fw = new FileWriter(SAVED_GAME_FILENAME);
			BufferedWriter br = new BufferedWriter(fw);
			writer = new PrintWriter(br);
			
			gameState.writeTo(writer);
			
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
	}

	
}
