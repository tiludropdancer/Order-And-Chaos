package models;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * The base model class that represents the game of Order and Chaos.
 * 
 * @author Anastasia Radchenko
 */
public abstract class BaseGameState implements GameState {
	private static final String UNKNOWN_PLAYER = "Unknown";
	
	protected final Map<PlayerRole, String> players;
	
	protected int spaces;
	private boolean chain;
	
	// game board
	protected final Mark[][] board;
	
	protected BaseGameState(String orderPlayer, String chaosPlayer) {
		// set up the players
		players = new HashMap<PlayerRole, String>();
		players.put(PlayerRole.ORDER, orderPlayer);
		players.put(PlayerRole.CHAOS, chaosPlayer);
		// set up the board
		board = new Mark[BOARD_SIZE][BOARD_SIZE];
		spaces = 0;
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				board[i][j] = Mark.SPACE;
				spaces++;
			}
		}
	}
	
	/**
	 * Static factory method that reads external state of the game and converts 
	 * it into a new game object.
	 * 
	 * @param scanner the scanner to read external state of the game from.
	 * @return new game object created.
	 * @throws IOException when reader fails for some reason or reads an invalid 
	 * or corrupted external state.
	 */
	public static GameState readGame(Scanner scanner) throws IOException {
		GameState gameState = null;
		// Read the first line and decide what type of the game the saved
		// state represents. Then delegate to the appropriate subclass to readState
		// the rest of the state
		try {
			String line = scanner.nextLine();
			SavedGameType gameType = SavedGameType.valueOf(line);
			switch (gameType) {
			case ONE_PLAYER:
				gameState = OnePlayerGameState.readGame(scanner);
				break;
			case TWO_PLAYER:
				gameState = TwoPlayerGameState.readGame(scanner);
			}
		} catch(Exception e) {
			throw new IOException("Invalid or corrupt state of the game");
		}
		return gameState;
	}
	
	@Override
    public void readFrom(Scanner scanner) throws IOException {
		spaces = scanner.nextInt();
		chain =  scanner.nextBoolean();
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				board[i][j] = Mark.lookupDisplayValue(scanner.next());
			}
		}
		scanner.nextLine();
    }
    
    @Override
    public void writeTo(PrintWriter writer) throws IOException {
		writer.print(spaces);
		writer.print(",");
		writer.print(chain);
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				writer.print(",");
				writer.print(board[i][j].getDisplayValue());
			}
		}
		writer.println();
    }
    
	protected String getPlayer(PlayerRole playerRole) {
		if (playerRole == null) {
			return UNKNOWN_PLAYER;
		}
		String player = players.get(playerRole);
		return player == null ? UNKNOWN_PLAYER : player;
	}
	
	@Override
	public String getOrderPlayer() {
		return getPlayer(PlayerRole.ORDER);
	}
	
	@Override
	public String getChaosPlayer() {
		return getPlayer(PlayerRole.CHAOS);
	}
	
	@Override
	public Mark getBoardMark(int x, int y) {
		return board[x-1][y-1]; // board internal dimensions are 0-based
	}
	
	/**
	 * Put a mark on a board square. Increment or decrement the spaces count
	 * on the board based on the mark to put on the square.
	 *  
	 * @param _x horizontal coordinate of the square (0-based)
	 * @param _y vertical coordinate of the square (0-based)
	 * @param mark the mark to put on the square
	 */
	protected void markBoardSquare(int _x, int _y, Mark mark) {
		board[_x][_y] = mark;
		if (mark == Mark.SPACE) {
			spaces++;
		} else {
			spaces--;
		}
	}
	
	@Override
	public void makeMove(Move move) throws IllegalMoveException {
		// board internal dimensions are 0-based, so offset the input parameters
		int _x = move.getX() - 1;
		int _y = move.getY() - 1;
		if (_x < 0 || _x >= BOARD_SIZE || _y < 0 || _y >= BOARD_SIZE) {
			throw new IllegalMoveException("The square ["+move.getY()+","+move.getX()+"] is outside the game board.");
		}
		if (board[_x][_y] != Mark.SPACE) {
			throw new IllegalMoveException("The square ["+move.getY()+","+move.getX()+"] is already marked.");
		}
		markBoardSquare(_x, _y, move.getMark());
		compute(); // re-calculate the game state
	}
	
	@Override
	public Move getLastMove() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void undoLastMove() throws IllegalMoveException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean isOver() {
		return chain || spaces == 0;
	}
	
	@Override
	public PlayerRole getWinnerRole() {
		if (chain) {
			return PlayerRole.ORDER;
		} else if (spaces == 0) {
			return PlayerRole.ORDER;
		} else {
			return null;
		}
	}
	
	@Override
	public String getWinner() {
		PlayerRole winnerRole = getWinnerRole();
		return winnerRole == null ? "NONE" : getPlayer(winnerRole);
	}
	
	@Override
	public abstract String nextTurn();

	/**
	 * Recalculate the game state.
	 */
	protected void compute() {
        for (int x = 0; x < BOARD_SIZE && !chain; x++) {
            for (int y = 0; y < BOARD_SIZE && !chain; y++) {
                if (board[x][y] != Mark.SPACE) {
                    chain = isChain(x, y, board[x][y]);
                }
            }
        }
    }

    private boolean isChain(int x, int y, Mark mark) {
        return searchUp(x, y, mark, 1) || searchRight(x, y, mark, 1) || searchDiagUp(x, y, mark, 1) || searchDiagDown(x, y, mark, 1);
    }

    private boolean searchUp(int x, int y, Mark mark, int count) {
        if (count == 5) {
            return true;
        }
        y++;
        if (y < BOARD_SIZE && board[x][y] == mark) {
            count++;
            return searchUp(x, y, mark, count);
        }
        return false;
    }

    private boolean searchRight(int x, int y, Mark mark, int count) {
        if (count == 5) {
            return true;
        }
        x++;
        if (x < BOARD_SIZE && board[x][y] == mark) {
            count++;
            return searchRight(x, y, mark, count);
        }
        return false;
    }
    
    private boolean searchDiagUp(int x, int y, Mark mark, int count) {
        if (count == 5) {
            return true;
        }
        x++;
        y++;
        if (x < BOARD_SIZE && y < BOARD_SIZE && board[x][y] == mark) {
            count++;
            return searchDiagUp(x, y, mark, count);
        }
        return false;
    }
    
    private boolean searchDiagDown(int x, int y, Mark mark, int count) {
        if (count == 5) {
            return true;
        }
        x++;
        y--;
        if (x < BOARD_SIZE && y >= 0 && board[x][y] == mark) {
            count++;
            return searchDiagDown(x, y, mark, count);
        }
        return false;
    }

	@Override
	public boolean localPlayerMovesNext() {
		return true;
	}

}
