package com.marklogic.appdeployer.manager;

import java.util.List;

import com.marklogic.appdeployer.AppPlugin;
import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.mgmt.admin.AdminManager;

/**
 * Simple implementation that allows for a list of plugins to be set. Useful for testing purposes in particular - i.e.
 * for testing plugins together.
 */
public class SimpleAppDeployer extends AbstractAppDeployer {

    private List<AppPlugin> appPlugins;

    public SimpleAppDeployer(ManageClient manageClient, AdminManager adminManager) {
        super(manageClient, adminManager);
    }

    @Override
    protected List<AppPlugin> getAppPlugins() {
        return appPlugins;
    }

    public void setAppPlugins(List<AppPlugin> appPlugins) {
        this.appPlugins = appPlugins;
    }

}
