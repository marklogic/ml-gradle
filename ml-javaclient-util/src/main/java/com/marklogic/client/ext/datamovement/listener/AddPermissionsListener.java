/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement.listener;

public class AddPermissionsListener extends AbstractPermissionsListener {

	public AddPermissionsListener(String... rolesAndCapabilities) {
		super(rolesAndCapabilities);
	}

	@Override
	protected String getXqueryFunction() {
		return "xdmp:document-add-permissions";
	}
}
