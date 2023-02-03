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
