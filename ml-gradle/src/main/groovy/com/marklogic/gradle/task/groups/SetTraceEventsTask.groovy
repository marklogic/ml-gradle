/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.groups

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.api.API
import com.marklogic.mgmt.api.group.Group
import org.gradle.api.tasks.TaskAction

class SetTraceEventsTask extends MarkLogicTask {

	@TaskAction
	void setTraceEvents() {
		if (!project.hasProperty("events")) {
			println "Please specify the trace events as a comma-delimited string with a project property of 'events' - e.g. -Pevents=event1,event2"
			return
		}
		API api = new API(getManageClient())
		Group g = new Group(api, getAppConfig().getGroupName())
		g.addEvents(project.property("events").split(","))
		g.saveEvents()
	}
}
