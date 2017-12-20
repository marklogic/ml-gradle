package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.listener.SetPermissionsListener;

import java.util.Arrays;

public class SetPermissionsJob extends AbstractQueryBatcherJob {

	private String[] rolesAndCapabilities;

	public SetPermissionsJob(String... rolesAndCapabilities) {
		this.rolesAndCapabilities = rolesAndCapabilities;
		this.addUrisReadyListener(new SetPermissionsListener(rolesAndCapabilities));
	}

	@Override
	protected String getJobDescription() {
		return "Setting permissions " + Arrays.asList(rolesAndCapabilities) + " on documents " + getQueryDescription();
	}

}
