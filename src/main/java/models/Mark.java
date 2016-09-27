package models;

/**
 * Enumeration of possible marks on the board
 * 
 * @author Anastasia Radchenko
 */
public enum Mark {
	SPACE(" "),
	X("x"),
	O("o");
	
	private final String displayValue;
	
	Mark(String displayValue) {
		this.displayValue = displayValue;
	}

	@Override
	public String toString() {
		return "["+displayValue+"]";
	}
	
	public String getDisplayValue() {
		return displayValue;
	}
	
	public static Mark lookupDisplayValue(String displayValue) {
		for (Mark mark : values()) {
			if (mark.displayValue.equals(displayValue)) {
				return mark;
			}
		}
		return null;
	}
}
