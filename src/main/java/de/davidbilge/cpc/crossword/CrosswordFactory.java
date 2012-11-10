package de.davidbilge.cpc.crossword;

public final class CrosswordFactory {
	private CrosswordFactory() {
	}

	public static Crossword createEmptyCrossword(int w, int h) {
		Crossword c = new Crossword(w, h);

		return c;
	}

}
