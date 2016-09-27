package models;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

/**
 * The model class that represents 1-player game.
 * 
 * @author Anastasia Radchenko
 */
public class OnePlayerGameState extends BaseGameState {
	public static final String AI_PLAYER = "Computer";
	
	// AI player strategy for this game
	private final AIPlayer aiPlayer = new AIPlayerImpl();
	
	// Indicator of AI player role in this game
	private final boolean aiPlaysOrder;
	
	// To support undo remember all moves made by the players
	private final Stack<Move> moves = new Stack<Move>();
	
	/**
	 * Factory method to instantiate a new 1-player game where real player
	 * will play as Order.
	 * 
	 * @param player the real player who will play as Order
	 * @return new game object created.
	 */
	public static OnePlayerGameState playAsOrder(String player) {
		return new OnePlayerGameState(player, AI_PLAYER);
	}
	
	/**
	 * Factory method to instantiate a new 1-player game where real player
	 * will play as Chaos.
	 * 
	 * @param player the real player who will play as Chaos
	 * @return new game object created.
	 */
	public static OnePlayerGameState playAsChaos(String player) {
		return new OnePlayerGameState(AI_PLAYER, player);
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

		String player = scanner.next();
		String role = scanner.next();
		scanner.nextLine();
		PlayerRole playerRole = PlayerRole.valueOf(role);

		if (playerRole == PlayerRole.ORDER) {
			gameState = playAsOrder(player);
		} else if (playerRole == PlayerRole.CHAOS) {
			gameState = playAsChaos(player);
		} else {
			throw new IOException("Saved game is corrupt or invalid");
		}
		gameState.readFrom(scanner);

		return gameState;
	}

	@Override
    public void readFrom(Scanner scanner) throws IOException {
		// readState basic state from the super class
    	super.readFrom(scanner);
    	// readState players' moves
    	while (scanner.hasNext()) {
    		try {
				String player = scanner.next();
				int x = scanner.nextInt();
				int y = scanner.nextInt();
				Mark mark = Mark.lookupDisplayValue(scanner.next());
				scanner.nextLine();
				Move move = new Move(player, x, y, mark);
				moves.push(move);
			} catch (Exception e) {
				throw new IOException();
			}
    	}
    }
    
	@Override
	public void writeTo(PrintWriter writer) throws IOException {
		// game type
		writer.println(SavedGameType.ONE_PLAYER);
		// player and their role
		if (aiPlaysOrder) {
			writer.print(getChaosPlayer());
			writer.print(",");
			writer.println(PlayerRole.CHAOS.name());
		} else {
			writer.print(getOrderPlayer());
			writer.print(",");
			writer.println(PlayerRole.ORDER.name());
		}
		
		// basic state from the super class
		super.writeTo(writer);
		
		// Reverse the order of the moves such that the top of the stack would be earliest move
		Stack<Move> reversedMoves = new Stack<Move>();
		while (!moves.empty()) {
			reversedMoves.push(moves.pop());
		}
		// Now put them back and keep writing them down as the would appear to come 
		// in chronological order
		while (!reversedMoves.empty()) {
			Move move = reversedMoves.pop();
			writer.print(move.getPlayer());
			writer.print(",");
			writer.print(move.getX());
			writer.print(",");
			writer.print(move.getY());
			writer.print(",");
			writer.println(move.getMark().getDisplayValue());
			moves.push(move);
		}
	}
	
	/**
	 * Hidden constructor that both factory methods call. Since java does not 
	 * allow any logic in the constructor before the call to its super(),
	 * we use factory methods to figure out how to call it.
	 * 
	 * @param orderPlayer the name of an Order player.
	 * @param chaosPlayer the name of a Chaos player.
	 */
	protected OnePlayerGameState(String orderPlayer, String chaosPlayer) {
		super(orderPlayer, chaosPlayer);
		aiPlaysOrder = AI_PLAYER.equals(orderPlayer);
		if (aiPlaysOrder) {
			makeAINextMove();
		}
	}
	
	private void makeMoveAndRemember(Move move) throws IllegalMoveException {
		super.makeMove(move);
		moves.push(move);
	}
	
	private void makeAINextMove() {
		Move move = aiPlayer.nextMove();
		try {
			makeMoveAndRemember(move);
		} catch (IllegalMoveException e) {
		} // will never happened, because AI player always makes correct moves
	}
	
	@Override
	public void makeMove(Move move) throws IllegalMoveException {
		makeMoveAndRemember(move);
		
		if (isOver()) { // no more moves
			moves.clear();
		} else { // follow human move by the AI move
			makeAINextMove();
		}
	}

	@Override
	public Move getLastMove() {
		return moves.isEmpty() ? null : moves.peek();
	}

	@Override
	public void undoLastMove() throws IllegalMoveException {
		// The top of the moves stack should always be the last AI player move.
		// To undo the real player last move, try to pop stack twice to get these moves
		// and wipe out their marks on the board.
		Move lastMove = null;
		Move nextToLastMove = null;
		try {
			lastMove = moves.pop();
			nextToLastMove = moves.pop();
			undoMove(nextToLastMove);
			undoMove(lastMove);
		} catch (EmptyStackException e) {
			// In case there were no moves made yet or the only move was made by AI, 
			// restore the stack and do nothing.
			if (lastMove != null) {
				moves.push(lastMove);
			}
			throw new IllegalMoveException("No prior move yet");
		}
	}
	
	private void undoMove(Move move) {
		// board internal dimensions are 0-based, so offset the move coordinate
		int _x = move.getX() - 1;
		int _y = move.getY() - 1;
		markBoardSquare(_x, _y, Mark.SPACE);
		compute();
	}

	@Override
	public String nextTurn() {
		return aiPlaysOrder ? getChaosPlayer() : getOrderPlayer();
	}

	@Override
	public boolean isOrderNextTurn() {
		return !aiPlaysOrder;
	}

	// Private inner interface/classes that have access to this game
	
	/**
	 * AI player strategy. It defines behavior of AI player in 1-player game.
	 */
	private interface AIPlayer {
		Move nextMove();
	}

	private class AIPlayerImpl implements AIPlayer {
		private Random r = new Random();

		@Override
		public Move nextMove() {
			Chain chain = findLongestOpenChain();
			Mark mark = null;
			Coordinates coordinates = null;
			// if no chain found, use random mark, 
			// otherwise, if AI plays Order, then extend the chain with its mark, 
			// otherwise close it with the opposite mark
			if (chain == null) {
				mark = getRandomMark();
				coordinates = getRandomCoordinates();
			} else {
				mark = aiPlaysOrder ? chain.getMark() : chain.getClosingMark();
				
				boolean headOpen = isHeadOpen(chain); 
				boolean tailOpen = isTailOpen(chain);
				
				if (headOpen && tailOpen) {
					coordinates = r.nextBoolean() ? chain.extendHead() : chain.extendTail();
				} else if (headOpen) {
					coordinates = chain.extendHead();
				} else {
					coordinates = chain.extendTail();
				} 
			}
			return new Move(AI_PLAYER, coordinates.getX(), coordinates.getY(), mark);
		}
		
		private Mark getRandomMark() {
			return r.nextBoolean() ? Mark.X : Mark.O;
		}
		
		private Coordinates getRandomCoordinates() {
			int x;
			int y;
			do {
				x = r.nextInt(BOARD_SIZE);
				y = r.nextInt(BOARD_SIZE);
			} while (board[x][y] != Mark.SPACE);
			return new Coordinates(x+1, y+1); // external board coordinates are 1-based
		}
		
		private Chain findLongestOpenChain() {
	    	List<Chain> longestChains = new ArrayList<Chain>();
	    	int maxLength = 0;
	        for (int x = 0; x < BOARD_SIZE; x++) {
	            for (int y = 0; y < BOARD_SIZE; y++) {
	                if (board[x][y] != Mark.SPACE) {
	                    maxLength = findLongestOpenChains(x, y, board[x][y], maxLength, longestChains);
	                }
	            }
	        }
	        int size = longestChains.size(); 
	        switch (size) {
	        case 0:	
	        	return null;
	        case 1:	
	        	return longestChains.get(0);
	        default:
	        	int randomIndex = r.nextInt(size);
	        	return longestChains.get(randomIndex);
	        }
		}
		
	    private int findLongestOpenChains(int _x, int _y, Mark mark, int maxLength, List<Chain> longestChains) {
	    	for (Chain.Direction direction : Chain.Direction.values()) {
	    		Coordinates head = new Coordinates(_x+1, _y+1);
		    	Coordinates tail = searchTail(_x, _y, mark, direction);
		    	if (tail != null) {
		    		Chain chain = new Chain(mark, direction, head, tail);
		    		if (isHeadOpen(chain) || isTailOpen(chain)) {
		    			int length = chain.getLength();
		    			if (length == maxLength) {
		    				longestChains.add(chain);
		    			} else if (length > maxLength) {
		    				longestChains.clear();
		    				longestChains.add(chain);
		    				maxLength = length;
		    			}
		    		}
		    	}
	    	}
	    	return maxLength;
	    }

	    private Coordinates searchTail(int _x, int _y, Mark mark, Chain.Direction direction) {
	    	// step in the direction of the chain
	    	switch (direction) {
	    	case UP:
		        _y++; break;
	    	case DIAG_UP:
	    		_x++;
		        _y++; break;
	    	case RIGHT:
	    		_x++; break;
	    	case DIAG_DOWN:
	    		_x++;
		        _y--; break;
	    	}
	        if (_x <  BOARD_SIZE && 0 <= _y && _y < BOARD_SIZE && board[_x][_y] == mark) {
	            return searchTail(_x, _y, mark, direction);
	        }
	    	// either went outside the board or found an empty square or the square marked with the opposite mark
	        // step back and build the tail of the chain coordinates
	    	switch (direction) {
	    	case UP:
		        _y--; break;
	    	case DIAG_UP:
	    		_x--;
		        _y--; break;
	    	case RIGHT:
	    		_x--; break;
	    	case DIAG_DOWN:
	    		_x--;
		        _y++; break;
	    	}
	        return new Coordinates(_x+1, _y+1);
	    }

	    private boolean isHeadOpen(Chain chain) {
			Coordinates coordinates = chain.extendHead();
			if (coordinates.getX() < 1 || coordinates.getY() < 1 || coordinates.getY() > BOARD_SIZE) {
				return false;
			}
			Mark mark = getBoardMark(coordinates.getX(), coordinates.getY());
			return mark == Mark.SPACE;
		}
		
		private boolean isTailOpen(Chain chain) {
			Coordinates coordinates = chain.extendTail();
			if (coordinates.getX() > BOARD_SIZE || coordinates.getY() < 1 || coordinates.getY() > BOARD_SIZE) {
				return false;
			}
			Mark mark = getBoardMark(coordinates.getX(), coordinates.getY());
			return mark == Mark.SPACE;
		}
	}
	
}
