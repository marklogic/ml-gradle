/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
