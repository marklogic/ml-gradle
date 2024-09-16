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

import java.util.List;

public class DefaultWriteListener extends WriteListenerSupport {

	private Throwable firstError;

	@Override
	public synchronized void onWriteFailure(Throwable ex, List<? extends DocumentWriteOperation> items) {
		if (firstError == null) {
			firstError = ex;
		}
	}

	@Override
	public void afterCompletion() {
		if (firstError != null) {
			throw new RuntimeException("Caught exception before writing was completed: " +
				firstError.getMessage(), firstError);
		}
	}
}
