package de.davidbilge.cpc.crossword;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;

import de.davidbilge.cpc.crossword.undo.CellAccessibilityUndoOperation;
import de.davidbilge.cpc.crossword.undo.CellContentUpdateUndoOperation;
import de.davidbilge.cpc.crossword.undo.CellUpdateUndoOperation;
import de.davidbilge.cpc.crossword.undo.MetaUndoOperation;
import de.davidbilge.cpc.crossword.undo.NoopUndoOperation;
import de.davidbilge.cpc.crossword.undo.UndoOperation;

public class Crossword implements Iterable<Cell> {
	private final Table<Integer, Integer, Cell> cells;
	private final int width, height;

	public Crossword(Crossword original) {
		this.width = original.width;
		this.height = original.height;

		this.cells = ArrayTable.create(original.cells);
	}

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
				cells.put(y, x, new Cell(Cell.EMPTY_CHARACTER));
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

	public UndoOperation setCellAccessible(int x, int y, boolean accessible) {
		Cell cell = cells.get(y, x);
		UndoOperation uo;

		if (cell.isAccessible() == accessible) {
			uo = new NoopUndoOperation();
		} else {
			uo = new CellAccessibilityUndoOperation(x, y, cell.isAccessible());
			cell.setAccessible(accessible);
		}

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
		String currentContent = regexify(getWord(x, y, direction));

		Pattern p = Pattern.compile(currentContent);

		return p.matcher(word).matches();
	}

	public static String regexify(String word) {
		for (int i = word.length(); i > 0; --i) {
			String suffix = StringUtils.right(word, i);

			if (StringUtils.containsOnly(suffix, Cell.EMPTY_CHARACTER)) {
				return StringUtils.removeEnd(word, suffix) + ".{0," + i + "}";
			}
		}

		return word;
	}

	public UndoOperation putWord(final String word, int x, int y, final Direction direction, boolean forceInsert) {
		if (!forceInsert && !validEntry(word, x, y, direction)) {
			return new NoopUndoOperation();
		}

		final List<UndoOperation> undoOperations = new ArrayList<>();

		iterateCells(x, y, direction, new CellHandler() {

			@Override
			public void handleCell(Cell cell, int offset, int currentX, int currentY) {
				if (offset < word.length()) {
					if (offset == word.length() - 1) {
						undoOperations.add(new CellUpdateUndoOperation(currentX, currentY, cell));

						if (direction == Direction.ACROSS) {
							cell.setBarrierRight(true);
						} else {
							cell.setBarrierBottom(true);
						}
					}

					undoOperations.add(setCell(currentX, currentY, word.charAt(offset)));
				}
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

	public Position findStartOfFirstIncompleteWord(Direction direction) {
		Position pivotCell = findFirstEmptyCell(direction);

		// Scan back
		Map<Integer, Cell> container;
		int containerOffset;

		if (direction == Direction.ACROSS) {
			container = cells.row(pivotCell.y);
			containerOffset = pivotCell.x;
		} else {
			container = cells.column(pivotCell.x);
			containerOffset = pivotCell.y;
		}

		if (containerOffset == 0) {
			return pivotCell;
		}

		int startOffset = containerOffset;
		while (startOffset > 0) {
			Cell previousCell = container.get(startOffset - 1);

			if ((direction == Direction.ACROSS && previousCell.isBarrierRight()) || (direction == Direction.DOWN && previousCell.isBarrierBottom())) {
				break;
			}

			--startOffset;
		}

		Position startPos;
		if (direction == Direction.ACROSS) {
			startPos = new Position(startOffset, pivotCell.y);
		} else {
			startPos = new Position(pivotCell.x, startOffset);
		}

		return startPos;

	}

	public Position findFirstEmptyCell(Direction direction) {
		if (direction == Direction.DOWN) {
			for (int x = 0; x < getWidth(); ++x) {
				for (int y = 0; y < getHeight(); ++y) {
					Cell cell = getCell(x, y);
					if (cell.isAccessible() && cell.getContent().equals(Cell.EMPTY_CHARACTER)) {
						return new Position(x, y);
					}
				}
			}
		} else {
			for (int y = 0; y < getHeight(); ++y) {
				for (int x = 0; x < getWidth(); ++x) {
					Cell cell = getCell(x, y);
					if (cell.isAccessible() && cell.getContent().equals(Cell.EMPTY_CHARACTER)) {
						return new Position(x, y);
					}
				}
			}
		}

		return null;
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

		for (int i = containerOffset; i < container.size(); i++) {
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
