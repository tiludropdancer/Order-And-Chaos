package models;

/**
 * Player's move on the board.
 * 
 * @author Anastasia Radchenko
 */
public class Move {
	private final String player;
	private final int x;
	private final int y;
	private final Mark mark;

	public Move(String player, int x, int y, Mark mark) {
		this.player = player;
		this.x = x;
		this.y = y;
		this.mark = mark;
	}
	
	public String getPlayer() {
		return player;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Mark getMark() {
		return mark;
	}
	
}
