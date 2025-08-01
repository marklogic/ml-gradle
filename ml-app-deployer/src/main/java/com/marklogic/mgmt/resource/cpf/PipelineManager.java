/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
