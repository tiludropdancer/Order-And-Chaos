package models;
/**
 * Thrown when a player attempts to make an illegal move.
 * 
 * @author Anastasia Radchenko
 */
public class IllegalMoveException extends Exception {

	private static final long serialVersionUID = 1L;

	public IllegalMoveException() {
		super();
	}

	public IllegalMoveException(String message) {
		super(message);
	}

}
