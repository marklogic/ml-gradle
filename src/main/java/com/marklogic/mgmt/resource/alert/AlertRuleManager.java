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

public class AlertRuleManager extends AbstractResourceManager {

    private String databaseIdOrName;
    private String configUri;
    private String actionIdOrName;

    /**
     * The actionIdOrName is required so that getResourcesPath() works. According to the ML docs, the action-name should
     * always be present in the alert payload, but we don't have a payload when calling getResourcesPath().
     *
     * @param client
     * @param databaseIdOrName
     * @param configUri
     * @param actionIdOrName
     */
    public AlertRuleManager(ManageClient client, String databaseIdOrName, String configUri, String actionIdOrName) {
        super(client);
        this.databaseIdOrName = databaseIdOrName;
        this.configUri = configUri;
        this.actionIdOrName = actionIdOrName;
    }

    @Override
    protected String getIdFieldName() {
        return "name";
    }

    @Override
    public String getResourcesPath() {
        return format("/manage/v2/databases/%s/alert/actions/%s/rules?uri=%s", databaseIdOrName, actionIdOrName,
                configUri);
    }

    @Override
    public String getResourcePath(String resourceNameOrId, String... resourceUrlParams) {
        return format("/manage/v2/databases/%s/alert/actions/%s/rules/%s?uri=%s", databaseIdOrName, actionIdOrName,
                resourceNameOrId, configUri);
    }

    @Override
    public String getPropertiesPath(String resourceNameOrId, String... resourceUrlParams) {
        return format("/manage/v2/databases/%s/alert/actions/%s/rules/%s/properties?uri=%s", databaseIdOrName,
                actionIdOrName, resourceNameOrId, configUri);
    }
}
