package com.marklogic.client.batch;

import com.marklogic.client.helper.LoggingObject;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ExecutorConfigurationSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Support class for BatchWriter implementations that uses Spring's TaskExecutor interface for parallelizing writes to
 * MarkLogic. Allows for setting a TaskExecutor instance, and if one is not set, a default one will be created based
 * on the threadCount attribute. That attribute is ignored if a TaskExecutor is set.
 */
public abstract class BatchWriterSupport extends LoggingObject implements BatchWriter {

	private TaskExecutor taskExecutor;
	private int threadCount = 16;

	@Override
	public void initialize() {
		if (taskExecutor == null) {
			initializeDefaultTaskExecutor();
		}
	}

	@Override
	public void waitForCompletion() {
		if (taskExecutor instanceof ExecutorConfigurationSupport) {
			if (logger.isInfoEnabled()) {
				logger.info("Calling shutdown on thread pool");
			}
			((ExecutorConfigurationSupport) taskExecutor).shutdown();
			if (logger.isInfoEnabled()) {
				logger.info("Thread pool finished shutdown");
			}
		}
	}

	protected void initializeDefaultTaskExecutor() {
		if (threadCount > 1) {
			if (logger.isInfoEnabled()) {
				logger.info("Initializing thread pool with a count of " + threadCount);
			}
			ThreadPoolTaskExecutor tpte = new ThreadPoolTaskExecutor();
			tpte.setCorePoolSize(threadCount);
			// By default, wait for tasks to finish, and wait up to an hour
			tpte.setWaitForTasksToCompleteOnShutdown(true);
			tpte.setAwaitTerminationSeconds(60 * 60);
			tpte.afterPropertiesSet();
			this.taskExecutor = tpte;
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("Thread count is 1, so using a synchronous TaskExecutor");
			}
			this.taskExecutor = new SyncTaskExecutor();
		}
	}

	protected TaskExecutor getTaskExecutor() {
		return taskExecutor;
	}

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
}
