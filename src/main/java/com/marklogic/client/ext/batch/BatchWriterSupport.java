/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.ext.batch;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.ext.helper.LoggingObject;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ExecutorConfigurationSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;

/**
 * Support class for BatchWriter implementations that uses Spring's TaskExecutor interface for parallelizing writes to
 * MarkLogic. Allows for setting a TaskExecutor instance, and if one is not set, a default one will be created based
 * on the threadCount attribute. That attribute is ignored if a TaskExecutor is set.
 */
public abstract class BatchWriterSupport extends LoggingObject implements BatchWriter {

	private TaskExecutor taskExecutor;
	private int threadCount = 16;
	private WriteListener writeListener = new DefaultWriteListener();

	@Override
	public void initialize() {
		if (taskExecutor == null) {
			initializeDefaultTaskExecutor();
		}
	}

	@Override
	public void waitForCompletion() {
		if (taskExecutor instanceof ExecutorConfigurationSupport) {
			if (logger.isDebugEnabled()) {
				logger.debug("Calling shutdown on thread pool");
			}
			((ExecutorConfigurationSupport) taskExecutor).shutdown();
			if (logger.isDebugEnabled()) {
				logger.debug("Thread pool finished shutdown");
			}
			taskExecutor = null;
		}

		if (writeListener != null) {
			writeListener.afterCompletion();
		}
	}

	protected void initializeDefaultTaskExecutor() {
		if (threadCount > 1) {
			if (logger.isDebugEnabled()) {
				logger.debug("Initializing thread pool with a count of " + threadCount);
			}
			ThreadPoolTaskExecutor tpte = new ThreadPoolTaskExecutor();
			tpte.setCorePoolSize(threadCount);

			// By default, wait for tasks to finish, and wait up to an hour
			tpte.setWaitForTasksToCompleteOnShutdown(true);
			tpte.setAwaitTerminationSeconds(60 * 60);

			tpte.afterPropertiesSet();
			this.taskExecutor = tpte;
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Thread count is 1, so using a synchronous TaskExecutor");
			}
			this.taskExecutor = new SyncTaskExecutor();
		}
	}

	/**
	 * Will use the WriteListener if the TaskExecutor is an instance of AsyncListenableTaskExecutor. The WriteListener
	 * will then be used to listen for failures.
	 *
	 * @param runnable
	 * @param items
	 */
	protected void executeRunnable(Runnable runnable, final List<? extends DocumentWriteOperation> items) {
		if (writeListener != null && taskExecutor instanceof AsyncListenableTaskExecutor) {
			AsyncListenableTaskExecutor asyncListenableTaskExecutor = (AsyncListenableTaskExecutor)taskExecutor;
			ListenableFuture<?> future = asyncListenableTaskExecutor.submitListenable(runnable);
			future.addCallback(new ListenableFutureCallback<Object>() {
				@Override
				public void onFailure(Throwable ex) {
					writeListener.onWriteFailure(ex, items);
				}
				@Override
				public void onSuccess(Object result) {
				}
			});
		} else {
			taskExecutor.execute(runnable);
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

	protected WriteListener getWriteListener() {
		return writeListener;
	}

	public void setWriteListener(WriteListener writeListener) {
		this.writeListener = writeListener;
	}

	public int getThreadCount() {
		return threadCount;
	}
}
