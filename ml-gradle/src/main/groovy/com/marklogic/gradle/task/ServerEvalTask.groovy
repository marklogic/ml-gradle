/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task

import com.marklogic.client.DatabaseClient
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

class ServerEvalTask extends MarkLogicTask {

	@Input
	@Optional
	DatabaseClient client

	@Input
	@Optional
	String xquery

	@Input
	@Optional
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
