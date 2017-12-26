package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.listener.RemovePermissionsListener;

import java.util.Arrays;

public class RemovePermissionsJob extends AbstractQueryBatcherJob {

	private String[] rolesAndCapabilities;

	public RemovePermissionsJob() {
		super();

		addRequiredJobProperty("permissions",
			"Comma-delimited list of roles and capabilities defining permissions to be removed from selected records",
			value -> setRolesAndCapabilities(value.split(",")));
	}

	public RemovePermissionsJob(String... rolesAndCapabilities) {
		this();
		setRolesAndCapabilities(rolesAndCapabilities);
	}

	@Override
	protected String getJobDescription() {
		return "Removing permissions " + Arrays.asList(rolesAndCapabilities) + " from documents " + getQueryDescription();
	}

	public void setRolesAndCapabilities(String... rolesAndCapabilities) {
		this.rolesAndCapabilities = rolesAndCapabilities;
		this.addUrisReadyListener(new RemovePermissionsListener(rolesAndCapabilities));
	}
}
