package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.api.configuration.Configurations;
import com.marklogic.mgmt.api.security.Amp;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.mapper.ResourceMapper;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.AmpManager;

import java.io.File;
import java.util.ArrayList;

public class DeployAmpsCommand extends AbstractResourceCommand {

	public DeployAmpsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_AMPS);
		setUndoSortOrder(SortOrderConstants.DELETE_AMPS);
	}

	/**
	 * As of 3.11.0, includes support for deploying amps via CMA.
	 *
	 * @param context
	 */
	@Override
	public void execute(CommandContext context) {
		if (shouldOptimizeWithCma(context)) {
			ResourceMapper resourceMapper = new DefaultResourceMapper(new API(context.getManageClient()));

			Configuration config = new Configuration();
			config.setAmps(new ArrayList<>());
			for (File resourceDir : getResourceDirs(context)) {
				for (File f : listFilesInDirectory(resourceDir, context)) {
					String payload = readResourceFromFile(null, context, f);
					config.getAmps().add(resourceMapper.readResource(payload, Amp.class));
				}
			}
			new Configurations(config).submit(context.getManageClient());
		} else {
			super.execute(context);
		}
	}

	@Override
	protected File[] getResourceDirs(CommandContext context) {
		return findResourceDirs(context, configDir -> configDir.getAmpsDir());
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return new AmpManager(context.getManageClient());
	}

}
