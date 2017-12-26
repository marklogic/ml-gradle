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

class DataMovementTask extends MarkLogicTask {

	void runQueryBatcherJob(QueryBatcherJob job) {
		if (project.hasProperty("jobProperties")) {
			if (job instanceof ConfigurableJob) {
				ConfigurableJob cjob = (ConfigurableJob) job
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

			DatabaseClient client = newClient()
			try {
				job.run(client)
			} finally {
				client.release()
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
