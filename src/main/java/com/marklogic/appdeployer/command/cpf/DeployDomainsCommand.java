package com.marklogic.appdeployer.command.cpf;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.cpf.AbstractCpfResourceManager;
import com.marklogic.mgmt.resource.cpf.DomainManager;

import java.io.File;

public class DeployDomainsCommand extends AbstractCpfResourceCommand {

	public DeployDomainsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_DOMAINS);
	}

	@Override
	protected File getCpfResourceDir(ConfigDir configDir) {
		return configDir.getDomainsDir();
	}

	@Override
	protected AbstractCpfResourceManager getResourceManager(CommandContext context, String databaseIdOrName) {
		return new DomainManager(context.getManageClient(), databaseIdOrName);
	}

}
