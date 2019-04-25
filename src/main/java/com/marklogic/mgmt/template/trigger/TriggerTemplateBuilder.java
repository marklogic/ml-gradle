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
