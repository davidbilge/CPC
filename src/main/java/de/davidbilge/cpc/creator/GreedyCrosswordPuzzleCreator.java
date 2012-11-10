package de.davidbilge.cpc.creator;

import java.util.Stack;

import de.davidbilge.cpc.creator.scoring.ScoreCalculator;
import de.davidbilge.cpc.crossword.Crossword;
import de.davidbilge.cpc.crossword.Direction;
import de.davidbilge.cpc.crossword.undo.UndoOperation;
import de.davidbilge.cpc.dictionary.Dictionary;

public class GreedyCrosswordPuzzleCreator implements CrosswordPuzzleCreator {
	private final ScoreCalculator scoreCalculator;

	public GreedyCrosswordPuzzleCreator(ScoreCalculator scoreCalculator) {
		super();
		this.scoreCalculator = scoreCalculator;
	}

	@Override
	public Stack<UndoOperation> fillCrossword(Crossword initial, Dictionary dictionary) {
		return null;
	}

	private UndoOperation insertWord(Crossword c, String word, Direction dir) {
		return null;
	}

}
