/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
