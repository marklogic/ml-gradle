package com.marklogic.appdeployer.project.plugin;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.mgmt.ManageClient;
import com.marklogic.appdeployer.project.AbstractPlugin;
import com.marklogic.appdeployer.project.ConfigDir;

public class UpdateContentDatabasePlugin extends AbstractPlugin {

    @Override
    public Integer getSortOrderOnCreate() {
        return 200;
    }

    @Override
    public void onCreate(AppConfig appConfig, ConfigDir configDir, ManageClient manageClient) {
    }

    @Override
    public void onDelete(AppConfig appConfig, ConfigDir configDir, ManageClient manageClient) {
    }

}
