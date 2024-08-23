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
package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.helper.LoggingObject;

import java.util.function.Supplier;

public class SimpleLoadModulesFailureListener extends LoggingObject implements LoadModulesFailureListener, Supplier<Throwable> {

	private Throwable firstThrowable;

	@Override
	public void processFailure(Throwable throwable, DatabaseClient databaseClient) {
		final String message = format("Error occurred while loading modules; host: %s; port: %d; cause: %s", databaseClient.getHost(), databaseClient.getPort(), throwable.getMessage());
		final RuntimeException exception = new RuntimeException(message, throwable);
		if (firstThrowable == null) {
			firstThrowable = exception;
		}
		if (logger.isErrorEnabled()) {
			logger.error(message, throwable);
		}
	}

	@Override
	public Throwable get() {
		return firstThrowable;
	}
}
