package de.davidbilge.cpc.creator;

import java.util.List;

import de.davidbilge.cpc.crossword.Crossword;
import de.davidbilge.cpc.crossword.undo.UndoOperation;
import de.davidbilge.cpc.dictionary.Dictionary;

public interface CrosswordPuzzleCreator {
	List<UndoOperation> fillCrossword(Crossword initial, Dictionary dictionary);
}
