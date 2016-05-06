package com.marklogic.gradle.task.cluster

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.ManageClient
import com.marklogic.mgmt.admin.ActionRequiringRestart
import org.gradle.api.tasks.TaskAction

class RestartClusterTask extends MarkLogicTask {

	@TaskAction
	void restartCluster() {
		final ManageClient client = getManageClient();
		getAdminManager().invokeActionRequiringRestart(new ActionRequiringRestart() {
			public boolean execute() {
				client.postForm("/manage/v2", "state", "restart")
				return true;
			}
		});
	}
}
