package de.davidbilge.cpc.crossword.undo;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.davidbilge.cpc.crossword.Crossword;

public class MetaUndoOperation implements UndoOperation {
	private final List<UndoOperation> undoOperations;

	public MetaUndoOperation(List<UndoOperation> undoOperations) {
		this.undoOperations = undoOperations;
	}

	public MetaUndoOperation(UndoOperation... undoOperations) {
		this.undoOperations = ImmutableList.copyOf(undoOperations);
	}

	public MetaUndoOperation(List<UndoOperation> undoOperations, UndoOperation... furtherUndoOperations) {
		this.undoOperations = new ArrayList<>(undoOperations);
		this.undoOperations.addAll(ImmutableList.copyOf(furtherUndoOperations));
	}

	@Override
	public void undo(Crossword crossword) {
		for (UndoOperation undoOperation : Lists.reverse(undoOperations)) {
			undoOperation.undo(crossword);
		}
	}

	@Override
	public boolean hasEffect() {
		return undoOperations != null && !undoOperations.isEmpty();
	}

}
