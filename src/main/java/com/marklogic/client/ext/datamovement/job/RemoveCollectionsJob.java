package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.CollectionsQueryBatcherBuilder;
import com.marklogic.client.ext.datamovement.QueryBatcherBuilder;
import com.marklogic.client.ext.datamovement.listener.RemoveCollectionsListener;

import java.util.Arrays;

public class RemoveCollectionsJob extends AbstractQueryBatcherJob {

	private String[] collections;

	public RemoveCollectionsJob() {
		super();
		setRequireWhereProperty(false);

		addRequiredJobProperty("collections", "Comma-delimited list of collections from which to remove selected records. " +
				"If no 'where' property is set, then this property also defines the list of collections to select.",
			value -> setCollections(value.split(",")));
	}

	public RemoveCollectionsJob(String... collections) {
		this();
		setCollections(collections);
	}

	@Override
	protected String getJobDescription() {
		String description;
		if (!isWherePropertySet()) {
			description = "in collections " + Arrays.asList(collections);
		} else {
			description = super.getQueryDescription();
		}
		return "Removing documents " + description + " from collections " + Arrays.asList(collections);
	}

	/**
	 * If no "where" property is set, assume that the collections to remove documents from also specifies the set of
	 * documents to perform this operation on.
	 *
	 * @return
	 */
	@Override
	protected QueryBatcherBuilder newQueryBatcherBuilder() {
		QueryBatcherBuilder builder = super.newQueryBatcherBuilder();
		return builder != null ? builder : new CollectionsQueryBatcherBuilder(collections);
	}

	public void setCollections(String... collections) {
		this.collections = collections;
		this.addUrisReadyListener(new RemoveCollectionsListener(collections));
	}
}
