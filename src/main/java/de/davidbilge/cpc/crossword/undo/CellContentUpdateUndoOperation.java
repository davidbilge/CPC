package de.davidbilge.cpc.crossword.undo;

import de.davidbilge.cpc.crossword.Crossword;

public class CellContentUpdateUndoOperation implements UndoOperation {
	private final char oldContent;
	private final int posX, posY;

	public CellContentUpdateUndoOperation(char oldContent, int posX, int posY) {
		this.oldContent = oldContent;
		this.posX = posX;
		this.posY = posY;
	}

	@Override
	public void undo(Crossword crossword) {
		crossword.setCell(posX, posY, oldContent);
	}

	@Override
	public boolean hasEffect() {
		return true;
	}
}
