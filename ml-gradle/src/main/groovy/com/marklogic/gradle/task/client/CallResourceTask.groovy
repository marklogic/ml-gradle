/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.client

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Intended to simplify calling a custom resource - e.g. something under /v1/resources/(resourceName).
 */
class CallResourceTask extends MarkLogicTask {

	@Input @Optional
	def params = [:]

	@Input
	def method = "GET"

	@Input @Optional
	def client = null

	@Input
	def mimeType = "application/json"

	@Input
	String resourceName

	@Input @Optional
	String body

	@Input @Optional
	String outputFilePath

	@TaskAction
	void callResource() {
		def mgr = new GenericResourceManager()
		if (resourceName == null || resourceName.trim().length() == 0) {
			throw new IllegalArgumentException("Must set the value of the 'resourceName' property on this task to the name of the resource to connect to")
		}

		if (client == null) {
			client = newClient()
		}

		client.init(resourceName, mgr)
		method = method.toUpperCase()

		try {
			switch (method) {
				case "GET": handleOutput(mgr.get(params)); break
				case "POST": handleOutput(mgr.post(params, body, mimeType)); break
				case "PUT": handleOutput(mgr.put(params, body, mimeType)); break
				case "DELETE": handleOutput(mgr.delete(params)); break
				default: throw new IllegalArgumentException("Unsupported method " + method);
			}
		} finally {
			client.release()
		}
	}

	def handleOutput(result) {
		if (outputFilePath) {
			writeToFile(result)
		} else {
			println result
		}
	}

	def writeToFile(result) {
		def file = new File(outputFilePath)
		if (file.parentFile) {
			file.parentFile.mkdirs()
		}
		file.write result
		println "Wrote output to " + outputFilePath;
	}

}
