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

import com.marklogic.client.ext.DatabaseClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Extends DatabaseClientConfig to apply 4 Value annotations for host/port/username/password.
 */
@Component
public class SpringDatabaseClientConfig extends DatabaseClientConfig {

	@Autowired
	public SpringDatabaseClientConfig(
		@Value("${marklogic.host}") String host,
		@Value("${marklogic.port}") int port,
		@Value("${marklogic.username}") String username,
		@Value("${marklogic.password}") String password) {
		super(host, port, username, password);
	}

}
