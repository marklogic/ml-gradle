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
package com.marklogic.gradle.task.datamovement

import com.marklogic.client.DatabaseClient
import com.marklogic.client.datamovement.QueryBatchListener
import com.marklogic.client.ext.datamovement.QueryBatcherBuilder
import com.marklogic.client.ext.datamovement.QueryBatcherTemplate
import com.marklogic.client.ext.datamovement.job.ConfigurableJob
import com.marklogic.client.ext.datamovement.job.JobProperty
import com.marklogic.client.ext.datamovement.job.QueryBatcherJob
import com.marklogic.client.ext.datamovement.listener.SimpleBatchLoggingListener
import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * The "client" field in this task allows a user to provide a custom DatabaseClient while declaring an instance of
 * this task in a Gradle build file.
 */
class DataMovementTask extends MarkLogicTask {

	@Input
	@Optional
	DatabaseClient client

	void runQueryBatcherJob(QueryBatcherJob job) {
		if (project.hasProperty("jobProperties")) {
			if (job instanceof ConfigurableJob) {
				printJobProperties(job)
			} else {
				println "Job does not implement ConfigurableJob, cannot show its properties"
			}

		} else {

			if (job instanceof ConfigurableJob) {
				ConfigurableJob cjob = (ConfigurableJob) job

				Properties props = new Properties()
				Map<String, ?> gradleProps = getProject().getProperties()
				for (String key : gradleProps.keySet()) {
					Object value = gradleProps.get(key)
					if (value instanceof String) {
						props.setProperty(key, (String) value)
					}
				}

				List<String> messages = cjob.configureJob(props)

				if (messages != null && !messages.isEmpty()) {
					println "\nInvalid job configuration; showing job properties as a reference"
					printJobProperties(cjob)

					String errorMessage = "Invalid job configuration, see list of job properties above; errors:"
					for (String message : messages) {
						errorMessage += "\n - " + message
					}
					throw new GradleException(errorMessage)
				}
			}

			DatabaseClient databaseClient = client
			if (databaseClient == null) {
				databaseClient = newClient()
			}
			try {
				job.run(databaseClient)
			} finally {
				if (databaseClient != null) {
					databaseClient.release()
				}
			}
		}
	}

	void printJobProperties(ConfigurableJob job) {
		println "\nJob properties (* = required):"
		for (JobProperty prop : job.getJobProperties()) {
			String text = prop.getPropertyName() + ": " + prop.getPropertyDescription()
			if (prop.isRequired()) {
				text = " - (*) " + text
			} else {
				text = " - " + text
			}
			println text
		}
	}

	/**
	 * Use runQueryBatcherJob instead.
	 *
	 * @param listener
	 * @param collections
	 */
	@Deprecated
	void applyOnCollections(QueryBatchListener listener, String... collections) {
		DatabaseClient client = newClient()
		try {
			newQueryBatcherTemplate(client).applyOnCollections(listener, collections);
		} finally {
			client.release()
		}
	}

	/**
	 * Use runQueryBatcherJob instead.
	 *
	 * @param listener
	 * @param uriPattern
	 */
	@Deprecated
	void applyOnUriPattern(QueryBatchListener listener, String uriPattern) {
		DatabaseClient client = newClient()
		try {
			newQueryBatcherTemplate(client).applyOnUriPattern(listener, uriPattern);
		} finally {
			client.release()
		}
	}

	/**
	 * Use runQueryBatcherJob instead.
	 *
	 * @param listener
	 * @param builder
	 */
	@Deprecated
	void applyWithQueryBatcherBuilder(QueryBatchListener listener, QueryBatcherBuilder builder) {
		DatabaseClient client = newClient()
		try {
			newQueryBatcherTemplate(client).apply(listener, builder)
		} finally {
			client.release()
		}
	}

	/**
	 * Use runQueryBatcherJob instead.
	 *
	 * @param client
	 * @return
	 */
	@Deprecated
	QueryBatcherTemplate newQueryBatcherTemplate(DatabaseClient client) {
		QueryBatcherTemplate t = new QueryBatcherTemplate(client)

		if (project.hasProperty("jobName")) {
			t.setJobName(project.property("jobName"))
		}
		if (project.hasProperty("batchSize")) {
			t.setBatchSize(Integer.parseInt(project.property("batchSize")))
		}
		if (project.hasProperty("threadCount")) {
			t.setThreadCount(Integer.parseInt(project.property("threadCount")))
		}

		if (project.hasProperty("consistentSnapshot")) {
			t.setApplyConsistentSnapshot(Boolean.parseBoolean(project.property("consistentSnapshot")))
		}

		if (project.hasProperty("logBatches")) {
			t.addUrisReadyListeners(new SimpleBatchLoggingListener())
		}

		return t
	}
}
