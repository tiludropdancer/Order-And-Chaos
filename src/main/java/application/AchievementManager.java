package application;

import io.AchievementsReaderWriter;
import io.FileAchievemensReaderWriter;
import models.Achievement;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Component responsible for keeping track of players achievements.
 *
 * @author Anastasia Radchenko
 */
public class AchievementManager {

	private static final int BONUS = 50; // one time winner bonus
	private static final int SUPER_BONUS = 100; // repeat winner bonus
	
	private static final AchievementsReaderWriter achievementsReaderWriter = new FileAchievemensReaderWriter();
	
	private static AchievementManager INSTANCE = null;

	/**
	 * Hidden constructor to implement Singleton design pattern
	 */
	private AchievementManager() {
	}
	
	public static AchievementManager getInstance() {
		if (INSTANCE == null) {
			try {
				INSTANCE = achievementsReaderWriter.read();
			} catch (IOException e) {
				System.err.println("Exception while reading achievements: "+e.getMessage());
			}
			if (INSTANCE == null) {
				INSTANCE = new AchievementManager(); // fall back to default
			}
		}
		return INSTANCE;
	}
	
	private Map<String, Achievement> achievements = new HashMap<String, Achievement>();
	private String lastWinner = null;
	
	public static AchievementManager readFrom(Scanner scanner) throws IOException {
		AchievementManager achievementManager = new AchievementManager();
		if (scanner.hasNext()) {
			achievementManager.lastWinner = scanner.nextLine();
			while (scanner.hasNext()) {
				String player = scanner.next();
				int score = scanner.nextInt();
				scanner.nextLine();
				achievementManager.achievements.put(player, new Achievement(player, score));
			}
		}
		return achievementManager;
	}
	
	public void writeTo(PrintWriter writer) throws IOException {
		if (lastWinner != null) {
			writer.println(lastWinner);
			for (Achievement achievement : achievements.values()) {
				writer.print(achievement.getPlayer());
				writer.print(",");
				writer.println(achievement.getScore());
			}
		}
	}
	
	public void recordAchievement(String player) throws IOException {
		Achievement achievement = achievements.get(player);
		if (achievement == null) {
			achievement = new Achievement(player, BONUS);
			achievements.put(player, achievement);
		} else {
			int bonus = player.equals(lastWinner) ? SUPER_BONUS : BONUS;
			achievement.raiseScore(bonus);
		}
		lastWinner = player;
		achievementsReaderWriter.write(this);
	}
	
	public Achievement getAchievement(String player) {
		return achievements.get(player);
	}

	public List<Achievement> getAchievements() {
		List<Achievement> list = new ArrayList<Achievement>(achievements.values());
		Collections.sort(list);
		return list;
	}
	
}
