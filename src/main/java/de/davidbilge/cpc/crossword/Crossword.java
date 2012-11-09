package de.davidbilge.cpc.crossword;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;

public class Crossword {
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
				cells.put(y, x, new Cell(' '));
			}
		}

		this.width = width;
		this.height = height;
	}

	public Cell getCell(int x, int y) {
		return cells.get(y, x);
	}

	public void setCell(int x, int y, Cell c) {
		cells.put(y, x, c);
	}

	public void setCell(int x, int y, char c) {
		cells.get(y, x).setContent(c);
	}

	public String getWord(int x, int y, Direction direction) {
		Map<Integer, Cell> container;
		int containerOffset;

		if (direction == Direction.ACROSS) {
			container = cells.row(y);
			containerOffset = x;
		} else {
			container = cells.column(x);
			containerOffset = y;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = containerOffset; i < container.size(); ++i) {
			Cell cell = container.get(i);
			sb.append(cell.getContent());

			if ((direction == Direction.ACROSS && cell.isBarrierRight()) || (direction == Direction.DOWN && cell.isBarrierBottom())) {
				break;
			}
		}

		return sb.toString();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
