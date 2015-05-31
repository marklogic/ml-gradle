package com.marklogic.appdeployer.project;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.mgmt.ManageClient;

public interface ProjectPlugin extends Comparable<ProjectPlugin> {

    public void onCreate(AppConfig appConfig, ConfigDir configDir, ManageClient manageClient);

    public void onDelete(AppConfig appConfig, ConfigDir configDir, ManageClient manageClient);

    public Integer getSortOrder();
}
