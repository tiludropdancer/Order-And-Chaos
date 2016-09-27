package models;

/**
 * External (1-based) coordinates of the board.
 * 
 * @author Anastasia Radchenko
 */
public class Coordinates {
	private final int x;
	private final int y;
	
	public Coordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}
