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
package com.marklogic.appdeployer.command.appservers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.command.*;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.api.server.Server;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;

/**
 * "Other" = non-REST-API servers. This will process every JSON/XML file that's not named "rest-api-server.*" in the
 * servers directory.
 */
public class DeployOtherServersCommand extends AbstractResourceCommand implements SupportsCmaCommand {

	/**
	 * Defines the server names that, by default, this command will never undeploy.
	 */
	private Set<String> defaultServersToNotUndeploy = new HashSet<>();

	public DeployOtherServersCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_OTHER_SERVERS);
		setUndoSortOrder(SortOrderConstants.DELETE_OTHER_SERVERS);
		setRestartAfterDelete(true);
		setCatchExceptionOnDeleteFailure(true);
		setResourceFilenameFilter(new ResourceFilenameFilter("rest-api-server.xml", "rest-api-server.json"));

		initializeDefaultServersToNotUndeploy();

		setSupportsResourceMerging(true);
		setResourceClassType(Server.class);
	}

	@Override
	public boolean cmaShouldBeUsed(CommandContext context) {
		return context.getAppConfig().getCmaConfig().isDeployServers();
	}

	@Override
	public void addResourceToConfiguration(ObjectNode resource, Configuration configuration) {
		configuration.addServer(resource);
	}

	protected void initializeDefaultServersToNotUndeploy() {
		defaultServersToNotUndeploy = new HashSet<>();
		defaultServersToNotUndeploy.add("Admin");
		defaultServersToNotUndeploy.add("App-Services");
		defaultServersToNotUndeploy.add("HealthCheck");
		defaultServersToNotUndeploy.add("Manage");
	}

	@Override
	protected File[] getResourceDirs(CommandContext context) {
		return findResourceDirs(context.getAppConfig(), configDir -> configDir.getServersDir());
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return new ServerManager(context.getManageClient(), context.getAppConfig().getGroupName());
	}

	/**
	 * If the payload has a group-name that differs from the group name in the AppConfig, then this returns a new
	 * ServerManager using the group-name in the payload.
	 *
	 * @param mgr
	 * @param context
	 * @param payload
	 * @return
	 */
	@Override
	protected ResourceManager adjustResourceManagerForPayload(ResourceManager mgr, CommandContext context, String payload) {
		String groupName = new PayloadParser().getPayloadFieldValue(payload, "group-name", false);
		if (groupName != null && !groupName.equals(context.getAppConfig().getGroupName())) {
			return new ServerManager(context.getManageClient(), groupName);
		}
		return mgr;
	}

	@Override
	protected String adjustPayloadBeforeDeletingResource(ResourceManager mgr, CommandContext context, File f, String payload) {
		String serverName = new PayloadParser().getPayloadFieldValue(payload, "server-name", false);

		if (serverName != null && !shouldUndeployServer(serverName, context)) {
			logger.info(format("Not undeploying server %s because it's in the list of server names to not undeploy", serverName));
			return null;
		}

		return super.adjustPayloadBeforeDeletingResource(mgr, context, f, payload);
	}

	@Override
	protected BiPredicate<ResourceReference, ResourceReference> getBiPredicateForMergingResources() {
		return (reference1, reference2) -> {
			final ObjectNode node1 = reference1.getObjectNode();
			final ObjectNode node2 = reference2.getObjectNode();

			EqualsBuilder b = new EqualsBuilder();
			b.append(
				node1.has("server-name") ? node1.get("server-name").asText() : null,
				node2.has("server-name") ? node2.get("server-name").asText() : null
			);

			b.append(
				node1.has("group-name") ? node1.get("group-name").asText() : ServerManager.DEFAULT_GROUP,
				node2.has("group-name") ? node2.get("group-name").asText() : ServerManager.DEFAULT_GROUP
			);

			return b.isEquals();
		};
	}

	public boolean shouldUndeployServer(String serverName, CommandContext context) {
		return defaultServersToNotUndeploy == null || !defaultServersToNotUndeploy.contains(serverName);
	}

	public Set<String> getDefaultServersToNotUndeploy() {
		return defaultServersToNotUndeploy;
	}

	public void setDefaultServersToNotUndeploy(Set<String> defaultServersToNotUndeploy) {
		this.defaultServersToNotUndeploy = defaultServersToNotUndeploy;
	}
}
