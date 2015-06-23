package com.marklogic.appdeployer.command.cpf;

import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.rest.mgmt.cpf.AbstractCpfResourceManager;
import com.marklogic.rest.mgmt.cpf.PipelineManager;

public class CreatePipelinesCommand extends AbstractCpfResourceCommand {

    @Override
    public Integer getExecuteSortOrder() {
        return SortOrderConstants.CREATE_PIPELINES_ORDER;
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
