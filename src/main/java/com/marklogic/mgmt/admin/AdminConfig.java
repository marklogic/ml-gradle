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
package com.marklogic.mgmt.admin;

import com.marklogic.rest.util.RestConfig;


/**
 * Defines the configuration data for talking to the Admin Manage API that is by default on port 8001.
 */
public class AdminConfig extends RestConfig {

	/**
	 * Assumes the usage of "localhost" and 8001 as the host and port.
	 */
    public AdminConfig() {
        super("localhost", 8001, null, null);
    }

    public AdminConfig(String host, String password) {
        super(host, 8001, null, password);
    }

    public AdminConfig(String host, int port, String username, String password) {
        super(host, port, username, password);
    }

	/**
	 * Because this class adds no state and only sets sensible default values for connecting to MarkLogic's Admin app
	 * server, this copy constructor only requires an instance of {@code RestConfig}.
	 *
	 * @param other
	 */
    public AdminConfig(RestConfig other) {
    	super(other);
	}
}
