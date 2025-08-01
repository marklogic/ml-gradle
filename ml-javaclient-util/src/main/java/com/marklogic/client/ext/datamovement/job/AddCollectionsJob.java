/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.listener.AddCollectionsListener;

import java.util.Arrays;

public class AddCollectionsJob extends AbstractQueryBatcherJob implements QueryBatcherJob {

	private String[] collections;

	public AddCollectionsJob() {
		super();
		addRequiredJobProperty("collections", "Comma-delimited list collections to which selected records are added",
			value -> setCollections(value.split(",")));
	}

	public AddCollectionsJob(String... collections) {
		this();
		setCollections(collections);
	}

	@Override
	protected String getJobDescription() {
		return "Adding documents " + getQueryDescription() + " to collections " + Arrays.asList(collections);
	}

	public void setCollections(String... collections) {
		this.collections = collections;
		this.addUrisReadyListener(new AddCollectionsListener(collections));
	}
}
