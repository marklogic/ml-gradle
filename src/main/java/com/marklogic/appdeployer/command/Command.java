package com.marklogic.appdeployer.command;


/**
 * A command is used to provide behavior when creating an application; it keeps the AppDeployer instance from being
 * tightly coupled to all the things a developer may want to happen.
 */
public interface Command {

    /**
     * Configure the application in some way.
     * 
     * @param context
     */
    public void execute(CommandContext context);

    /**
     * Return a number corresponding to the order in which this command should execute when an application is being
     * deployed, where the lower the number, the earlier the command is invoked.
     */
    public Integer getExecuteSortOrder();

}
