package de.davidbilge.cpc.crossword;

public class Cell {
	public static final char EMPTY_CHARACTER = '.';

	private Character content;
	private boolean barrierBottom = false, barrierRight = false;
	private boolean accessible = true;

	public Cell(Cell copyOf) {
		this.content = copyOf.content;
		this.barrierBottom = copyOf.barrierBottom;
		this.barrierRight = copyOf.barrierRight;
		this.accessible = copyOf.accessible;
	}

	public Cell(Character content) {
		this.content = content;
	}

	public Cell(Character content, boolean barrierBottom, boolean barrierRight) {
		this(content);

		this.barrierBottom = barrierBottom;
		this.barrierRight = barrierRight;
	}

	public Cell(Character content, boolean barrierBottom, boolean barrierRight, boolean accessible) {
		this(content, barrierBottom, barrierRight);

		this.accessible = accessible;
	}

	public Character getContent() {
		return content;
	}

	public void setContent(Character content) {
		this.content = content;
	}

	public boolean isBarrierBottom() {
		return barrierBottom;
	}

	public void setBarrierBottom(boolean barrierBottom) {
		this.barrierBottom = barrierBottom;
	}

	public boolean isBarrierRight() {
		return barrierRight;
	}

	public void setBarrierRight(boolean barrierRight) {
		this.barrierRight = barrierRight;
	}

	public boolean isAccessible() {
		return accessible;
	}

	public void setAccessible(boolean accessible) {
		this.accessible = accessible;
	}

	@Override
	public String toString() {
		// Top row
		StringBuilder res = new StringBuilder();

		// Center row
		res.append(isAccessible() ? content : '#');
		res.append(isBarrierRight() ? "|\n" : " \n");

		// Bottom row
		res.append(isBarrierBottom() ? "-+\n" : " +\n");

		return res.toString();
	}
}
