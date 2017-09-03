package com.marklogic.gradle.task.qconsole

import com.marklogic.client.DatabaseClient
import com.marklogic.client.ext.qconsole.impl.DefaultWorkspaceManager
import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class ExportWorkspacesTask extends MarkLogicTask {

	@TaskAction
	void exportWorkspaces() {
		DatabaseClient client = newClient()
		try {
			String user = project.property("user")
			String workspaceNames = project.property("workspaceNames")
			DefaultWorkspaceManager mgr = new DefaultWorkspaceManager(client);
			def files = mgr.exportWorkspaces(user, workspaceNames.split(","))
			for (f in files) {
				println "Exported workspace to " + f
			}
		} finally {
			client.release()
		}
	}
}
