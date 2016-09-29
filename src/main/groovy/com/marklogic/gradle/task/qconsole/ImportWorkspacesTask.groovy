package com.marklogic.gradle.task.qconsole

import com.marklogic.client.DatabaseClient
import com.marklogic.client.qconsole.impl.DefaultWorkspaceManager
import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class ImportWorkspacesTask extends MarkLogicTask {

	@TaskAction
	void importWorkspaces() {
		DatabaseClient client = newClient()
		try {
			String user = project.property("user")
			String workspaceNames = project.property("workspaceNames")
			DefaultWorkspaceManager mgr = new DefaultWorkspaceManager(client);
			def files = mgr.importWorkspaces(user, workspaceNames.split(","))
			for (f in files) {
				println "Imported workspace from " + f
			}
		} finally {
			client.release()
		}
	}
}
