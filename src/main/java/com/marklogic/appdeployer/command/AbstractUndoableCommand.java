package com.marklogic.appdeployer.command;

/**
 * Useful base class for implementing UndoableCommand, allowing you to configure the undo sort order easily.
 */
public abstract class AbstractUndoableCommand extends AbstractCommand implements UndoableCommand {

    private int undoSortOrder = Integer.MAX_VALUE;

    @Override
    public Integer getUndoSortOrder() {
        return undoSortOrder;
    }

    public void setUndoSortOrder(int undoSortOrder) {
        this.undoSortOrder = undoSortOrder;
    }

}
