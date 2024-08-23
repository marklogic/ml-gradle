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
package com.marklogic.client.ext.spring;

import org.springframework.beans.factory.DisposableBean;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.DatabaseClientConfig;
import com.marklogic.client.ext.helper.DatabaseClientProvider;

public class SimpleDatabaseClientProvider implements DatabaseClientProvider, DisposableBean {

    private DatabaseClientConfig config;
    private DatabaseClient client;

    public SimpleDatabaseClientProvider() {
    }

    public SimpleDatabaseClientProvider(DatabaseClientConfig config) {
        this.config = config;
    }

    public SimpleDatabaseClientProvider(DatabaseClientManager mgr) {
        this.client = mgr.getObject();
    }

    @Override
    public DatabaseClient getDatabaseClient() {
        if (client == null) {
            client = new DatabaseClientManager(config).getObject();
        }
        return client;
    }

    @Override
    public void destroy() throws Exception {
        if (client != null) {
            client.release();
        }
    }

}
