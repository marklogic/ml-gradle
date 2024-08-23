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
import com.marklogic.client.DatabaseClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NewDatabaseClientTest {

	private DatabaseClientConfig config;

	@BeforeEach
	void setup() {
		config = new DatabaseClientConfig("localhost", 8028);
	}

	@Test
	void saml() {
		config.setSecurityContextType(SecurityContextType.SAML);
		config.setSamlToken("my-token");

		DatabaseClient client = new DefaultConfiguredDatabaseClientFactory().newDatabaseClient(config);
		DatabaseClientFactory.SecurityContext context = client.getSecurityContext();

		assertTrue(context instanceof DatabaseClientFactory.SAMLAuthContext);
		DatabaseClientFactory.SAMLAuthContext samlContext = (DatabaseClientFactory.SAMLAuthContext) context;
		assertEquals("my-token", samlContext.getToken());
	}
}
