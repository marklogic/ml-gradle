package com.marklogic.appdeployer.command.cpf;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.cpf.AbstractCpfResourceManager;
import com.marklogic.mgmt.resource.cpf.CpfConfigManager;

import java.io.File;

public class DeployCpfConfigsCommand extends AbstractCpfResourceCommand {

	public DeployCpfConfigsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_CPF_CONFIGS);
	}

	@Override
	protected File getCpfResourceDir(ConfigDir configDir) {
		return configDir.getCpfConfigsDir();
	}

	@Override
	protected AbstractCpfResourceManager getResourceManager(CommandContext context, String databaseIdOrName) {
		return new CpfConfigManager(context.getManageClient(), databaseIdOrName);
	}

}
