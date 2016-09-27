package models;
/**
 * Represents a sequence of like marked squares on the game board.
 * The sequence starts at the square with coordinates (headX,headY) 
 * and continues to the square with coordinates (tailX,tailY).
 * 
 * @author Anastasia Radchenko
 */
public class Chain {
	
	/**
	 * The chain possible directions
	 */
	public enum Direction { UP, DIAG_UP, RIGHT, DIAG_DOWN }
	
	private final Mark mark;
	
	private final Direction direction;
	
	private final int headX;
	private final int headY;

	private int tailX;
	private int tailY;
	
	private int length;
	
	public Chain(Mark mark, Direction direction, Coordinates coordinates) {
		this(mark, direction, coordinates, coordinates); 
	}
	
	public Chain(Mark mark, Direction direction, Coordinates head, Coordinates tail) {
		this(mark, direction, head.getX(), head.getY(), tail.getX(), tail.getY()); 
	}
	
	public Chain(Mark mark, Direction direction, int x, int y) {
		this(mark, direction, x, y, x, y); // single square chain
	}
	
	public Chain(Mark mark, Direction direction, int headX, int headY, int tailX, int tailY) {
		this.mark = mark;
		this.direction = direction;
		this.headX = headX;
		this.headY = headY;
		this.tailX = tailX;
		this.tailY = tailY;
		length = Math.max(Math.abs(tailX - headX + 1), Math.abs(tailY - headY + 1));
	}

	public Mark getMark() {
		return mark;
	}
	
	public Mark getClosingMark() {
		return mark == Mark.X ? Mark.O : Mark.X;
	}
	
	public int getHeadX() {
		return headX;
	}

	public int getHeadY() {
		return headY;
	}

	public int getTailX() {
		return tailX;
	}

	public int getTailY() {
		return tailY;
	}

	public int getLength() {
		return length;
	}
	
	public Coordinates extendHead() {
		switch (direction) {
		case UP: 
			return new Coordinates(headX, headY-1);
		case DIAG_UP: 
			return new Coordinates(headX-1, headY-1);
		case RIGHT: 
			return new Coordinates(headX-1, headY);
		case DIAG_DOWN: 
			return new Coordinates(headX-1, headY+1);
		default:
			return null; // never happens
		}
	}

	public Coordinates extendTail() {
		switch (direction) {
		case UP: 
			return new Coordinates(tailX, tailY+1);
		case DIAG_UP: 
			return new Coordinates(tailX+1, tailY+1);
		case RIGHT: 
			return new Coordinates(tailX+1, tailY);
		case DIAG_DOWN: 
			return new Coordinates(tailX+1, tailY-1);
		default:
			return null; // never happens
		}
	}
}
