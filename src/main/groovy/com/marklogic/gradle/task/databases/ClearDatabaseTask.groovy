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
package com.marklogic.gradle.task.databases

import com.marklogic.gradle.task.AbstractConfirmableTask
import com.marklogic.mgmt.resource.databases.DatabaseManager
import org.gradle.api.GradleException

class ClearDatabaseTask extends AbstractConfirmableTask {

	@Override
	void executeIfConfirmed() {
		if (project.hasProperty("database")) {
			String db = project.property("database")
			println "Clearing database: " + db
			new DatabaseManager(getManageClient()).clearDatabase(db)
			println "Finished clearing database: " + db
		} else {
			throw new GradleException("The property 'database' must be specified")
		}
	}
}
