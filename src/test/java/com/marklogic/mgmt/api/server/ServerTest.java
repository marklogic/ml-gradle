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
package com.marklogic.mgmt.api.server;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ServerTest  {

	/**
	 * Verifies that the correct JSON keys are used for these properties.
	 *
	 * @throws Exception
	 */
	@Test
	public void disableSslProperties() throws Exception {
		Server s = new Server(new API(null, ObjectMapperFactory.getObjectMapper()), null);
		s.setSslDisableSslv3(true);
		s.setSslDisableTlsv1(true);
		s.setSslDisableTlsv11(true);
		s.setSslDisableTlsv12(true);

		String json = s.getJson();
		ObjectNode node = (ObjectNode) ObjectMapperFactory.getObjectMapper().readTree(json);
		assertTrue(node.get("ssl-disable-sslv3").asBoolean());
		assertTrue(node.get("ssl-disable-tlsv1").asBoolean());
		assertTrue(node.get("ssl-disable-tlsv1-1").asBoolean());
		assertTrue(node.get("ssl-disable-tlsv1-2").asBoolean());
	}

	@Test
	public void xdbc() {
		String xml = "<xdbc-server-properties xmlns=\"http://marklogic.com/manage\">\n" +
			"  <server-name>example-xdbc</server-name>" +
			"</xdbc-server-properties>";

		Server s = new DefaultResourceMapper(new API(null)).readResource(xml, Server.class);
		assertTrue(s instanceof XdbcServer);
		assertEquals("example-xdbc", s.getServerName());
	}

	@Test
	public void odbc() {
		String xml = "<odbc-server-properties xmlns=\"http://marklogic.com/manage\">\n" +
			"  <server-name>example-odbc</server-name>" +
			"</odbc-server-properties>";

		Server s = new DefaultResourceMapper(new API(null)).readResource(xml, Server.class);
		assertTrue(s instanceof OdbcServer);
		assertEquals("example-odbc", s.getServerName());
	}
}
