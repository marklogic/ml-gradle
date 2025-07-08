/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.rebalancer

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.resource.rebalancer.PartitionManager
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

class TakePartitionOnlineTask extends MarkLogicTask {

	@TaskAction
	void takePartitionOnline() {
		if (project.hasProperty("partition") && project.hasProperty("database")) {
			String partition = project.property("partition")
			String database = project.property("database")
			println "Taking partition '${partition}' online in database '${database}'"
			new PartitionManager(getManageClient(), database).takePartitionOnline(partition)
			println "Finished taking partition '${partition}' online in database '${database}'"
		} else {
			throw new GradleException("The properties 'database' and 'partition' must be specified")
		}
	}
}
