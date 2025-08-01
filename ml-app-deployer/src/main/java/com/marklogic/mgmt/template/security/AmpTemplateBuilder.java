/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.template.security;

import com.marklogic.mgmt.api.security.Amp;
import com.marklogic.mgmt.template.GenericTemplateBuilder;

import java.util.Arrays;

public class AmpTemplateBuilder extends GenericTemplateBuilder {

	public AmpTemplateBuilder() {
		super(Amp.class);
		addDefaultPropertyValue("local-name", "CHANGEME-name-of-the-function-to-amp");
		addDefaultPropertyValue("namespace", "CHANGEME-namespace-of-the-module");
		addDefaultPropertyValue("document-uri", "CHANGEME-module-path");
		addDefaultPropertyValue("modules-database", "Modules");
		addDefaultPropertyValue("role", Arrays.asList("rest-reader", "rest-writer"));
	}
}
