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

class ClearContentDatabaseTask extends AbstractConfirmableTask {

	@Override
	void executeIfConfirmed() {
		println "Clearing content database"
		DatabaseManager mgr = new DatabaseManager(getManageClient())
		mgr.clearDatabase(getAppConfig().getContentDatabaseName())
		println "Finished clearing content database"
	}

}
