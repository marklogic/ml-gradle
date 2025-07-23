/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement;

import com.marklogic.client.datamovement.Batcher;
import com.marklogic.client.datamovement.ForestConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Captures configurable data for a WriteBatcher or QueryBatcher.
 */
public class BatcherConfig {

	public final static Integer DEFAULT_BATCH_SIZE = 100;
	public final static Integer DEFAULT_THREAD_COUNT = 8;

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private String jobId;
	private String jobName;
	private Integer batchSize = DEFAULT_BATCH_SIZE;
	private Integer threadCount = DEFAULT_THREAD_COUNT;
	private ForestConfiguration forestConfig;

	public void prepareBatcher(Batcher batcher) {
		if (jobId != null) {
			batcher.withJobId(jobId);
		}
		if (jobName != null) {
			batcher.withJobName(jobName);
		}
		if (batchSize != null && batchSize > 0) {
			batcher.withBatchSize(batchSize);
		}
		if (threadCount != null && threadCount > 0) {
			batcher.withThreadCount(threadCount);
		}
		if (forestConfig != null) {
			batcher.withForestConfig(forestConfig);
		}
	}

	public String getJobName() {
		return jobName;
	}

	public BatcherConfig setJobName(String jobName) {
		this.jobName = jobName;
		return this;
	}

	public String getJobId() {
		return jobId;
	}

	public BatcherConfig setJobId(String jobId) {
		this.jobId = jobId;
		return this;
	}

	public Integer getBatchSize() {
		return batchSize;
	}

	public BatcherConfig setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
		return this;
	}

	public Integer getThreadCount() {
		return threadCount;
	}

	public BatcherConfig setThreadCount(Integer threadCount) {
		this.threadCount = threadCount;
		return this;
	}

	public ForestConfiguration getForestConfig() {
		return forestConfig;
	}

	public BatcherConfig setForestConfig(ForestConfiguration forestConfig) {
		this.forestConfig = forestConfig;
		return this;
	}
}
