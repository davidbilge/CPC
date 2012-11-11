package de.davidbilge.cpc.creator.greedycreator;

import de.davidbilge.cpc.crossword.Crossword;
import de.davidbilge.cpc.crossword.Direction;
import de.davidbilge.cpc.crossword.Vector;

public interface FillWordPicker {

	Vector pickWordToFill(Crossword c, Direction dir);

}
