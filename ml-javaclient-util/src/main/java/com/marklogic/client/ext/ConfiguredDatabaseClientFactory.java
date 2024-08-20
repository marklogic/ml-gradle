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
package com.marklogic.client.ext;

import com.marklogic.client.DatabaseClient;

/**
 * Hides how a DatabaseClient is constructed based on the inputs in a DatabaseClientConfig object. The intent is that a
 * client can populate any set of properties on the DatabaseClientConfig, and an implementation of this interface will
 * determine how to construct a new DatabaseClient based on those properties.
 */
public interface ConfiguredDatabaseClientFactory {

	DatabaseClient newDatabaseClient(DatabaseClientConfig databaseClientConfig);

}
