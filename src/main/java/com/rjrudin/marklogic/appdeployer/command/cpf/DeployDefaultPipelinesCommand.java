package com.rjrudin.marklogic.appdeployer.command.cpf;

import com.rjrudin.marklogic.appdeployer.command.AbstractCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.cpf.PipelineManager;

public class DeployDefaultPipelinesCommand extends AbstractCommand {

    public DeployDefaultPipelinesCommand() {
        setExecuteSortOrder(SortOrderConstants.LOAD_DEFAULT_PIPELINES);
    }

    @Override
    public void execute(CommandContext context) {
        new PipelineManager(context.getManageClient()).loadDefaultPipelines(context.getAppConfig()
                .getTriggersDatabaseName());
    }

}
