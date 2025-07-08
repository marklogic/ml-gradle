/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
