package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.listener.AddPermissionsListener;

import java.util.Arrays;

public class AddPermissionsJob extends AbstractQueryBatcherJob {

	private String[] rolesAndCapabilities;

	public AddPermissionsJob(String... rolesAndCapabilities) {
		this.rolesAndCapabilities = rolesAndCapabilities;
		this.addUrisReadyListener(new AddPermissionsListener(rolesAndCapabilities));
	}

	@Override
	protected String getJobDescription() {
		return "Adding permissions " + Arrays.asList(rolesAndCapabilities) + " to documents " + getQueryDescription();
	}

}
