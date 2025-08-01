/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.cluster

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.resource.clusters.ClusterManager
import org.gradle.api.tasks.TaskAction

class RemoveHostTask extends MarkLogicTask {

	@TaskAction
	void removeHost() {
		String host = project.property("host")
		ClusterManager mgr = new ClusterManager(getManageClient())
		mgr.removeHost(host)
	}
}
