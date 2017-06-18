package com.marklogic.gradle.task.cluster

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.resource.clusters.ClusterManager
import org.gradle.api.tasks.TaskAction

class AddHostTask extends MarkLogicTask {

	@TaskAction
	void addHost() {
		String host = project.property("host")
		String group = project.hasProperty("hostGroup") ? project.property("hostGroup") : "Default"
		String zone = project.hasProperty("hostZone") ? project.property("hostZone") : null

		ClusterManager mgr = new ClusterManager(getManageClient())
		mgr.addHost(getAdminManager(), host, group, zone)
	}
}
