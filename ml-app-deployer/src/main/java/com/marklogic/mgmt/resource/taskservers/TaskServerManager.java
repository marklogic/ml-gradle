/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.resource.taskservers;

import com.marklogic.mgmt.AbstractManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.admin.AdminManager;
import com.marklogic.rest.util.Fragment;
import org.springframework.http.ResponseEntity;

public class TaskServerManager extends AbstractManager {

	private ManageClient manageClient;

	public TaskServerManager(ManageClient manageClient) {
		this.manageClient = manageClient;
	}

	/**
	 *
	 * @param taskServerName
	 * @param payload
	 * @param adminManager required in the event that the update to the task server causes a restart
	 */
	public void updateTaskServer(String taskServerName, String payload, AdminManager adminManager) {
		String path = format("/manage/v2/task-servers/%s/properties", taskServerName);

		ResponseEntity<String> response;
		if (payloadParser.isJsonPayload(payload)) {
			response = manageClient.putJson(path, payload);
		} else {
			response = manageClient.putXml(path, payload);
		}

		if (response != null && response.getHeaders().getLocation() != null && adminManager != null) {
			adminManager.waitForRestart();
		}
	}

	public Fragment getPropertiesAsXml() {
		return getPropertiesAsXml("TaskServer");
	}

	public Fragment getPropertiesAsXml(String taskServerName) {
		return manageClient.getXml(format("/manage/v2/task-servers/%s/properties", taskServerName));
	}
}
