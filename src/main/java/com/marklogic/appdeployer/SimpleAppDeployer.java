package com.marklogic.appdeployer;

import java.util.List;

import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.mgmt.admin.AdminManager;

/**
 * Simple implementation that allows for a list of commands to be set.
 */
public class SimpleAppDeployer extends AbstractAppDeployer {

    private List<Command> commands;

    public SimpleAppDeployer(ManageClient manageClient, AdminManager adminManager) {
        super(manageClient, adminManager);
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
