package com.marklogic.appdeployer.command;


/**
 * Implement this interface for when your command needs to perform some action when an application is being undeployed.
 */
public interface UndoableCommand extends Command {

    /**
     * Undo whatever done by the execute method.
     * 
     * @param context
     */
    public void undo(CommandContext context);

    /**
     * Return a number corresponding to the order in which this command should execute when an application is being
     * undeployed, where the lower the number, the earlier the command is invoked.
     */
    public Integer getUndoSortOrder();

}
