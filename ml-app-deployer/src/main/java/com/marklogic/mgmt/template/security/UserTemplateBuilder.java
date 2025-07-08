/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.template.security;

import com.marklogic.mgmt.api.security.User;
import com.marklogic.mgmt.template.GenericTemplateBuilder;

import java.util.ArrayList;
import java.util.List;

public class UserTemplateBuilder extends GenericTemplateBuilder {

	public UserTemplateBuilder() {
		super(User.class);
		addDefaultPropertyValue("user-name", "CHANGEME-name-of-user");
		addDefaultPropertyValue("description", "CHANGEME description of user");
		addDefaultPropertyValue("password", "CHANGEME");

		List<String> list = new ArrayList<>();
		list.add("rest-reader");
		list.add("rest-writer");
		addDefaultPropertyValue("role", list);
	}
}
