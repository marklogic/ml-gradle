package com.marklogic.mgmt.api.server;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import org.junit.Assert;
import org.junit.Test;

public class ServerTest extends Assert {

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
}
