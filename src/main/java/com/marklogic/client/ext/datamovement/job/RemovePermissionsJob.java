package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.listener.RemovePermissionsListener;

import java.util.Arrays;

public class RemovePermissionsJob extends AbstractQueryBatcherJob {

	private String[] rolesAndCapabilities;

	public RemovePermissionsJob(String... rolesAndCapabilities) {
		this.rolesAndCapabilities = rolesAndCapabilities;
		this.addUrisReadyListener(new RemovePermissionsListener(rolesAndCapabilities));
	}

	@Override
	protected String getJobDescription() {
		return "Removing permissions " + Arrays.asList(rolesAndCapabilities) + " from documents " + getQueryDescription();
	}
}
