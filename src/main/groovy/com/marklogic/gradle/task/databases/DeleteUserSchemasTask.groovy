package com.marklogic.gradle.task.databases

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

/**
 * "User schemas" is intended to be any documents in a schemas database that are loaded by a user as opposed to those
 * created via the deployment of resources such as temporal collections and view schemas.
 */
class DeleteUserSchemasTask extends MarkLogicTask {

	String xquery
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

		def client = newClient()
		try {
			client.newServerEval().xquery(fullQuery).eval()
		} finally {
			client.release()
		}
	}
}
