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

/**
 * This is just for loading REST modules, which DefaultModulesLoader loads by default in parallel. The DatabaseClient
 * is provided so that the implementation can e.g. capture information about the host and port in use.
 */
public interface LoadModulesFailureListener {

	void processFailure(Throwable throwable, DatabaseClient databaseClient);

}
