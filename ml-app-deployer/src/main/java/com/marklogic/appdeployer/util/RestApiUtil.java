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
package com.marklogic.appdeployer.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.mgmt.util.ObjectMapperFactory;

public abstract class RestApiUtil {

	public static String buildDefaultRestApiJson() {
		return buildDefaultRestApiJson(0);
	}

	/**
	 * @param forestsPerHost if greater than zero, than will be used to set the "forests-per-host" property in the
	 *                       payload
	 * @return
	 */
	public static String buildDefaultRestApiJson(int forestsPerHost) {
		ObjectMapper m = ObjectMapperFactory.getObjectMapper();
		ObjectNode node = m.createObjectNode();
		ObjectNode n = node.putObject("rest-api");
		n.put("name", "%%NAME%%");
		n.put("group", "%%GROUP%%");
		n.put("database", "%%DATABASE%%");
		n.put("modules-database", "%%MODULES_DATABASE%%");
		n.put("port", "%%PORT%%");
		n.put("xdbc-enabled", true);
		if (forestsPerHost > 0) {
			n.put("forests-per-host", forestsPerHost);
		}
		n.put("error-format", "json");

		try {
			String json = m.writer(new DefaultPrettyPrinter()).writeValueAsString(node);
			json = json.replace("\"%%PORT%%\"", "%%PORT%%");
			return json;
		} catch (JsonProcessingException ex) {
			throw new RuntimeException(ex);
		}
	}
}
