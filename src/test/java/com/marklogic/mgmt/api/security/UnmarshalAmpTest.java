package com.marklogic.mgmt.api.security;

import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import org.junit.Assert;
import org.junit.Test;

public class UnmarshalAmpTest extends Assert {

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
