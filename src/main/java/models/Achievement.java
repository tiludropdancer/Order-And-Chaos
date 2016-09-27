package models;

/**
 * Represents a player achievement, the score they earned by playing the game.
 *
 * @author Anastasia Radchenko
 */
public class Achievement implements Comparable<Achievement>{
	private final String player;
	private int score;
	
	public Achievement(String player, int score) {
		this.player = player;
		this.score = score;
	}

	public String getPlayer() {
		return player;
	}

	public int getScore() {
		return score;
	}

	public void raiseScore(int bonus) {
		score += bonus;
	}

	@Override
	public int compareTo(Achievement other) {
		if (this.score > other.score) {
			return -1;
		} else if (this.score < other.score) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public String toString() {
		return "{player:"+player+",score:"+score+"}";
	}

}
