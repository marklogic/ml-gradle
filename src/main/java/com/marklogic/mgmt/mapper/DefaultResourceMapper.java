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
package com.marklogic.mgmt.mapper;

import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.api.server.HttpServer;
import com.marklogic.mgmt.api.server.OdbcServer;
import com.marklogic.mgmt.api.server.Server;
import com.marklogic.mgmt.api.server.XdbcServer;

import javax.xml.bind.JAXBContext;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * This implementation assumes that the API object that it's given has a Jackson ObjectMapper that's configured to use
 * a lower-case hyphenated naming strategy.
 */
public class DefaultResourceMapper implements ResourceMapper {

	private API api;
	private Map<Class<?>, JAXBContext> jaxbContextMap;
	private PayloadParser payloadParser = new PayloadParser();

	public DefaultResourceMapper() {
		this.jaxbContextMap = new HashMap<>();
	}

	public DefaultResourceMapper(API api) {
		this();
		this.api = api;
	}

	@Override
	public <T extends Resource> T readResource(String payload, Class<T> resourceType) {
		try {
			T resource;
			if (payloadParser.isJsonPayload(payload)) {
				resource = api.getObjectMapper().readerFor(resourceType).readValue(payload);
			} else {
				JAXBContext context;

				/**
				 * Can't figure out how to use a JAXB ObjectFactory to support the 3 different kinds of servers, so using
				 * this hacky approach.
				 */
				if (resourceType.equals(Server.class)) {
					if (payload.contains("xdbc-server-properties")) {
						context = jaxbContextMap.get(XdbcServer.class);
					} else if (payload.contains("odbc-server-properties")) {
						context = jaxbContextMap.get(OdbcServer.class);
					} else {
						context = jaxbContextMap.get(HttpServer.class);
					}
				} else {
					context = jaxbContextMap.get(resourceType);
				}

				if (context == null) {
					if (resourceType.equals(Server.class)) {
						if (payload.contains("xdbc-server-properties")) {
							context = JAXBContext.newInstance(XdbcServer.class);
						} else if (payload.contains("odbc-server-properties")) {
							context = JAXBContext.newInstance(OdbcServer.class);
						} else {
							context = JAXBContext.newInstance(HttpServer.class);
						}
					} else {
						context = JAXBContext.newInstance(resourceType);
					}
					jaxbContextMap.put(resourceType, context);
				}
				resource = (T) context.createUnmarshaller().unmarshal(new StringReader(payload));
			}
			if (api != null) {
				resource.setApi(api);
				resource.setObjectMapper(api.getObjectMapper());
			}
			return resource;
		} catch (Exception ex) {
			throw new RuntimeException("Unable to read resource payload: " + ex.getMessage(), ex);
		}
	}

}
