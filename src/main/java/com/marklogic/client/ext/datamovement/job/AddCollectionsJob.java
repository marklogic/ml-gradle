package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.listener.AddCollectionsListener;

import java.util.Arrays;

public class AddCollectionsJob extends AbstractQueryBatcherJob implements QueryBatcherJob {

	private String[] collections;

	public AddCollectionsJob(String... collections) {
		this.collections = collections;
		this.addUrisReadyListener(new AddCollectionsListener(collections));
	}

	@Override
	protected String getJobDescription() {
		return "Adding documents " + getQueryDescription() + " to collections " + Arrays.asList(collections);
	}

}
