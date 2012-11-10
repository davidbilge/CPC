package de.davidbilge.cpc.crossword.undo;

import de.davidbilge.cpc.crossword.Crossword;

public interface UndoOperation {
	public void undo(Crossword crossword);

	public boolean hasEffect();
}
