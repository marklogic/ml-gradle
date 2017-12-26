package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.listener.SetCollectionsListener;

import java.util.Arrays;

public class SetCollectionsJob extends AbstractQueryBatcherJob {

	private String[] collections;

	public SetCollectionsJob() {
		super();

		addRequiredJobProperty("collections", "Comma-delimited list collections to set on selected records",
			value -> setCollections(value.split(",")));
	}

	public SetCollectionsJob(String... collections) {
		this();
		setCollections(collections);
	}

	@Override
	protected String getJobDescription() {
		return "Setting collections " + Arrays.asList(collections) + " on documents " + getQueryDescription();
	}

	public void setCollections(String... collections) {
		this.collections = collections;
		this.addUrisReadyListener(new SetCollectionsListener(collections));
	}
}
