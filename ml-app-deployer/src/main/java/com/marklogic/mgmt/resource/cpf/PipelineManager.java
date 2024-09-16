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
package com.marklogic.mgmt.resource.cpf;

import com.marklogic.mgmt.ManageClient;

public class PipelineManager extends AbstractCpfResourceManager {

    public PipelineManager(ManageClient client, String databaseIdOrName) {
        super(client, databaseIdOrName);
    }

    public void loadDefaultPipelines() {
        logger.info("Loading default pipelines into database: " + getDatabaseIdOrName());
        getManageClient()
                .postJson(getResourcesPath(), "{\"operation\":\"load-default-cpf-pipelines\"}");
        logger.info("Loaded default pipelines into database: " + getDatabaseIdOrName());
    }
}
