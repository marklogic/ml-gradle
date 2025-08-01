/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.ext.datamovement.*;
import com.marklogic.client.ext.datamovement.listener.SimpleBatchLoggingListener;
import com.marklogic.client.ext.datamovement.util.TransformPropertyValueParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Provides basic plumbing for implementing QueryBatcherJob.
 */
public abstract class AbstractQueryBatcherJob extends BatcherConfig implements QueryBatcherJob, ConfigurableJob {

	private List<JobProperty> jobProperties = new ArrayList<>();

	private List<QueryBatchListener> urisReadyListeners;
	private List<QueryFailureListener> queryFailureListeners;

	private boolean consistentSnapshot = true;
	private boolean awaitCompletion = true;
	private boolean stopJobAfterCompletion = true;

	// A client can provide its own DataMovementManager to be reused
	private DataMovementManager dataMovementManager;

	// A client can provide its own QueryBatcherBuilder in case it doesn't want to use the "where" properties
	private QueryBatcherBuilder queryBatcherBuilder;

	private String[] whereUris;
	private String[] whereCollections;
	private String whereUriPattern;
	private String whereUrisQuery;
	private boolean requireWhereProperty = true;

	/**
	 * @return a description of the job that is useful for logging purposes.
	 */
	protected abstract String getJobDescription();

	protected AbstractQueryBatcherJob() {
		addQueryBatcherJobProperties();
	}

	@Override
	public QueryBatcherJobTicket run(DatabaseClient databaseClient) {
		DataMovementManager dmm = this.dataMovementManager != null ? this.dataMovementManager : databaseClient.newDataMovementManager();

		String jobDescription = getJobDescription();
		if (jobDescription != null && logger.isInfoEnabled()) {
			logger.info(jobDescription);
		}

		QueryBatcherBuilder builder = newQueryBatcherBuilder();
		QueryBatcher queryBatcher = builder.buildQueryBatcher(databaseClient, dmm);

		prepareQueryBatcher(queryBatcher);

		JobTicket jobTicket = dmm.startJob(queryBatcher);

		if (awaitCompletion) {
			queryBatcher.awaitCompletion();
			if (stopJobAfterCompletion) {
				dmm.stopJob(queryBatcher);
			}
			if (jobDescription != null && logger.isInfoEnabled()) {
				logger.info("Completed: " + jobDescription);
			}
		}

		return new QueryBatcherJobTicket(dmm, queryBatcher, jobTicket);
	}

	@Override
	public List<String> configureJob(Properties props) {
		List<String> messages = new ArrayList<>();

		for (JobProperty jobProperty : this.jobProperties) {
			String name = jobProperty.getPropertyName();
			String value = props.getProperty(name);
			if (value != null && value.trim().length() > 0) {
				jobProperty.getPropertyValueConsumer().accept(value);
			} else if (jobProperty.isRequired()) {
				messages.add("The property '" + name + "' is required");
			}
		}

		if (requireWhereProperty && !isWherePropertySet() && queryBatcherBuilder == null) {
			messages.add("At least one 'where' property must be set for selecting records to process");
		}

		return messages;
	}

	@Override
	public List<JobProperty> getJobProperties() {
		return jobProperties;
	}

	protected void addQueryBatcherJobProperties() {
		addJobProperty("batchSize", "Number of records to process at once; defaults to " + DEFAULT_BATCH_SIZE,
			value -> setBatchSize(Integer.parseInt(value)));

		addJobProperty("consistentSnapshot", "Whether or not to apply a consistent snapshot to the query for records; defaults to true",
			value -> setConsistentSnapshot(Boolean.parseBoolean(value)));

		addJobProperty("jobId", "Optional ID for the Data Movement job", value -> setJobId(value));

		addJobProperty("jobName", "Optional name for the Data Movement job", value -> setJobName(value));

		addJobProperty("logBatches", "Log each batch to stdout as it's processed",
			value -> addUrisReadyListener(new SimpleBatchLoggingListener()));

		addJobProperty("logBatchesWithLogger", "Log each batch as it's processed at the info-level using SLF4J",
			value -> addUrisReadyListener(new SimpleBatchLoggingListener(true)));

		addJobProperty("threadCount", "Number of threads to process records with; default to " + DEFAULT_THREAD_COUNT,
			value -> setThreadCount(Integer.parseInt(value)));

		addWhereJobProperties();
	}

	protected void addWhereJobProperties() {
		addJobProperty("whereCollections", "Comma-delimited list of collections for selecting records to process",
			value -> setWhereCollections(value.split(",")));

		addJobProperty("whereUriPattern", "URI pattern for selecting records to process",
			value -> setWhereUriPattern(value));

		addJobProperty("whereUris", "Comma-delimited list of URIs for selecting records to process",
			value -> setWhereUris(value.split(",")));

		addJobProperty("whereUrisQuery", "CTS URIs query for selecting records to process",
			value -> setWhereUrisQuery(value));
	}

	protected void addJobProperty(String name, String description, Consumer<String> propertyValueConsumer) {
		jobProperties.add(new SimpleJobProperty(name, description, propertyValueConsumer));
	}

	protected void addRequiredJobProperty(String name, String description, Consumer<String> propertyValueConsumer) {
		SimpleJobProperty prop = new SimpleJobProperty(name, description, propertyValueConsumer);
		prop.setRequired(true);
		jobProperties.add(prop);
	}

	/**
	 * Several jobs support a transform parameter, and we want each of them to inherit the support for providing
	 * optional parameters after the transform name.
	 *
	 * @param consumer
	 */
	protected void addTransformJobProperty(BiConsumer<String, ServerTransform> consumer) {
		addJobProperty("transform", "The name of a REST transform to apply to each record. Parameters can be passed " +
				"to the transform by appending them to the value of this property, delimited by commas - e.g. myTransform,param1,value1,param2,value2",
			value -> consumer.accept(value, TransformPropertyValueParser.parsePropertyValue(value))
		);
	}

	/**
	 * Can be overridden by the subclass to prepare the QueryBatcher before the job is started.
	 *
	 * @param queryBatcher
	 */
	protected void prepareQueryBatcher(QueryBatcher queryBatcher) {
		super.prepareBatcher(queryBatcher);

		if (consistentSnapshot) {
			queryBatcher.withConsistentSnapshot();
		}

		if (urisReadyListeners != null) {
			for (QueryBatchListener listener : urisReadyListeners) {
				queryBatcher.onUrisReady(listener);
			}
		}

		if (queryFailureListeners != null) {
			for (QueryFailureListener listener : queryFailureListeners) {
				queryBatcher.onQueryFailure(listener);
			}
		}
	}

	/**
	 * @return
	 */
	protected QueryBatcherBuilder newQueryBatcherBuilder() {
		if (queryBatcherBuilder != null) {
			return queryBatcherBuilder;
		}

		if (whereUris != null && whereUris.length > 0) {
			return new DocumentUrisQueryBatcherBuilder(whereUris);
		}
		if (whereCollections != null) {
			return new CollectionsQueryBatcherBuilder(whereCollections);
		}
		if (whereUriPattern != null) {
			return new UriPatternQueryBatcherBuilder(whereUriPattern);
		}
		if (whereUrisQuery != null) {
			return new UrisQueryQueryBatcherBuilder(whereUrisQuery);
		}
		return null;
	}

	/**
	 * @return an expression describing the query, based on the "where" properties in this class, that will be used to
	 * select documents
	 */
	protected String getQueryDescription() {
		if (this.queryBatcherBuilder != null) {
			return "with custom query";
		}

		if (whereUris != null && whereUris.length > 0) {
			return "with URIs " + Arrays.asList(whereUris);
		} else if (whereCollections != null && whereCollections.length > 0) {
			return "in collections " + Arrays.asList(this.whereCollections);
		} else if (whereUriPattern != null) {
			return "matching URI pattern [" + whereUriPattern + "]";
		} else if (whereUrisQuery != null) {
			return "matching URIs query [" + whereUrisQuery + "]";
		}

		return null;
	}


	protected boolean isWherePropertySet() {
		return
			(whereUris != null && whereUris.length > 0)
				|| (whereCollections != null && whereCollections.length > 0)
				|| whereUriPattern != null
				|| whereUrisQuery != null;
	}

	public void addUrisReadyListener(QueryBatchListener listener) {
		if (urisReadyListeners == null) {
			urisReadyListeners = new ArrayList<>();
		}
		urisReadyListeners.add(listener);
	}

	public void addQueryFailureListener(QueryFailureListener listener) {
		if (queryFailureListeners == null) {
			queryFailureListeners = new ArrayList<>();
		}
		queryFailureListeners.add(listener);
	}


	public String[] getWhereCollections() {
		return whereCollections;
	}

	public AbstractQueryBatcherJob setWhereCollections(String... whereCollections) {
		this.whereCollections = whereCollections;
		return this;
	}

	public String getWhereUriPattern() {
		return whereUriPattern;
	}

	public AbstractQueryBatcherJob setWhereUriPattern(String whereUriPattern) {
		this.whereUriPattern = whereUriPattern;
		return this;
	}

	public String getWhereUrisQuery() {
		return whereUrisQuery;
	}

	public AbstractQueryBatcherJob setWhereUrisQuery(String whereUrisQuery) {
		this.whereUrisQuery = whereUrisQuery;
		return this;
	}

	public String[] getWhereUris() {
		return whereUris;
	}

	public AbstractQueryBatcherJob setWhereUris(String... whereUris) {
		this.whereUris = whereUris;
		return this;
	}

	public List<QueryBatchListener> getUrisReadyListeners() {
		return urisReadyListeners;
	}

	public AbstractQueryBatcherJob setUrisReadyListeners(List<QueryBatchListener> urisReadyListeners) {
		this.urisReadyListeners = urisReadyListeners;
		return this;
	}

	public List<QueryFailureListener> getQueryFailureListeners() {
		return queryFailureListeners;
	}

	public AbstractQueryBatcherJob setQueryFailureListeners(List<QueryFailureListener> queryFailureListeners) {
		this.queryFailureListeners = queryFailureListeners;
		return this;
	}

	public boolean isConsistentSnapshot() {
		return consistentSnapshot;
	}

	public AbstractQueryBatcherJob setConsistentSnapshot(boolean consistentSnapshot) {
		this.consistentSnapshot = consistentSnapshot;
		return this;
	}

	public boolean isAwaitCompletion() {
		return awaitCompletion;
	}

	public AbstractQueryBatcherJob setAwaitCompletion(boolean awaitCompletion) {
		this.awaitCompletion = awaitCompletion;
		return this;
	}

	public boolean isStopJobAfterCompletion() {
		return stopJobAfterCompletion;
	}

	public AbstractQueryBatcherJob setStopJobAfterCompletion(boolean stopJobAfterCompletion) {
		this.stopJobAfterCompletion = stopJobAfterCompletion;
		return this;
	}

	public AbstractQueryBatcherJob setDataMovementManager(DataMovementManager dataMovementManager) {
		this.dataMovementManager = dataMovementManager;
		return this;
	}

	public AbstractQueryBatcherJob setQueryBatcherBuilder(QueryBatcherBuilder queryBatcherBuilder) {
		this.queryBatcherBuilder = queryBatcherBuilder;
		return this;
	}

	public void setRequireWhereProperty(boolean requireWhereProperty) {
		this.requireWhereProperty = requireWhereProperty;
	}
}
