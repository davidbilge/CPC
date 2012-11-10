package de.davidbilge.cpc.crossword.undo;

import de.davidbilge.cpc.crossword.Crossword;

public class CellAccessibilityUndoOperation implements UndoOperation {
	private final int x, y;
	private final boolean oldAccessible;

	public CellAccessibilityUndoOperation(int x, int y, boolean oldAccessible) {
		this.x = x;
		this.y = y;
		this.oldAccessible = oldAccessible;
	}

	@Override
	public void undo(Crossword crossword) {
		crossword.setCellAccessible(x, y, oldAccessible);
	}

	@Override
	public boolean hasEffect() {
		return true;
	}

}
