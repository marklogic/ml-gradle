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
