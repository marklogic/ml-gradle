package com.marklogic.appdeployer.command.cpf;

import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.cpf.AbstractCpfResourceManager;
import com.marklogic.mgmt.resource.cpf.PipelineManager;

public class DeployPipelinesCommand extends AbstractCpfResourceCommand {

    public DeployPipelinesCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_PIPELINES);
    }

    @Override
    protected String getCpfDirectoryName() {
        return "pipelines";
    }

    @Override
    protected AbstractCpfResourceManager getResourceManager(CommandContext context) {
        return new PipelineManager(context.getManageClient());
    }
}
