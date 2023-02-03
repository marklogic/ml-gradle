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
package com.marklogic.appdeployer.export.appservers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.export.impl.AbstractNamedResourceExporter;
import com.marklogic.appdeployer.export.ExportedResources;
import com.marklogic.appdeployer.export.databases.DatabaseExporter;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.appservers.ServerManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ServerExporter extends AbstractNamedResourceExporter {

	private String groupName;
	private boolean exportDatabases = true;

	public ServerExporter(ManageClient manageClient, String... serverNames) {
		super(manageClient, serverNames);
	}

	public ServerExporter(String groupName, ManageClient manageClient, String... serverNames) {
		super(manageClient, serverNames);
		this.groupName = groupName;
	}

	@Override
	protected ResourceManager newResourceManager(ManageClient manageClient) {
		return groupName != null ? new ServerManager(manageClient, groupName) : new ServerManager(manageClient);
	}

	@Override
	protected File getResourceDirectory(File baseDir) {
		return new ConfigDir(baseDir).getServersDir();
	}

	@Override
	public ExportedResources exportResources(File baseDir) {
		ExportedResources resources = super.exportResources(baseDir);
		if (isExportDatabases()) {
			resources = exportDatabases(baseDir, resources);
		}
		return resources;
	}

	protected ExportedResources exportDatabases(File baseDir, ExportedResources resources) {
		ServerManager mgr = groupName != null ? new ServerManager(getManageClient(), groupName) : new ServerManager(getManageClient());
		for (String serverName : getResourceNames()) {
			String json = mgr.getPropertiesAsJson(serverName);
			ObjectNode server = (ObjectNode) payloadParser.parseJson(json);
			List<String> dbNames = new ArrayList<>();
			if (server.has("content-database")) {
				dbNames.add(server.get("content-database").textValue());
			}
			if (server.has("modules-database")) {
				dbNames.add(server.get("modules-database").textValue());
			}
			if (!dbNames.isEmpty()) {
				ExportedResources er = new DatabaseExporter(getManageClient(), dbNames.toArray(new String[]{})).exportResources(baseDir);
				resources.add(er);
			}
		}
		return resources;
	}

	public boolean isExportDatabases() {
		return exportDatabases;
	}

	public void setExportDatabases(boolean exportDatabases) {
		this.exportDatabases = exportDatabases;
	}
}
