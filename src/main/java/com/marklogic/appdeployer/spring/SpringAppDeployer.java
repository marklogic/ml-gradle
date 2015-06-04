package com.marklogic.appdeployer.spring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.marklogic.appdeployer.AbstractAppDeployer;
import com.marklogic.appdeployer.Command;
import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.mgmt.admin.AdminManager;

/**
 * Depends on a Spring ApplicationContext for finding AppPlugin instances.
 */
public class SpringAppDeployer extends AbstractAppDeployer {

    private ApplicationContext appContext;

    public SpringAppDeployer(ApplicationContext appContext, ManageClient manageClient, AdminManager adminManager) {
        super(manageClient, adminManager);
        this.appContext = appContext;
    }

    protected List<Command> getCommands() {
        List<Command> plugins = new ArrayList<Command>();
        plugins.addAll(appContext.getBeansOfType(Command.class).values());
        return plugins;
    }
}
