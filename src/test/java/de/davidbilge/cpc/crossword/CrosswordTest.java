package de.davidbilge.cpc.crossword;

import junit.framework.Assert;

import org.junit.Test;

public class CrosswordTest {

	@Test
	public void testGetWordEmpty() {
		Crossword c = new Crossword(4, 3);

		String wordAcross = c.getWord(0, 0, Direction.ACROSS);
		Assert.assertEquals("....", wordAcross);
		String wordDown = c.getWord(0, 0, Direction.DOWN);
		Assert.assertEquals("...", wordDown);
	}

	@Test
	public void testGetWordFilled() {
		Crossword c = new Crossword(4, 3);
		c.setCell(0, 0, 'A');
		c.setCell(1, 0, 'B');
		c.setCell(2, 0, 'C');
		c.setCell(3, 0, 'D');

		String wordAcross = c.getWord(0, 0, Direction.ACROSS);
		Assert.assertEquals("ABCD", wordAcross);
		String wordDown = c.getWord(0, 0, Direction.DOWN);
		Assert.assertEquals("A..", wordDown);
	}

	@Test
	public void testGetWordWithBarrier() {
		Crossword c = createDemoCrossword();

		String wordAcross = c.getWord(0, 0, Direction.ACROSS);
		Assert.assertEquals("CAN", wordAcross);
		String wordDown = c.getWord(0, 0, Direction.DOWN);
		Assert.assertEquals("C..", wordDown);
	}

	private Crossword createDemoCrossword() {
		Crossword c = new Crossword(4, 3);

		c.setCell(0, 0, 'C');
		c.setCell(1, 0, 'A');
		c.setCell(2, 0, new Cell('N', false, true));
		c.setCell(3, 0, 'Z');

		return c;
	}

	@Test
	public void testIsValidEntry() {
		Crossword c = createDemoCrossword();

		Assert.assertTrue(c.validEntry("ZEN", 3, 0, Direction.DOWN));
		Assert.assertFalse(c.validEntry("ZEN", 3, 0, Direction.ACROSS));
	}

}
