package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.listener.SetPermissionsListener;

import java.util.Arrays;

public class SetPermissionsJob extends AbstractQueryBatcherJob {

	private String[] rolesAndCapabilities;

	public SetPermissionsJob() {
		super();

		addRequiredJobProperty("permissions",
			"Comma-delimited list of roles and capabilities defining permissions to set on selected records",
			value -> setRolesAndCapabilities(value.split(",")));
	}

	public SetPermissionsJob(String... rolesAndCapabilities) {
		this();
		setRolesAndCapabilities(rolesAndCapabilities);
	}

	@Override
	protected String getJobDescription() {
		return "Setting permissions " + Arrays.asList(rolesAndCapabilities) + " on documents " + getQueryDescription();
	}

	public void setRolesAndCapabilities(String... rolesAndCapabilities) {
		this.rolesAndCapabilities = rolesAndCapabilities;
		this.addUrisReadyListener(new SetPermissionsListener(rolesAndCapabilities));
	}
}
