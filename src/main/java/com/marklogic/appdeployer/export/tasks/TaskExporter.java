package com.marklogic.appdeployer.export.tasks;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.export.AbstractNamedResourceExporter;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ResourceManager;
import com.marklogic.mgmt.tasks.TaskManager;

import java.io.File;

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
	protected ResourceManager newResourceManager(ManageClient manageClient) {
		return groupName != null ? new TaskManager(manageClient, groupName) : new TaskManager(manageClient);
	}

	@Override
	protected File getResourceDirectory(File baseDir) {
		return new ConfigDir(baseDir).getTasksDir();
	}

	@Override
	protected String[] getResourceUrlParams(String resourceName) {
		return new String[]{"group-id", groupName};
	}

	@Override
	protected String buildFilename(String resourceName, String suffix) {
		int pos = resourceName.lastIndexOf("/");
		String prefix = pos > -1 ? resourceName.substring(pos + 1) : resourceName;
		return prefix + "." + suffix;
	}

	@Override
	protected String beforeResourceWrittenToFile(String resourceName, String payload) {
		return removeJsonKeyFromPayload(payload, "task-id");
	}
}
