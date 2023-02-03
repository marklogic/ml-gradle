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
package com.marklogic.mgmt.template.trigger;

import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.api.trigger.*;
import com.marklogic.mgmt.template.GenericTemplateBuilder;

import java.util.Map;

public class TriggerTemplateBuilder extends GenericTemplateBuilder {

	private String databaseName;

	public TriggerTemplateBuilder(String databaseName) {
		super(Trigger.class);

		this.databaseName = databaseName;

		addDefaultPropertyValue("name", "trigger-name");
		addDefaultPropertyValue("description", "Trigger description");

		Event event = new Event();
		DataEvent dataEvent = new DataEvent();
		event.setDataEvent(dataEvent);
		dataEvent.setCollectionScope(new CollectionScope("some-collection"));
		dataEvent.setDocumentContent(new DocumentContent("create"));
		dataEvent.setWhen("pre-commit");
		addDefaultPropertyValue("event", event);

		addDefaultPropertyValue("module", "/path/to/module.sjs");
		addDefaultPropertyValue("module-db", "Modules");
		addDefaultPropertyValue("module-root", "/");
		addDefaultPropertyValue("enabled", "true");
		addDefaultPropertyValue("recursive", "false");
		addDefaultPropertyValue("task-priority", "normal");
	}

	@Override
	public Resource buildTemplate(Map<String, Object> propertyMap) {
		Resource r = super.buildTemplate(propertyMap);
		((Trigger) r).setDatabaseName(this.databaseName);
		return r;
	}
}
