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
package com.marklogic.appdeployer.command.tasks;

import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.resource.tasks.TaskManager;

public class WaitForTaskServerCommand extends AbstractCommand {

	private String groupName;
	private int retryInSeconds;

	@Override
	public void execute(CommandContext context) {
		TaskManager mgr = new TaskManager(context.getManageClient());
		String group = groupName != null ? groupName : context.getAppConfig().getGroupName();
		int retry = retryInSeconds > 0 ? retryInSeconds : 1;
		mgr.waitForTasksToComplete(group, retry);
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getRetryInSeconds() {
		return retryInSeconds;
	}

	public void setRetryInSeconds(int retryInSeconds) {
		this.retryInSeconds = retryInSeconds;
	}
}
