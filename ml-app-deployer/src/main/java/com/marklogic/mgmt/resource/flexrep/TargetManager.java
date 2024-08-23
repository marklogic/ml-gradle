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
