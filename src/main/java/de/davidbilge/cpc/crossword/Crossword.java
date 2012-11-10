package de.davidbilge.cpc.crossword;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;

import de.davidbilge.cpc.crossword.undo.CellContentUpdateUndoOperation;
import de.davidbilge.cpc.crossword.undo.CellUpdateUndoOperation;
import de.davidbilge.cpc.crossword.undo.MetaUndoOperation;
import de.davidbilge.cpc.crossword.undo.NoopUndoOperation;
import de.davidbilge.cpc.crossword.undo.UndoOperation;

public class Crossword implements Iterable<Cell> {
	private final Table<Integer, Integer, Cell> cells;
	private final int width, height;

	public Crossword(int width, int height) {
		Preconditions.checkArgument(width > 0);
		Preconditions.checkArgument(height > 0);

		List<Integer> rowKeys = new ArrayList<>();
		List<Integer> columnKeys = new ArrayList<>();

		for (int w = 0; w < width; ++w) {
			columnKeys.add(w);
		}

		for (int h = 0; h < height; ++h) {
			rowKeys.add(h);
		}

		cells = ArrayTable.create(rowKeys, columnKeys);

		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				cells.put(y, x, new Cell('.'));
			}
		}

		this.width = width;
		this.height = height;
	}

	public Cell getCell(int x, int y) {
		return cells.get(y, x);
	}

	@Override
	public Iterator<Cell> iterator() {
		return cells.values().iterator();
	}

	public UndoOperation setCell(int x, int y, Cell c) {
		UndoOperation uo = new CellUpdateUndoOperation(x, y, cells.get(y, x));

		cells.put(y, x, c);

		return uo;
	}

	public UndoOperation setCell(int x, int y, char c) {
		Cell cell = cells.get(y, x);

		UndoOperation uo = new CellContentUpdateUndoOperation(cell.getContent(), x, y);

		cell.setContent(c);

		return uo;
	}

	public String getWord(int x, int y, Direction direction) {
		final StringBuilder sb = new StringBuilder();

		iterateCells(x, y, direction, new CellHandler() {
			@Override
			public void handleCell(Cell cell, int offset, int currentX, int currentY) {
				sb.append(cell.getContent());
			}
		});

		return sb.toString();
	}

	public boolean validEntry(String word, int x, int y, Direction direction) {
		String currentContent = getWord(x, y, direction);

		Pattern p = Pattern.compile(currentContent);

		return p.matcher(word).matches();
	}

	public UndoOperation putWord(final String word, int x, int y, Direction direction, boolean forceInsert) {
		if (!forceInsert && !validEntry(word, x, y, direction)) {
			return new NoopUndoOperation();
		}

		final List<UndoOperation> undoOperations = new ArrayList<>();

		iterateCells(x, y, direction, new CellHandler() {

			@Override
			public void handleCell(Cell cell, int offset, int currentX, int currentY) {
				undoOperations.add(setCell(currentX, currentY, word.charAt(offset)));
			}
		});

		return new MetaUndoOperation(undoOperations);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void undo(UndoOperation undoOperation) {
		undoOperation.undo(this);
	}

	private void iterateCells(int x, int y, Direction direction, CellHandler ch) {
		Map<Integer, Cell> container;
		int containerOffset;

		if (direction == Direction.ACROSS) {
			container = cells.row(y);
			containerOffset = x;
		} else {
			container = cells.column(x);
			containerOffset = y;
		}

		for (int i = containerOffset; i < container.size(); ++i) {
			Cell cell = container.get(i);

			int currentX = direction == Direction.ACROSS ? i : x;
			int currentY = direction == Direction.DOWN ? i : y;

			ch.handleCell(cell, i, currentX, currentY);

			if ((direction == Direction.ACROSS && cell.isBarrierRight()) || (direction == Direction.DOWN && cell.isBarrierBottom())) {
				break;
			}
		}
	}

	private interface CellHandler {
		public void handleCell(Cell cell, int offset, int currentX, int currentY);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		// Top line
		sb.append('+');
		for (int x = 0; x < width; ++x) {
			sb.append("-+");
		}
		sb.append('\n');

		// Other lines
		for (int y = 0; y < height; ++y) {
			sb.append('|');

			// Row content
			for (int x = 0; x < width; ++x) {
				Cell cell = getCell(x, y);

				sb.append(cell.getContent());
				sb.append(cell.isBarrierRight() || x == width - 1 ? '|' : ' ');
			}

			sb.append('\n');

			// Bottom line
			sb.append('+');
			for (int x = 0; x < width; ++x) {
				Cell cell = getCell(x, y);

				sb.append(cell.isBarrierBottom() || y == height - 1 ? '-' : ' ');
				sb.append('+');
			}
			sb.append('\n');
		}

		return sb.toString();
	}

}
