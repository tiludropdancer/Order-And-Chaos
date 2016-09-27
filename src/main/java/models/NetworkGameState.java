package models;

/**
 * The sate of the network game.
 * 
 * @author Anastasia Radchenko
 */
public class NetworkGameState extends TwoPlayerGameState {

	// Indicator of host player playing as Order in this game
	private final boolean hostPlaysOrder;
	
	private Move lastMove;
	
	public NetworkGameState(String orderPlayer, String chaosPlayer, boolean hostPlaysOrder) {
		super(orderPlayer, chaosPlayer);
		this.hostPlaysOrder = hostPlaysOrder;
	}

	public String getHostPlayer() {
		return hostPlaysOrder ? getOrderPlayer() : getChaosPlayer();
	}

	public String getRemotePlayer() {
		return hostPlaysOrder ? getChaosPlayer() : getOrderPlayer();
	}
	
	@Override
	public void makeMove(Move move) throws IllegalMoveException {
		super.makeMove(move);
		lastMove = move;
	}

	@Override
	public Move getLastMove() {
		return lastMove;
	}

	@Override
	public boolean localPlayerMovesNext() {
		return nextTurn().equals(getHostPlayer());
	}

}
