package com.marklogic.gradle.task

import com.marklogic.client.DatabaseClient
import org.gradle.api.tasks.TaskAction

class ServerEvalTask extends MarkLogicTask {

	DatabaseClient client

	String xquery
	String javascript

	@TaskAction
	void serverEval() {
		if (client == null) {
			client = newClient()
		}
		try {
			String result
			if (xquery != null) {
				result = client.newServerEval().xquery(xquery).evalAs(String.class)
			} else {
				result = client.newServerEval().javascript(javascript).evalAs(String.class)
			}
			if (result != null) {
				println result
			}
		} finally {
			client.release()
		}
	}
}
