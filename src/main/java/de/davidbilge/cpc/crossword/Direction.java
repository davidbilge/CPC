package de.davidbilge.cpc.crossword;

public enum Direction {
	ACROSS, DOWN;

	public Direction opposite() {
		if (this == ACROSS) {
			return DOWN;
		} else {
			return ACROSS;
		}

	}
}
