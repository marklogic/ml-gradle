/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.datamovement.DeleteListener;
import com.marklogic.client.ext.datamovement.CollectionsQueryBatcherBuilder;
import com.marklogic.client.ext.datamovement.QueryBatcherBuilder;

import java.util.Arrays;

public class DeleteCollectionsJob extends AbstractQueryBatcherJob {

	private String[] collections;

	public DeleteCollectionsJob() {
		super();
		setRequireWhereProperty(false);
		addCollectionsProperty(true);
	}

	/**
	 * When this constructor is used - i.e. the collections are known at the time the job is constructed - then
	 * "collections" does not become a required property, as it's already been set.
	 *
	 * @param collections
	 */
	public DeleteCollectionsJob(String... collections) {
		super();
		setRequireWhereProperty(false);
		setCollections(collections);
		if (collections != null && collections.length > 0) {
			addCollectionsProperty(false);
		} else {
			addCollectionsProperty(true);
		}
	}

	private void addCollectionsProperty(boolean required) {
		final String message = "Comma-delimited list of collections to delete";
		if (required) {
			addRequiredJobProperty("collections", message, value -> setCollections(value.split(",")));
		} else {
			addJobProperty("collections", message, value -> setCollections(value.split(",")));
		}
	}

	@Override
	protected QueryBatcherBuilder newQueryBatcherBuilder() {
		return new CollectionsQueryBatcherBuilder(collections);
	}

	@Override
	protected String getJobDescription() {
		return "Deleting collections: " + Arrays.asList(collections);
	}

	public void setCollections(String... collections) {
		this.collections = collections;
		this.addUrisReadyListener(new DeleteListener());
	}

	@Override
	protected void addWhereJobProperties() {
		// These don't apply to this job
	}
}
