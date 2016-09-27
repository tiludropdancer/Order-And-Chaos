package models;

/**
 * The role that player can play in a game.
 *
 * @author Anastasia Radchenko
 */
public enum PlayerRole {
	ORDER("Order"), CHAOS("Chaos");
	
	private final String displayName;
	PlayerRole(String displayName) {
		this.displayName = displayName;
	}
	
	public String toString() {
		return displayName;
	}
}
