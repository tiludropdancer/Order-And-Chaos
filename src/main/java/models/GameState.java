package models;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * The interface that represents the state of the game.
 *
 * @author Anastasia Radchenko
 */
public interface GameState {

	/**
	 * The size of the game board
	 */
	int BOARD_SIZE = 6;
	
	/**
	 * @return the name of Order player
	 */
	String getOrderPlayer();

	/**
	 * @return the name of Chaos player
	 */
	String getChaosPlayer();

	
	/**
	 * Return the mark placed on the board square. The origin of the coordinate 
	 * system is at the lower left corner of the board, with coordinates (1,1).
	 *  
	 * @param x horizontal coordinate of the square.
	 * @param y vertical coordinate of the square.
	 * @return the mark placed on the board square with the coordinates (x, y).
	 */
	Mark getBoardMark(int x, int y);

	/**
	 * Make the move entered by a player. The game will evaluate the correctness
	 * of the move and if it is valid, will mark the square.
	 * 
	 * @param move the move that player has entered.
	 * @throws IllegalMoveException if the player has entered invalid move either 
	 * outside the board or on the square that had been already marked before.
	 */
	void makeMove(Move move) throws IllegalMoveException;

	/**
	 * @return The last move made by the player.
	 */
	Move getLastMove();
	
	/**
	 * Undo the last move made by player.
	 * 
	 * @throws IllegalMoveException when there was no prior move yet
	 */
	void undoLastMove() throws IllegalMoveException;
	
	/**
	 * @return true if game has finished.
	 */
	boolean isOver();

	/**
	 * @return the game winner role.
	 */
	PlayerRole getWinnerRole();

	/**
	 * @return the game winner.
	 */
	String getWinner();

	/**
	 * @return the name of a player who moves next.
	 */
	String nextTurn();

	/**
	 * @return true if the Order player moves next
	 */
	boolean isOrderNextTurn();

	/**
	 * Read this game state from external data store.
	 *
	 * @param scanner provides readState access to external data store.
	 * @throws IOException reading fails for some reason.
	 */
	void readFrom(Scanner scanner) throws IOException;
	
	/**
	 * Write this game state to external data store.
	 * 
	 * @param writer provides writeState access to external data store.
	 * @throws IOException writing fails for some reason.
	 */
	void writeTo(PrintWriter writer) throws IOException;


	/**
	 * @return true if local game player makes next move
	 */
	boolean localPlayerMovesNext();
}