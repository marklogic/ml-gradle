package com.marklogic.mgmt.template.security;

import com.marklogic.mgmt.api.security.Permission;
import com.marklogic.mgmt.api.security.ProtectedCollection;
import com.marklogic.mgmt.template.GenericTemplateBuilder;

import java.util.Arrays;

public class ProtectedCollectionTemplateBuilder extends GenericTemplateBuilder {

	public ProtectedCollectionTemplateBuilder() {
		super(ProtectedCollection.class);
		addDefaultPropertyValue("collection", "CHANGEME-collection-to-protect");
		addDefaultPropertyValue("permission", Arrays.asList(new Permission("rest-reader", "update")));
	}
}
