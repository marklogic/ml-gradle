package com.marklogic.mgmt.resource.taskservers;

import com.marklogic.mgmt.AbstractManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.rest.util.Fragment;

public class TaskServerManager extends AbstractManager {

	private ManageClient manageClient;

	public TaskServerManager(ManageClient manageClient) {
		this.manageClient = manageClient;
	}

	public void updateTaskServer(String taskServerName, String payload) {
		String path = format("/manage/v2/task-servers/%s/properties", taskServerName);
		if (payloadParser.isJsonPayload(payload)) {
			manageClient.putJson(path, payload);
		} else {
			manageClient.putXml(path, payload);
		}
	}

	public Fragment getPropertiesAsXml() {
		return getPropertiesAsXml("TaskServer");
	}

	public Fragment getPropertiesAsXml(String taskServerName) {
		return manageClient.getXml(format("/manage/v2/task-servers/%s/properties", taskServerName));
	}
}
