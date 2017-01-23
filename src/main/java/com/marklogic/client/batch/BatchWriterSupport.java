package com.marklogic.client.batch;

import com.marklogic.client.helper.LoggingObject;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Support class for BatchWriter implementations that uses Spring's TaskExecutor interface for parallelizing writes to
 * MarkLogic.
 */
public abstract class BatchWriterSupport extends LoggingObject implements BatchWriter {

	private TaskExecutor taskExecutor;
	private int threadCount = 16;

	/**
	 * Seems necessary to keep track of each Future instance so that we can properly wait for each one to finish.
	 * Spring's TaskExecutor library doesn't seem to provide a better way of doing this.
	 */
	private List<Future<?>> futures = new ArrayList<>();

	@Override
	public void initialize() {
		if (threadCount > 1) {
			if (logger.isInfoEnabled()) {
				logger.info("Initializing thread pool with a count of " + threadCount);
			}
			ThreadPoolTaskExecutor tpte = new ThreadPoolTaskExecutor();
			tpte.setCorePoolSize(threadCount);
			tpte.afterPropertiesSet();
			this.taskExecutor = tpte;
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("Thread count is 1, so using a synchronous TaskExecutor");
			}
			this.taskExecutor = new SyncTaskExecutor();
		}
	}

	@Override
	public void waitForCompletion() {
		int size = futures.size();
		if (logger.isDebugEnabled()) {
			logger.debug("Waiting for threads to finish document processing; futures count: " + size);
		}

		for (int i = 0; i < size; i++) {
			Future<?> f = futures.get(i);
			if (f.isDone()) {
				continue;
			}
			try {
				// Wait up to 1 hour for a write to ML to finish (should never happen)
				f.get(1, TimeUnit.HOURS);
			} catch (Exception ex) {
				logger.warn("Unable to wait for last set of documents to be processed: " + ex.getMessage(), ex);
			}
		}
	}

	protected void execute(Runnable runnable) {
		if (taskExecutor instanceof AsyncTaskExecutor) {
			futures.add(((AsyncTaskExecutor) taskExecutor).submit(runnable));
		} else {
			taskExecutor.execute(runnable);
		}
	}

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
}
