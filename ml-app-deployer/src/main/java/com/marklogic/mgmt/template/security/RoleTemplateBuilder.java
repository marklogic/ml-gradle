/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.template.security;

import com.marklogic.mgmt.api.security.Role;
import com.marklogic.mgmt.template.GenericTemplateBuilder;

public class RoleTemplateBuilder extends GenericTemplateBuilder {

	public RoleTemplateBuilder() {
		super(Role.class);
		addDefaultPropertyValue("role-name", "CHANGEME-name-of-role");
		addDefaultPropertyValue("description", "CHANGEME description of role");
	}

}
