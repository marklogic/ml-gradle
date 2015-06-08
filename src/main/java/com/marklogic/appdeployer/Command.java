package com.marklogic.appdeployer;

/**
 * A command is used to provide additional behavior when creating and deleting an application; it keeps the AppDeployer
 * instance from being tightly coupled to all the things a developer may want to happen as part of creating or deleting
 * an application.
 */
public interface Command {

    /**
     * Configure the application in some way.
     * 
     * @param context
     */
    public void execute(CommandContext context);

    /**
     * Undo whatever done by the execute method.
     * 
     * @param context
     */
    public void undo(CommandContext context);

    /**
     * Return a number corresponding to the order in which this command should execute when an application is being
     * deployed, where the lower the number, the earlier the command is invoked.
     */
    public Integer getExecuteSortOrder();

    /**
     * Return a number corresponding to the order in which this command should execute when an application is being
     * undeployed, where the lower the number, the earlier the command is invoked.
     */
    public Integer getUndoSortOrder();

}
