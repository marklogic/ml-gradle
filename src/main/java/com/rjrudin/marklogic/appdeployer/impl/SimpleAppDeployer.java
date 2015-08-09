package com.rjrudin.marklogic.appdeployer.impl;

import java.util.ArrayList;
import java.util.List;

import com.rjrudin.marklogic.appdeployer.command.Command;
import com.rjrudin.marklogic.mgmt.ManageClient;
import com.rjrudin.marklogic.mgmt.admin.AdminManager;

/**
 * Simple implementation that allows for a list of commands to be set.
 */
public class SimpleAppDeployer extends AbstractAppDeployer {

    private List<Command> commands;

    public SimpleAppDeployer(Command... commandArray) {
        super();
        buildModifiableCommandList(commandArray);
    }

    public SimpleAppDeployer(ManageClient manageClient, AdminManager adminManager, Command... commandArray) {
        super(manageClient, adminManager);
        buildModifiableCommandList(commandArray);
    }

    /**
     * Arrays.asList produces an unmodifiable list, but we want a client to be able to modify the list.
     * 
     * @param commandArray
     */
    protected void buildModifiableCommandList(Command... commandArray) {
        commands = new ArrayList<Command>(commandArray.length);
        for (Command c : commandArray) {
            commands.add(c);
        }
    }

    /**
     * Keep this public so that a client can easily manipulate the list in case a default set of commands has been
     * provided.
     */
    @Override
    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

}
