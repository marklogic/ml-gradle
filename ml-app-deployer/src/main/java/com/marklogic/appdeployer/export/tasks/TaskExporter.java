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
package com.marklogic.appdeployer.export.tasks;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.export.impl.AbstractNamedResourceExporter;
import com.marklogic.appdeployer.export.impl.ExportInputs;
import com.marklogic.appdeployer.export.impl.SimpleExportInputs;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.tasks.TaskManager;

import java.io.File;

/**
 * This is currently limited to exporting tasks from a single group.
 */
public class TaskExporter extends AbstractNamedResourceExporter {

	private String groupName = "Default";

	public TaskExporter(ManageClient manageClient, String... taskNames) {
		super(manageClient, taskNames);
	}

	public TaskExporter(String groupName, ManageClient manageClient, String... taskNames) {
		super(manageClient, taskNames);
		this.groupName = groupName;
	}

	@Override
	protected File exportToFile(ResourceManager mgr, String resourceName, File resourceDir) {
		return super.exportToFile(mgr, new TaskExportInputs(resourceName, "group-id", groupName), resourceDir);
	}

	@Override
	protected ResourceManager newResourceManager(ManageClient manageClient) {
		return groupName != null ? new TaskManager(manageClient, groupName) : new TaskManager(manageClient);
	}

	@Override
	protected File getResourceDirectory(File baseDir) {
		return new ConfigDir(baseDir).getTasksDir();
	}

	@Override
	protected String beforeResourceWrittenToFile(ExportInputs exportInputs, String payload) {
		return removeJsonKeyFromPayload(payload, "task-id");
	}
}

class TaskExportInputs extends SimpleExportInputs {

	public TaskExportInputs(String resourceName, String... resourceUrlParams) {
		super(resourceName, resourceUrlParams);
	}

	@Override
	public String buildFilename(String suffix) {
		String resourceName = getResourceName();
		int pos = resourceName.lastIndexOf("/");
		String prefix = pos > -1 ? resourceName.substring(pos + 1) : resourceName;
		return prefix + "." + suffix;
	}
}
