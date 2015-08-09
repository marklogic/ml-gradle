package com.rjrudin.marklogic.appdeployer.spring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.rjrudin.marklogic.appdeployer.command.Command;
import com.rjrudin.marklogic.appdeployer.impl.AbstractAppDeployer;
import com.rjrudin.marklogic.mgmt.ManageClient;
import com.rjrudin.marklogic.mgmt.admin.AdminManager;

/**
 * Depends on a Spring ApplicationContext for finding Command instances.
 */
public class SpringAppDeployer extends AbstractAppDeployer {

    private ApplicationContext appContext;

    public SpringAppDeployer(ApplicationContext appContext, ManageClient manageClient, AdminManager adminManager) {
        super(manageClient, adminManager);
        this.appContext = appContext;
    }

    protected List<Command> getCommands() {
        List<Command> commands = new ArrayList<Command>();
        commands.addAll(appContext.getBeansOfType(Command.class).values());
        return commands;
    }
}
