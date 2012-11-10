package de.davidbilge.cpc.creator.scoring;

import de.davidbilge.cpc.crossword.Crossword;

public interface ScoreCalculator {
	int calculateScore(Crossword crossword);
}
