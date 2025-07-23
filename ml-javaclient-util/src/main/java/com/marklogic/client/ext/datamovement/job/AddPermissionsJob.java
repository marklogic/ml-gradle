/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.listener.AddPermissionsListener;

import java.util.Arrays;

public class AddPermissionsJob extends AbstractQueryBatcherJob {

	private String[] rolesAndCapabilities;

	public AddPermissionsJob() {
		super();

		addRequiredJobProperty("permissions",
			"Comma-delimited list of roles and capabilities defining permissions added to selected records",
			value -> setRolesAndCapabilities(value.split(",")));
	}

	public AddPermissionsJob(String... rolesAndCapabilities) {
		this();
		setRolesAndCapabilities(rolesAndCapabilities);
	}

	@Override
	protected String getJobDescription() {
		return "Adding permissions " + Arrays.asList(rolesAndCapabilities) + " to documents " + getQueryDescription();
	}

	public void setRolesAndCapabilities(String... rolesAndCapabilities) {
		this.rolesAndCapabilities = rolesAndCapabilities;
		this.addUrisReadyListener(new AddPermissionsListener(rolesAndCapabilities));
	}
}
