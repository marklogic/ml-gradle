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
