/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import com.marklogic.rest.util.ResourcesFragment;

import java.io.File;

public class DeployAmpsCommand extends AbstractResourceCommand implements SupportsCmaCommand {

	private AmpManager ampManager;
	private ResourcesFragment existingAmpResources;

	public DeployAmpsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_AMPS);
		setUndoSortOrder(SortOrderConstants.DELETE_AMPS);

		// Need this to support CMA deployments
		setResourceClassType(Amp.class);
	}

	@Override
	public void execute(CommandContext context) {
		ampManager = new AmpManager(context.getManageClient());
		existingAmpResources = findExistingAmpResources(ampManager);
		super.execute(context);
	}

	/**
	 * Protected so a subclass can override it.
	 *
	 * @param ampManager
	 * @return
	 */
	protected ResourcesFragment findExistingAmpResources(AmpManager ampManager) {
		return ampManager.getAsXml();
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

	/**
	 * Because amps are static and CMA doesn't allow for roles to be changed, we can do some optimization here and
	 * not deploy the amp if it exists - which means its local name, namespace, document URI, and modules database are
	 * all the same.
	 *
	 * @param amp
	 * @param configuration the CMA Configuration object that the payload should be added to
	 */
	@Override
	public void addResourceToConfiguration(ObjectNode amp, Configuration configuration) {
		if (ampIsUnchanged(amp)) {
			logger.info("Amp is unchanged, so not deploying: " + amp.get("local-name"));
		} else {
			configuration.addAmp(amp);
		}
	}

	protected boolean ampIsUnchanged(ObjectNode amp) {
		if (existingAmpResources != null) {
			String localName = amp.get("local-name").asText();
			String namespace = amp.has("namespace") ? amp.get("namespace").asText() : null;
			String documentUri = amp.get("document-uri").asText();
			String modulesDatabase = amp.has("modules-database") ? amp.get("modules-database").asText() : null;
			return ampManager.ampExists(existingAmpResources, localName, documentUri, namespace, modulesDatabase);
		}
		return false;
	}
}
