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
