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
package com.marklogic.mgmt.resource.alert;

import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.SaveReceipt;

public class AlertConfigManager extends AbstractResourceManager {

    private String databaseIdOrName;

    public AlertConfigManager(ManageClient client, String databaseIdOrName) {
        super(client);
        this.databaseIdOrName = databaseIdOrName;
    }

    @Override
    public String getResourcesPath() {
        return format("/manage/v2/databases/%s/alert/configs", databaseIdOrName);
    }

    @Override
    public String getResourcePath(String resourceNameOrId, String... resourceUrlParams) {
        return getResourcesPath();
    }

    @Override
    protected String[] getUpdateResourceParams(String payload) {
        return new String[] { "uri", payloadParser.getPayloadFieldValue(payload, "uri") };
    }

    @Override
    protected String getIdFieldName() {
        return "uri";
    }

    /**
     * This addresses a bug in ML 8.0-4 (36550) where the domains are not saved when an alert config is created, but
     * they are saved when the config is updated. So when the config is first created, we immediately update it to
     * ensure that the domains are setup correctly.
     */
    @Override
    public SaveReceipt save(String payload) {
        SaveReceipt receipt = super.save(payload);
        if (receipt.hasLocationHeader()) {
            if (logger.isInfoEnabled()) {
                logger.info("Immediately updating alert config after it's been created to ensure that CPF domains are set");
            }
            // Calling updateResource instead of save to avoid any chance of an infinite loop
            return super.updateResource(payload, getResourceId(payload));
        }
        return receipt;
    }

    /**
     * Deletes all alert configs, which will also delete all actions and rules associated with each config (this is
     * contrary to deleting a flexrep config, where the associated targets must first be deleted).
     */
    public void deleteAllConfigs() {
        for (String nameref : getAsXml().getListItemNameRefs()) {
            deleteByIdField(nameref);
        }
    }
}
