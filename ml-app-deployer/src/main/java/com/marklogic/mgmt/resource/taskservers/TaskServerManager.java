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
