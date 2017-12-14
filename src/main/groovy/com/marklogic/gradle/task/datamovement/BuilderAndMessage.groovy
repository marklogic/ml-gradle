package com.marklogic.gradle.task.datamovement

import com.marklogic.client.ext.datamovement.QueryBatcherBuilder

class BuilderAndMessage {

	QueryBatcherBuilder builder
	String message

	BuilderAndMessage(QueryBatcherBuilder builder, String message) {
		this.builder = builder
		this.message = message
	}
}
