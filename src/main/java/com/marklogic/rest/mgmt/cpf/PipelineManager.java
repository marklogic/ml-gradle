package com.marklogic.rest.mgmt.cpf;

import com.marklogic.rest.mgmt.ManageClient;

public class PipelineManager extends AbstractCpfResourceManager {

    public PipelineManager(ManageClient client) {
        super(client);
    }

    public void loadDefaultPipelines(String databaseIdOrName) {
        logger.info("Loading default pipelines into database: " + databaseIdOrName);
        getManageClient()
                .postJson(getResourcesPath(databaseIdOrName), "{\"operation\":\"load-default-cpf-pipelines\"}");
        logger.info("Loaded default pipelines into database: " + databaseIdOrName);
    }
}
