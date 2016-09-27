package models;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * The model class that represents 2-player game state.
 * 
 * @author Anastasia Radchenko
 */
public class TwoPlayerGameState extends BaseGameState {
	// true if Order player moves next
	private boolean orderMovesNext;

	/**
	 * Constructor of a 2-player game object.
	 * @param orderPlayer the name of a player who will play as Order.
	 * @param chaosPlayer the name of a player who will play as Chaos.
	 */
	public TwoPlayerGameState(String orderPlayer, String chaosPlayer) {
		super(orderPlayer, chaosPlayer);
		orderMovesNext = true;
	}

	/**
	 * Static factory method that reads external state of the game and converts
	 * it into a new game state object.
	 *
	 * @param scanner the scanner to read external state of the game from.
	 * @return new game object created.
	 * @throws IOException when reader fails for some reason or reads an invalid
	 * or corrupted external state.
	 */
	public static GameState readGame(Scanner scanner) throws IOException {
		GameState gameState = null;

		String orderPlayer = scanner.next();
		String chaosPlayer = scanner.next();
		scanner.nextLine();

		gameState = new TwoPlayerGameState(orderPlayer, chaosPlayer);
		gameState.readFrom(scanner);

		return gameState;
	}

	@Override
    public void readFrom(Scanner scanner) throws IOException {
		// readState basic state from the super class
    	super.readFrom(scanner);
    	// readState who moves next
    	orderMovesNext = scanner.nextBoolean();
		scanner.nextLine();
    }
    
	@Override
	public void writeTo(PrintWriter writer) throws IOException {
		// game type
		writer.println(SavedGameType.TWO_PLAYER);
		// players
		writer.print(getOrderPlayer());
		writer.print(",");
		writer.println(getChaosPlayer());
		// basic state from super class
		super.writeTo(writer);
		// who moves next
		writer.println(orderMovesNext);
	}
	
	@Override
	public void makeMove(Move move) throws IllegalMoveException {
		super.makeMove(move);
		if (!isOver()) {
			orderMovesNext = !orderMovesNext; // flip the next turn
		}
	}

	@Override
	public String nextTurn() {
		return orderMovesNext ? getOrderPlayer() : getChaosPlayer();
	}

	@Override
	public boolean isOrderNextTurn() {
		return orderMovesNext;
	}

}
