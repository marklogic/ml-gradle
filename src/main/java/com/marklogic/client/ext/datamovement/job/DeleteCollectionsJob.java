package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.datamovement.DeleteListener;
import com.marklogic.client.ext.datamovement.CollectionsQueryBatcherBuilder;
import com.marklogic.client.ext.datamovement.QueryBatcherBuilder;

import java.util.Arrays;

public class DeleteCollectionsJob extends AbstractQueryBatcherJob {

	private String[] collections;

	public DeleteCollectionsJob() {
		super();
		addRequiredJobProperty("collections", "Comma-delimited list of collections to delete",
			value -> setCollections(value.split(",")));
	}

	public DeleteCollectionsJob(String... collections) {
		this();
		setCollections(collections);
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
