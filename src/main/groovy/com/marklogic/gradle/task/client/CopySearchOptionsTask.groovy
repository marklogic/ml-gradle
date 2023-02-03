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
package com.marklogic.gradle.task.client

import com.marklogic.client.DatabaseClient
import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Addresses a problem common to DHF options where search options are loaded via the final server and thus only
 * available via that server, but a user would like to use them via the staging server too.
 */
class CopySearchOptionsTask extends MarkLogicTask {

	@Input
	@Optional
	DatabaseClient client

	@Input
	String group = "Default"

	@Input
	String sourceServer

	@Input
	String targetServer

	@Input
	String optionsFilename

	@TaskAction
	void copySearchOptions() {
		DatabaseClient databaseClient
		if (client != null) {
			databaseClient = client
		} else {
			databaseClient = getAppConfig().newModulesDatabaseClient()
		}

		def script = "declareUpdate();\nconst uri = '/${group}/${sourceServer}/rest-api/options/${optionsFilename}';\n"
		script += "xdmp.documentInsert('/${group}/${targetServer}/rest-api/options/${optionsFilename}', cts.doc(uri), " +
			"xdmp.documentGetPermissions(uri), xdmp.documentGetCollections(uri))";
		println "Copying search options via:\n" + script
		databaseClient.newServerEval().javascript(script).eval()
	}

}
