package com.rjrudin.marklogic.appdeployer.command.cpf;

import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.cpf.AbstractCpfResourceManager;
import com.rjrudin.marklogic.mgmt.cpf.PipelineManager;

public class DeployPipelinesCommand extends AbstractCpfResourceCommand {

    public DeployPipelinesCommand() {
        setExecuteSortOrder(SortOrderConstants.CREATE_PIPELINES);
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
