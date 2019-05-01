package com.marklogic.appdeployer.command.security;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.appdeployer.command.SupportsCmaCommand;
import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.api.security.Amp;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.AmpManager;

import java.io.File;

public class DeployAmpsCommand extends AbstractResourceCommand implements SupportsCmaCommand {

	public DeployAmpsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_AMPS);
		setUndoSortOrder(SortOrderConstants.DELETE_AMPS);

		// Need this to support CMA deployments
		setResourceClassType(Amp.class);
	}

	@Override
	protected File[] getResourceDirs(CommandContext context) {
		return findResourceDirs(context, configDir -> configDir.getAmpsDir());
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return new AmpManager(context.getManageClient());
	}

	@Override
	public boolean cmaShouldBeUsed(CommandContext context) {
		return context.getAppConfig().getCmaConfig().isDeployAmps();
	}

	@Override
	public void addResourceToConfiguration(ObjectNode payload, Configuration configuration) {
		configuration.addAmp(payload);
	}

	@Override
	protected void deployConfiguration(CommandContext context, Configuration config) {
		if (context.getAppConfig().getCmaConfig().isCombineRequests()) {
			logger.info("Adding amps to combined CMA request");
			context.addCmaConfigurationToCombinedRequest(config);
		} else {
			super.deployConfiguration(context, config);
		}
	}
}
