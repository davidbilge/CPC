package de.davidbilge.cpc.crossword.undo;

import de.davidbilge.cpc.crossword.Cell;
import de.davidbilge.cpc.crossword.Crossword;

public class CellUpdateUndoOperation implements UndoOperation {
	private final int posX, posY;
	private final Cell oldCell;

	public CellUpdateUndoOperation(int posX, int posY, Cell oldCell) {
		this.posX = posX;
		this.posY = posY;
		this.oldCell = oldCell;
	}

	@Override
	public void undo(Crossword crossword) {
		if (oldCell != null) {
			crossword.setCell(posX, posY, oldCell);
		}
	}

	@Override
	public boolean hasEffect() {
		return oldCell != null;
	}

}
