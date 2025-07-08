/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.resource.flexrep;

import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.rest.util.Fragment;

public class TargetManager extends AbstractResourceManager {

    private String databaseIdOrName;
    private String configIdOrName;

    public TargetManager(ManageClient client, String databaseIdOrName, String configIdOrName) {
        super(client);
        this.databaseIdOrName = databaseIdOrName;
        this.configIdOrName = configIdOrName;
    }

    @Override
    public String getResourcesPath() {
        return format("/manage/v2/databases/%s/flexrep/configs/%s/targets", databaseIdOrName, configIdOrName);
    }

    public void disableAllTargets() {
        for (String name : getAsXml().getListItemNameRefs()) {
            disableTarget(name);
        }
    }

    public void enableAllTargets() {
        for (String name : getAsXml().getListItemNameRefs()) {
            enableTarget(name);
        }
    }

    public void disableTarget(String targetIdOrName) {
        getManageClient().putJson(getPropertiesPath(targetIdOrName), "{\"enabled\":false}");
    }

    public void enableTarget(String targetIdOrName) {
        getManageClient().putJson(getPropertiesPath(targetIdOrName), "{\"enabled\":true}");
    }

    public String getTargetId(String targetName) {
		Fragment f =  getManageClient().getXml(getResourcesPath() +"/" + targetName);
	    return f.getElementValue("/node()/db:id");
    }
}
