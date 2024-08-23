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
package com.marklogic.mgmt.template.task;

import com.marklogic.mgmt.api.task.Task;
import com.marklogic.mgmt.template.GenericTemplateBuilder;

public class TaskTemplateBuilder extends GenericTemplateBuilder {

	public TaskTemplateBuilder() {
		super(Task.class);
		addDefaultPropertyValue("task-path", "/CHANGEME-path-to-module.sjs");
		addDefaultPropertyValue("task-root", "/");
		addDefaultPropertyValue("task-type", "daily");
		addDefaultPropertyValue("task-period", "1");
		addDefaultPropertyValue("task-start-time", "01:00:00");
		addDefaultPropertyValue("task-database", "Documents");
		addDefaultPropertyValue("task-modules", "Modules");
		addDefaultPropertyValue("task-user", "admin");
	}
}
