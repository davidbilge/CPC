package de.davidbilge.cpc.creator.scoring;

import org.springframework.stereotype.Component;

import de.davidbilge.cpc.crossword.Cell;
import de.davidbilge.cpc.crossword.Crossword;

@Component
public class SimplePenaltyScoreCalculator implements ScoreCalculator {

	private static final int UNACCESSIBLE_CELL_PENALTY = 100;
	private static final int EMPTY_CELL_PENALTY = 10000;
	private static final int BARRIER_PENALTY = 25;

	@Override
	public int calculateScore(Crossword crossword) {
		int score = 0;

		for (Cell cell : crossword) {
			if (!cell.isAccessible()) {
				score += UNACCESSIBLE_CELL_PENALTY;
			}

			if (cell.getContent().equals(Cell.EMPTY_CHARACTER)) {
				score += EMPTY_CELL_PENALTY;
			}

			if (cell.isBarrierBottom()) {
				score += BARRIER_PENALTY;
			}

			if (cell.isBarrierRight()) {
				score += BARRIER_PENALTY;
			}

		}

		return score;
	}

}
