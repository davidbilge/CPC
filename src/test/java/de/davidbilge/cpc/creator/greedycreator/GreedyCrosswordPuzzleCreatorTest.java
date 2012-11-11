package de.davidbilge.cpc.creator.greedycreator;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import de.davidbilge.cpc.creator.CrosswordPuzzleCreator.FillResult;
import de.davidbilge.cpc.crossword.CrosswordFactory;
import de.davidbilge.cpc.crossword.Direction;
import de.davidbilge.cpc.dictionary.Dictionary;

@ContextConfiguration({ "/application-context.xml" })
public class GreedyCrosswordPuzzleCreatorTest extends AbstractJUnit4SpringContextTests {
	private static final Logger LOG = LoggerFactory.getLogger(GreedyCrosswordPuzzleCreatorTest.class);

	@Resource
	private GreedyCrosswordPuzzleCreator greedyCrosswordPuzzleCreator;

	@Resource
	private Dictionary dictionary;

	@Test
	public void testCreation() {
		FillResult fillResult = greedyCrosswordPuzzleCreator.fillCrossword(CrosswordFactory.createEmptyCrossword(10, 5), dictionary, Direction.ACROSS);

		int score = greedyCrosswordPuzzleCreator.scoreCalculator.calculateScore(fillResult.crossword);

		LOG.debug("Resulting crossword:\n\n" + fillResult.crossword.toString() + "\nScore: " + score + "\n");
	}
}
