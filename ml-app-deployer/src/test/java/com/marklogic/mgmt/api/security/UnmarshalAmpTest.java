/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.security;

import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class UnmarshalAmpTest  {

	@Test
	public void xmlSmokeTest() {
		String xml = "<amp-properties xmlns=\"http://marklogic.com/manage\">\n" +
			"  <local-name>invoke</local-name>\n" +
			"  <namespace>http://marklogic.com/appservices/builder/deploy-noxq</namespace>\n" +
			"  <document-uri>/MarkLogic/appservices/appbuilder/deploy-noxq.xqy</document-uri>\n" +
			"  <roles>\n" +
			"    <role>app-builder-internal</role>\n" +
			"  </roles>\n" +
			"</amp-properties>\n";

		Amp amp = new DefaultResourceMapper().readResource(xml, Amp.class);
		assertEquals("invoke", amp.getLocalName());
		assertEquals("http://marklogic.com/appservices/builder/deploy-noxq", amp.getNamespace());
		assertEquals("/MarkLogic/appservices/appbuilder/deploy-noxq.xqy", amp.getDocumentUri());
		assertEquals(1, amp.getRole().size());
		assertEquals("app-builder-internal", amp.getRole().get(0));
	}
}
