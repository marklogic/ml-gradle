package com.marklogic.gradle.task.cluster

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.ManageClient
import com.marklogic.mgmt.resource.clusters.ClusterManager
import org.gradle.api.tasks.TaskAction

class RestartClusterTask extends MarkLogicTask {

	@TaskAction
	void restartCluster() {
		final ManageClient client = getManageClient();
		println "Restarting local cluster"
		new ClusterManager(client).restartLocalCluster(getAdminManager())
	}
}
