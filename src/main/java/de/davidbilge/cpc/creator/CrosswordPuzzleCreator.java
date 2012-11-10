package de.davidbilge.cpc.creator;

import de.davidbilge.cpc.crossword.Crossword;
import de.davidbilge.cpc.crossword.Direction;
import de.davidbilge.cpc.crossword.undo.UndoOperation;
import de.davidbilge.cpc.dictionary.Dictionary;

public interface CrosswordPuzzleCreator {

	FillResult fillCrossword(Crossword initial, Dictionary dictionary, Direction initialDirection);

	public static class FillResult {
		public final Crossword crossword;
		public final UndoOperation undoOperation;

		public FillResult(Crossword crossword, UndoOperation undoOperation) {
			this.crossword = crossword;
			this.undoOperation = undoOperation;
		}
	}
}
