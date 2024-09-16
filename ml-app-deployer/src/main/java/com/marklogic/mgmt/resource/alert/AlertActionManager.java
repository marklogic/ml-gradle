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

public class AlertActionManager extends AbstractResourceManager {

    private String databaseIdOrName;
    private String configUri;

    public AlertActionManager(ManageClient client, String databaseIdOrName, String configUri) {
        super(client);
        this.databaseIdOrName = databaseIdOrName;
        this.configUri = configUri;
    }

    @Override
    public String getResourcesPath() {
        return format("/manage/v2/databases/%s/alert/actions?uri=%s", databaseIdOrName, configUri);
    }

    @Override
    public String getResourcePath(String resourceNameOrId, String... resourceUrlParams) {
        return format("/manage/v2/databases/%s/alert/actions/%s?uri=%s", databaseIdOrName, resourceNameOrId, configUri);
    }

    @Override
    public String getPropertiesPath(String resourceNameOrId, String... resourceUrlParams) {
        return format("/manage/v2/databases/%s/alert/actions/%s/properties?uri=%s", databaseIdOrName, resourceNameOrId,
                configUri);
    }

    @Override
    protected String getIdFieldName() {
        return "name";
    }
}
