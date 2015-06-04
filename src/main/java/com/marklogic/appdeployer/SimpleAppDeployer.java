package com.marklogic.appdeployer;

import java.util.List;

import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.mgmt.admin.AdminManager;

/**
 * Simple implementation that allows for a list of commands to be set. Useful for testing purposes in particular - i.e.
 * for testing commands together.
 */
public class SimpleAppDeployer extends AbstractAppDeployer {

    private List<Command> commands;

    public SimpleAppDeployer(ManageClient manageClient, AdminManager adminManager) {
        super(manageClient, adminManager);
    }

    @Override
    protected List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

}
