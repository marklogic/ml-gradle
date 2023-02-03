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

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * "User schemas" is intended to be any documents in a schemas database that are loaded by a user as opposed to those
 * created via the deployment of resources such as temporal collections and view schemas.
 */
class DeleteUserSchemasTask extends MarkLogicTask {

	@Input
	@Optional
	String xquery

	@Input
	@Optional
	String database

	@TaskAction
	void deleteUserSchemas() {
		if (xquery == null) {
			xquery = "cts:not-query(" +
				"cts:collection-query((" +
				"'http://marklogic.com/xdmp/temporal/axis', " +
				"'http://marklogic.com/xdmp/temporal/collection', 'http://marklogic.com/xdmp/view'" +
				"))" +
				")"
		}

		if (database == null) {
			database = getAppConfig().getSchemasDatabaseName()
		}


		String fullQuery = "cts:uris((), (), " + xquery + ") ! xdmp:document-delete(.)"
		println "Deleting user schemas in database '" + database + "' via : " + fullQuery

		def client = newClient(database)
		try {
			client.newServerEval().xquery(fullQuery).eval()
		} finally {
			client.release()
		}
	}
}
