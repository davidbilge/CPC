package de.davidbilge.cpc.creator.greedycreator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import de.davidbilge.cpc.crossword.Cell;
import de.davidbilge.cpc.crossword.Crossword;
import de.davidbilge.cpc.crossword.Direction;
import de.davidbilge.cpc.crossword.Position;
import de.davidbilge.cpc.crossword.Vector;

@Component
public class MostFilledWordPicker implements FillWordPicker {

	@Override
	public Vector pickWordToFill(Crossword c, Direction dir) {
		Vector currentBest = null;
		float currentBestRatio = -1f;

		for (Direction searchDir : Direction.values()) {
			for (int y = 0; y < c.getHeight(); ++y) {
				for (int x = 0; x < c.getWidth(); ++x) {
					String word = c.getWord(x, y, searchDir);
					word = stripToSingle(word, Cell.EMPTY_CHARACTER);

					int empty = StringUtils.countMatches(word, String.valueOf(Cell.EMPTY_CHARACTER));
					if (empty > 0 && word.length() > 1) {
						int filled = word.length() - empty;
						float ratio = filled / (float) empty;

						if (ratio > currentBestRatio) {
							currentBest = new Vector(new Position(x, y), searchDir);
							currentBestRatio = ratio;
						}
					}
				}
			}
		}

		return currentBest;
	}

	static String stripToSingle(String str, char stripChar) {
		Preconditions.checkNotNull(str);

		String stripCharString = String.valueOf(stripChar);

		if (str.length() == 1 && str.equals(stripCharString)) {
			return stripCharString;
		} else {
			String start = str.startsWith(stripCharString) ? stripCharString : "";
			String tail = str.endsWith(stripCharString) ? stripCharString : "";

			String strippedStr = StringUtils.strip(str, stripCharString);

			return start + strippedStr + tail;
		}
	}
}
