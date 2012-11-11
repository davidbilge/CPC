package de.davidbilge.cpc.creator.greedycreator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import de.davidbilge.cpc.crossword.Crossword;
import de.davidbilge.cpc.crossword.Direction;
import de.davidbilge.cpc.crossword.Position;
import de.davidbilge.cpc.crossword.Vector;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FirstIncompleteWordPicker implements FillWordPicker {

	@Override
	public Vector pickWordToFill(Crossword c, Direction direction) {
		Preconditions.checkNotNull(direction);

		if (direction == Direction.ACROSS) {
			for (int y = 0; y < c.getHeight(); ++y) {
				for (int x = 0; x < c.getWidth(); ++x) {
					String word = c.getWord(x, y, direction);
					if (word.length() > 1 && StringUtils.contains(word, '.')) {
						return new Vector(new Position(x, y), direction);
					}
				}
			}
		} else {
			for (int x = 0; x < c.getWidth(); ++x) {
				for (int y = 0; y < c.getHeight(); ++y) {
					String word = c.getWord(x, y, direction);
					if (word.length() > 1 && StringUtils.contains(word, '.')) {
						return new Vector(new Position(x, y), direction);
					}
				}
			}
		}

		// Apparently, there are no incomplete words any more!
		return null;
	}

}
