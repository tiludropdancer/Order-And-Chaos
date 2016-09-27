package io;

import application.AchievementManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class FileAchievemensReaderWriter implements AchievementsReaderWriter {

	private static final String USER_HOME_DIRECTORY = System.getProperty("user.home")+System.getProperty("file.separator"); 
	private static final String SAVED_ACHIEVEMENTS_FILENAME = USER_HOME_DIRECTORY+"order-and-chaos.achievements";

	@Override
	public AchievementManager read() throws IOException {
		Scanner scanner = null;
		AchievementManager achievementManager = null;
		try {
			File file = new File(SAVED_ACHIEVEMENTS_FILENAME);
			if (file.exists()) {
				FileReader fileReader = new FileReader(file);
				scanner = new Scanner(fileReader);
				scanner.useDelimiter("[,\\n]"); //regular expression -comma and newline
				achievementManager = AchievementManager.readFrom(scanner);
			}
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		return achievementManager;
	}

	@Override
	public void write(AchievementManager achiementManager) throws IOException {
		PrintWriter writer = null;
		try {
			FileWriter fw = new FileWriter(SAVED_ACHIEVEMENTS_FILENAME);
			BufferedWriter br = new BufferedWriter(fw);
			writer = new PrintWriter(br);
			
			achiementManager.writeTo(writer);
			
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
	}

}
