package de.davidbilge.cpc.creator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.davidbilge.cpc.CPCException;
import de.davidbilge.cpc.creator.scoring.ScoreCalculator;
import de.davidbilge.cpc.crossword.Crossword;
import de.davidbilge.cpc.crossword.Direction;
import de.davidbilge.cpc.crossword.Position;
import de.davidbilge.cpc.crossword.undo.MetaUndoOperation;
import de.davidbilge.cpc.crossword.undo.NoopUndoOperation;
import de.davidbilge.cpc.crossword.undo.UndoOperation;
import de.davidbilge.cpc.dictionary.Dictionary;

public class GreedyCrosswordPuzzleCreator implements CrosswordPuzzleCreator {
	private static final int MAX_EVALUATED_ALTERNATIVES = 10;

	private final ScoreCalculator scoreCalculator;

	public GreedyCrosswordPuzzleCreator(ScoreCalculator scoreCalculator) {
		super();
		this.scoreCalculator = scoreCalculator;
	}

	@Override
	public FillResult fillCrossword(Crossword initial, Dictionary dictionary, Direction initialDirection) {
		Position pivotCell = initial.findStartOfFirstIncompleteWord(initialDirection);

		if (pivotCell == null) {
			return new FillResult(initial, new NoopUndoOperation());
		}

		String currentContent = initial.getWord(pivotCell.x, pivotCell.y, initialDirection);

		List<String> dictionarySubset = dictionary.filter(Crossword.regexify(currentContent));
		if (dictionarySubset.isEmpty()) {
			// initialDirection = switchDirection(initialDirection);
			// currentContent = initial.getWord(pivotCell.x, pivotCell.y,
			// initialDirection);
			// dictionarySubset =
			// dictionary.filter(Crossword.regexify(currentContent));

			if (dictionarySubset.isEmpty()) {
				// Can't proceed; back up!
				Crossword finalCrossword = new Crossword(initial);

				List<UndoOperation> undos = new ArrayList<>();

				Position emptyCell;
				while ((emptyCell = finalCrossword.findFirstEmptyCell(initialDirection)) != null) {
					undos.add(finalCrossword.setCellAccessible(emptyCell.x, emptyCell.y, false));
				}

				return new FillResult(finalCrossword, new MetaUndoOperation(undos));
			}
		}

		Collections.shuffle(dictionarySubset);

		UndoOperation undo = new NoopUndoOperation();
		int bestScore = Integer.MAX_VALUE;
		Crossword bestCopy = initial;

		for (int i = 0; i < Math.min(MAX_EVALUATED_ALTERNATIVES, dictionarySubset.size()); ++i) {
			String word = dictionarySubset.get(i);

			Crossword copy = new Crossword(initial);
			UndoOperation wordInsertionUndoOperation = copy.putWord(word, pivotCell.x, pivotCell.y, initialDirection, false);
			FillResult fillResult = fillCrossword(copy, dictionary, switchDirection(initialDirection));

			int currentScore = scoreCalculator.calculateScore(copy);

			if (currentScore <= bestScore) {
				undo = new MetaUndoOperation(wordInsertionUndoOperation, fillResult.undoOperation);
				bestScore = currentScore;
				bestCopy = fillResult.crossword;
			}
		}

		return new FillResult(bestCopy, undo);
	}

	private static Direction switchDirection(Direction dir) {
		switch (dir) {
		case ACROSS:
			return Direction.DOWN;
		case DOWN:
			return Direction.ACROSS;
		}

		throw new CPCException("Unknown direction '" + dir + "'");
	}

}
