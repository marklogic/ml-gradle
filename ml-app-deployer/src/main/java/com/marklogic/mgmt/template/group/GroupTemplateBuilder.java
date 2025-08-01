/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.template.group;

import com.marklogic.mgmt.api.group.Group;
import com.marklogic.mgmt.template.GenericTemplateBuilder;

public class GroupTemplateBuilder extends GenericTemplateBuilder {

	public GroupTemplateBuilder() {
		super(Group.class);
		addDefaultPropertyValue("group-name", "CHANGEME-name-of-group");
		addDefaultPropertyValue("metering-enabled", "true");
		addDefaultPropertyValue("meters-database", "Meters");
	}
}
