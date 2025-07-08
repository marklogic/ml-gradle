/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.security;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class CapabilityQuery {

	private String capability;
	// Note that an XML payload will not deserialize into this, nor will it deserialize into an Object
	private ObjectNode query;

	public String getCapability() {
		return capability;
	}

	public void setCapability(String capability) {
		this.capability = capability;
	}

	public ObjectNode getQuery() {
		return query;
	}

	public void setQuery(ObjectNode query) {
		this.query = query;
	}
}
