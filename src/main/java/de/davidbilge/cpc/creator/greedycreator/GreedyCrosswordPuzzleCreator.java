package de.davidbilge.cpc.creator.greedycreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import de.davidbilge.cpc.CPCException;
import de.davidbilge.cpc.creator.CrosswordPuzzleCreator;
import de.davidbilge.cpc.creator.scoring.ScoreCalculator;
import de.davidbilge.cpc.crossword.Cell;
import de.davidbilge.cpc.crossword.Crossword;
import de.davidbilge.cpc.crossword.Direction;
import de.davidbilge.cpc.crossword.Position;
import de.davidbilge.cpc.crossword.Vector;
import de.davidbilge.cpc.crossword.undo.CellUpdateUndoOperation;
import de.davidbilge.cpc.crossword.undo.MetaUndoOperation;
import de.davidbilge.cpc.crossword.undo.NoopUndoOperation;
import de.davidbilge.cpc.crossword.undo.UndoOperation;
import de.davidbilge.cpc.dictionary.Dictionary;

public class GreedyCrosswordPuzzleCreator implements CrosswordPuzzleCreator {
	private static final Logger LOG = LoggerFactory.getLogger(GreedyCrosswordPuzzleCreator.class);

	private static final int MAX_EVALUATED_ALTERNATIVES = 2;

	private final ScoreCalculator scoreCalculator;
	private final FillWordPicker fillWordPicker;

	public GreedyCrosswordPuzzleCreator(ScoreCalculator scoreCalculator, FillWordPicker fillWordPicker) {
		this.scoreCalculator = scoreCalculator;
		this.fillWordPicker = fillWordPicker;
	}

	@Override
	public FillResult fillCrossword(Crossword initial, Dictionary dictionary, Direction initialDirection) {
		return fillCrossword(initial, dictionary, initialDirection, 0f, 1f);
	}

	private FillResult fillCrossword(Crossword initial, Dictionary dictionary, final Direction initialDirection, float completion, float scale) {
		List<String> dictionarySubset = ImmutableList.of();
		String currentContent = null;
		Position pivotCell = null;
		List<UndoOperation> barrierInsertionUndos = new ArrayList<>();
		Direction currentDirection = initialDirection;

		while (dictionarySubset.isEmpty()) {
			Vector vector = fillWordPicker.pickWordToFill(initial, currentDirection);

			if (vector == null || vector.position == null || vector.direction == null) {
				// No word exists that could be filled.
				return new FillResult(initial, new NoopUndoOperation());
			}

			pivotCell = vector.position;
			currentDirection = vector.direction;

			currentContent = initial.getWord(pivotCell.x, pivotCell.y, currentDirection);
			if (currentContent.length() <= 1) {
				// Apparently, all that is left are length-one-words. We don't
				// want those, so fill those cells with "forbidden" flags.
				Crossword finalCrossword = new Crossword(initial);

				List<UndoOperation> undos = new ArrayList<>();

				Position emptyCell;
				while ((emptyCell = finalCrossword.findFirstEmptyCell(currentDirection)) != null) {
					undos.add(finalCrossword.setCellAccessible(emptyCell.x, emptyCell.y, false));
				}

				return new FillResult(finalCrossword, new MetaUndoOperation(undos));
			}

			dictionarySubset = dictionary.filter(Crossword.regexify(currentContent));

			if (dictionarySubset.isEmpty()) {
				Cell cellToUpdateWithBarrier = initial.getCell(pivotCell.x, pivotCell.y);

				barrierInsertionUndos.add(new CellUpdateUndoOperation(pivotCell.x, pivotCell.y, cellToUpdateWithBarrier));

				if (currentDirection == Direction.ACROSS) {
					cellToUpdateWithBarrier.setBarrierRight(true);
				} else {
					cellToUpdateWithBarrier.setBarrierBottom(true);
				}
			}
		}

		Collections.shuffle(dictionarySubset);

		UndoOperation undo = new NoopUndoOperation();
		int bestScore = Integer.MAX_VALUE;
		Crossword bestCopy = initial;

		for (int i = 0; i < Math.min(MAX_EVALUATED_ALTERNATIVES, dictionarySubset.size()); ++i) {
			String word = dictionarySubset.get(i);

			Crossword copy = new Crossword(initial);
			UndoOperation wordInsertionUndoOperation = copy.putWord(word, pivotCell.x, pivotCell.y, currentDirection, false);
			FillResult fillResult = fillCrossword(copy, dictionary, switchDirection(currentDirection), completion, scale * 0.1f);

			int currentScore = scoreCalculator.calculateScore(copy);

			if (currentScore <= bestScore) {
				undo = new MetaUndoOperation(barrierInsertionUndos, wordInsertionUndoOperation, fillResult.undoOperation);
				bestScore = currentScore;
				bestCopy = fillResult.crossword;
			}

			completion += (1f / MAX_EVALUATED_ALTERNATIVES) * scale;
			if (scale > 0.00001f) {
				LOG.debug("Completion: " + (completion * 100) + "%");
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
