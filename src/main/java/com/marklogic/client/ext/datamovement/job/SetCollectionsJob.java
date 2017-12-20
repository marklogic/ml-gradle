package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.listener.SetCollectionsListener;

import java.util.Arrays;

public class SetCollectionsJob extends AbstractQueryBatcherJob {

	private String[] collections;

	public SetCollectionsJob(String... collections) {
		this.collections = collections;
		this.addUrisReadyListener(new SetCollectionsListener(collections));
	}

	@Override
	protected String getJobDescription() {
		return "Setting collections " + Arrays.asList(collections) + " on documents " + getQueryDescription();
	}

}
