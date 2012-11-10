package de.davidbilge.cpc.crossword.undo;

import java.util.List;

import com.google.common.collect.ImmutableList;

import de.davidbilge.cpc.crossword.Crossword;

public class MetaUndoOperation implements UndoOperation {
	private final List<UndoOperation> undoOperations;

	public MetaUndoOperation(List<UndoOperation> undoOperations) {
		this.undoOperations = undoOperations;
	}

	public MetaUndoOperation(UndoOperation... undoOperations) {
		this.undoOperations = ImmutableList.copyOf(undoOperations);
	}

	@Override
	public void undo(Crossword crossword) {
		for (UndoOperation undoOperation : this.undoOperations) {
			undoOperation.undo(crossword);
		}
	}

	@Override
	public boolean hasEffect() {
		return undoOperations != null && !undoOperations.isEmpty();
	}

}
