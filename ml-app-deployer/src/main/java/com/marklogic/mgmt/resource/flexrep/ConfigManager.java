/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.resource.flexrep;

import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.rest.util.Fragment;

public class ConfigManager extends AbstractResourceManager {

    private String databaseIdOrName;

    public ConfigManager(ManageClient client, String databaseIdOrName) {
        super(client);
        this.databaseIdOrName = databaseIdOrName;
    }

    @Override
    public String getResourcesPath() {
        return format("/manage/v2/databases/%s/flexrep/configs", databaseIdOrName);
    }

    @Override
    protected String getIdFieldName() {
        return "domain-name";
    }

    /**
     * Iterate over every config and delete all the targets first, then delete the config.
     */
    public void deleteAllConfigs() {
        for (String nameref : getAsXml().getListItemNameRefs()) {
            TargetManager mgr = new TargetManager(getManageClient(), this.databaseIdOrName, nameref);
            for (String idref : mgr.getAsXml().getListItemIdRefs()) {
                mgr.deleteByIdField(idref);
            }
            deleteByIdField(nameref);
        }
    }

    public void disableAllFlexrepTargets() {
        for (String nameref : getAsXml().getListItemNameRefs()) {
            new TargetManager(getManageClient(), this.databaseIdOrName, nameref).disableAllTargets();
        }
    }

    public void enableAllFlexrepTargets() {
        for (String nameref : getAsXml().getListItemNameRefs()) {
            new TargetManager(getManageClient(), this.databaseIdOrName, nameref).enableAllTargets();
        }
    }

    public String getDomainId(String configName) {
	    Fragment f =  getManageClient().getXml(getResourcesPath() +"/" + configName);
	    return f.getElementValue("/node()/db:id");
    }

}
