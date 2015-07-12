package com.marklogic.appdeployer.command.cpf;

import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.rest.mgmt.cpf.PipelineManager;

public class LoadDefaultPipelinesCommand extends AbstractCommand {

    public LoadDefaultPipelinesCommand() {
        setExecuteSortOrder(SortOrderConstants.LOAD_DEFAULT_PIPELINES);
    }

    @Override
    public void execute(CommandContext context) {
        new PipelineManager(context.getManageClient()).loadDefaultPipelines(context.getAppConfig()
                .getTriggersDatabaseName());
    }

}
