package com.marklogic.appdeployer.command.cpf;

import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.cpf.PipelineManager;

public class DeployDefaultPipelinesCommand extends AbstractCommand {

	public DeployDefaultPipelinesCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_DEFAULT_PIPELINES);
	}

	@Override
	public void execute(CommandContext context) {
		new PipelineManager(context.getManageClient(), context.getAppConfig().getTriggersDatabaseName())
			.loadDefaultPipelines();
	}

}
