/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
}
