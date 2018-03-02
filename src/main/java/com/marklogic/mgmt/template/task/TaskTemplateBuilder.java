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
