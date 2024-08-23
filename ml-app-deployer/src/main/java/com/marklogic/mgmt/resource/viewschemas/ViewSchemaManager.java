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
package com.marklogic.mgmt.resource.viewschemas;

import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;

/**
 * This class requires a database ID or name so that it can build view-schema URLs for that particular database.
 */
public class ViewSchemaManager extends AbstractResourceManager {

    private String databaseIdOrName;

    public ViewSchemaManager(ManageClient client, String databaseIdOrName) {
        super(client);
        this.databaseIdOrName = databaseIdOrName;
    }

    @Override
    protected String getResourceName() {
        return "view-schema";
    }

    @Override
    protected String getIdFieldName() {
        return "view-schema-name";
    }

    @Override
    public String getResourcesPath() {
        return format("/manage/v2/databases/%s/view-schemas", databaseIdOrName);
    }

}
