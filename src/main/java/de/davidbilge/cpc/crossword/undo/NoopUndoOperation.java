package de.davidbilge.cpc.crossword.undo;

import de.davidbilge.cpc.crossword.Crossword;

public class NoopUndoOperation implements UndoOperation {

	@Override
	public void undo(Crossword crossword) {
		// NoOp
	}

	@Override
	public boolean hasEffect() {
		return false;
	}

}
