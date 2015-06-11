package com.marklogic.appdeployer.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.AppDeployer;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.UndoableCommand;
import com.marklogic.clientutil.LoggingObject;
import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.mgmt.admin.AdminManager;

/**
 * Abstract base class that just needs the subclass to define the list of Command instances to use. Handles executing
 * commands in sorted order.
 */
public abstract class AbstractAppDeployer extends LoggingObject implements AppDeployer {

    private ManageClient manageClient;
    private AdminManager adminManager;

    public AbstractAppDeployer(ManageClient manageClient, AdminManager adminManager) {
        super();
        this.manageClient = manageClient;
        this.adminManager = adminManager;
    }

    protected abstract List<Command> getCommands();

    public void deploy(AppConfig appConfig) {
        logger.info(format("Deploying app %s with config dir of: %s", appConfig.getName(), appConfig.getConfigDir()
                .getBaseDir().getAbsolutePath()));

        List<Command> commands = getCommands();
        Collections.sort(commands, new ExecuteComparator());

        CommandContext context = new CommandContext(appConfig, manageClient, adminManager);

        for (Command command : commands) {
            logger.info(format("Invoking command [%s] with sort order [%d]", command.getClass().getName(),
                    command.getExecuteSortOrder()));
            command.execute(context);
        }

        logger.info(format("Deployed app %s", appConfig.getName()));
    }

    public void undeploy(AppConfig appConfig) {
        logger.info(format("Undeploying app %s with config dir: %s", appConfig.getName(), appConfig.getConfigDir()
                .getBaseDir().getAbsolutePath()));

        List<Command> commands = getCommands();

        List<UndoableCommand> undoableCommands = new ArrayList<UndoableCommand>();
        for (Command command : commands) {
            if (command instanceof UndoableCommand) {
                undoableCommands.add((UndoableCommand) command);
            }
        }

        Collections.sort(undoableCommands, new UndoComparator());

        for (UndoableCommand command : undoableCommands) {
            logger.info(format("Invoking command [%s] with sort order [%d]", command.getClass().getName(),
                    command.getUndoSortOrder()));

            CommandContext context = new CommandContext(appConfig, manageClient, adminManager);
            command.undo(context);
        }

        logger.info(format("Undeployed app %s", appConfig.getName()));
    }

    protected String format(String s, Object... args) {
        return String.format(s, args);
    }
}

class ExecuteComparator implements Comparator<Command> {
    @Override
    public int compare(Command o1, Command o2) {
        return o1.getExecuteSortOrder().compareTo(o2.getExecuteSortOrder());
    }
}

class UndoComparator implements Comparator<UndoableCommand> {
    @Override
    public int compare(UndoableCommand o1, UndoableCommand o2) {
        return o1.getUndoSortOrder().compareTo(o2.getUndoSortOrder());
    }
}