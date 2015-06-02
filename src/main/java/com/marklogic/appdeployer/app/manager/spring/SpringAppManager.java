package com.marklogic.appdeployer.app.manager.spring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.marklogic.appdeployer.app.AppPlugin;
import com.marklogic.appdeployer.app.manager.AbstractAppManager;
import com.marklogic.appdeployer.mgmt.ManageClient;
import com.marklogic.appdeployer.mgmt.admin.AdminManager;

/**
 * Manages creating and deleting an app - i.e. looks for files in the ConfigDir and makes the appropriate calls to the
 * Mgmt API using "NounManager" classes. This is the class that something like a Gradle plugin would interact with, and
 * hopefully only this class.
 */
public class SpringAppManager extends AbstractAppManager {

    private ApplicationContext appContext;

    public SpringAppManager(ApplicationContext appContext, ManageClient manageClient, AdminManager adminManager) {
        super(manageClient, adminManager);
        this.appContext = appContext;
    }

    protected List<AppPlugin> getAppPlugins() {
        List<AppPlugin> plugins = new ArrayList<AppPlugin>();
        plugins.addAll(appContext.getBeansOfType(AppPlugin.class).values());
        return plugins;
    }
}
