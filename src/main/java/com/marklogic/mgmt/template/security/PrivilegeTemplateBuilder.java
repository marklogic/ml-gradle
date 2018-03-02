package com.marklogic.mgmt.template.security;

import com.marklogic.mgmt.api.security.Privilege;
import com.marklogic.mgmt.template.GenericTemplateBuilder;

import java.util.Arrays;

public class PrivilegeTemplateBuilder extends GenericTemplateBuilder {

	public PrivilegeTemplateBuilder() {
		super(Privilege.class);
		addDefaultPropertyValue("privilege-name", "CHANGEME-name-of-privilege");
		addDefaultPropertyValue("action", "CHANGEME");
		addDefaultPropertyValue("kind", "execute");
		addDefaultPropertyValue("role", Arrays.asList("rest-reader", "rest-writer"));
	}
}
