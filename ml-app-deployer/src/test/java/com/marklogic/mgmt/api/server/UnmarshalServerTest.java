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

import com.marklogic.mgmt.api.group.Namespace;
import com.marklogic.mgmt.api.group.Schema;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;

public class UnmarshalServerTest  {

	@Test
	public void test() {
		String xml = "<http-server-properties xmlns=\"http://marklogic.com/manage\">\n" +
			"  <server-name>testserver</server-name>\n" +
			"  <group-name>Default</group-name>\n" +
			"  <server-type>http</server-type>\n" +
			"  <enabled>true</enabled>\n" +
			"  <root>/</root>\n" +
			"  <port>8017</port>\n" +
			"  <webDAV>false</webDAV>\n" +
			"  <execute>true</execute>\n" +
			"  <display-last-login>false</display-last-login>\n" +
			"  <address>0.0.0.0</address>\n" +
			"  <backlog>512</backlog>\n" +
			"  <threads>32</threads>\n" +
			"  <request-timeout>30</request-timeout>\n" +
			"  <keep-alive-timeout>5</keep-alive-timeout>\n" +
			"  <session-timeout>3600</session-timeout>\n" +
			"  <max-time-limit>3600</max-time-limit>\n" +
			"  <default-time-limit>600</default-time-limit>\n" +
			"  <max-inference-size>500</max-inference-size>\n" +
			"  <default-inference-size>100</default-inference-size>\n" +
			"  <static-expires>3600</static-expires>\n" +
			"  <pre-commit-trigger-depth>1000</pre-commit-trigger-depth>\n" +
			"  <pre-commit-trigger-limit>10000</pre-commit-trigger-limit>\n" +
			"  <collation>http://marklogic.com/collation/</collation>\n" +
			"  <coordinate-system>wgs84</coordinate-system>\n" +
			"  <authentication>digest</authentication>\n" +
			"  <internal-security>true</internal-security>\n" +
			"  <concurrent-request-limit>0</concurrent-request-limit>\n" +
			"  <compute-content-length>true</compute-content-length>\n" +
			"  <file-log-level>info</file-log-level>\n" +
			"  <log-errors>false</log-errors>\n" +
			"  <debug-allow>true</debug-allow>\n" +
			"  <profile-allow>true</profile-allow>\n" +
			"  <default-xquery-version>1.0-ml</default-xquery-version>\n" +
			"  <multi-version-concurrency-control>contemporaneous</multi-version-concurrency-control>\n" +
			"  <distribute-timestamps>fast</distribute-timestamps>\n" +
			"  <output-sgml-character-entities>none</output-sgml-character-entities>\n" +
			"  <output-encoding>UTF-8</output-encoding>\n" +
			"  <output-method>default</output-method>\n" +
			"  <output-byte-order-mark>default</output-byte-order-mark>\n" +
			"  <output-cdata-section-namespace-uri/>\n" +
			"  <output-cdata-section-localname/>\n" +
			"  <output-doctype-public/>\n" +
			"  <output-doctype-system/>\n" +
			"  <output-escape-uri-attributes>default</output-escape-uri-attributes>\n" +
			"  <output-include-content-type>default</output-include-content-type>\n" +
			"  <output-indent>default</output-indent>\n" +
			"  <output-indent-untyped>default</output-indent-untyped>\n" +
			"  <output-indent-tabs>default</output-indent-tabs>\n" +
			"  <output-media-type/>\n" +
			"  <output-normalization-form>none</output-normalization-form>\n" +
			"  <output-omit-xml-declaration>default</output-omit-xml-declaration>\n" +
			"  <output-standalone>omit</output-standalone>\n" +
			"  <output-undeclare-prefixes>default</output-undeclare-prefixes>\n" +
			"  <output-version/>\n" +
			"  <output-include-default-attributes>default</output-include-default-attributes>\n" +
			"  <default-error-format>json</default-error-format>\n" +
			"  <error-handler>/MarkLogic/rest-api/error-handler.xqy</error-handler>\n" +
			"  <schemas>\n" +
			"    <schema>\n" +
			"      <namespace-uri>schema1</namespace-uri>\n" +
			"      <schema-location>/tmp/schema1</schema-location>\n" +
			"    </schema>\n" +
			"  </schemas>\n" +
			"  <namespaces>\n" +
			"    <namespace>\n" +
			"      <prefix>t1</prefix>\n" +
			"      <namespace-uri>test1</namespace-uri>\n" +
			"    </namespace>\n" +
			"  </namespaces>\n" +
			"  <using-namespaces>\n" +
			"    <using-namespace>\n" +
			"      <namespace-uri>path1</namespace-uri>\n" +
			"    </using-namespace>\n" +
			"  </using-namespaces>\n" +
			"  <module-locations>\n" +
			"    <module-location>\n" +
			"      <namespace-uri>path1</namespace-uri>\n" +
			"      <location>/tmp/path1</location>\n" +
			"    </module-location>\n" +
			"  </module-locations>\n" +
			"  <request-blackouts>\n" +
			"    <request-blackout>\n" +
			"      <users>\n" +
			"\t<user>nobody</user>\n" +
			"      </users>\n" +
			"      <roles>\n" +
			"\t<role>rest-reader</role>\n" +
			"      </roles>\n" +
			"      <blackout-type>recurring</blackout-type>\n" +
			"      <days>\n" +
			"\t<day>monday</day>\n" +
			"      </days>\n" +
			"      <period/>\n" +
			"    </request-blackout>\n" +
			"  </request-blackouts>\n" +
			"  <url-rewriter>/MarkLogic/rest-api/rewriter.xml</url-rewriter>\n" +
			"  <rewrite-resolves-globally>true</rewrite-resolves-globally>\n" +
			"  <ssl-allow-sslv3>true</ssl-allow-sslv3>\n" +
			"  <ssl-allow-tls>true</ssl-allow-tls>\n" +
			"  <ssl-disable-sslv3>false</ssl-disable-sslv3>\n" +
			"  <ssl-disable-tlsv1>false</ssl-disable-tlsv1>\n" +
			"  <ssl-disable-tlsv1-1>false</ssl-disable-tlsv1-1>\n" +
			"  <ssl-disable-tlsv1-2>false</ssl-disable-tlsv1-2>\n" +
			"  <ssl-hostname/>\n" +
			"  <ssl-ciphers>ALL:!LOW:@STRENGTH</ssl-ciphers>\n" +
			"  <ssl-require-client-certificate>true</ssl-require-client-certificate>\n" +
			"  <ssl-client-issuer-authority-verification>false</ssl-client-issuer-authority-verification>\n" +
			"  <content-database>testserver-content</content-database>\n" +
			"  <modules-database>testserver-modules</modules-database>\n" +
			"  <default-user>nobody</default-user>\n" +
			"</http-server-properties>\n";

		Server s = new DefaultResourceMapper().readResource(xml, Server.class);

		List<UsingNamespace> usingNamespaces = s.getUsingNamespace();
		assertEquals(1, usingNamespaces.size());
		assertEquals("path1", usingNamespaces.get(0).getNamespaceUri());

		List<Schema> schemas = s.getSchema();
		assertEquals(1, schemas.size());
		assertEquals("schema1", schemas.get(0).getNamespaceUri());
		assertEquals("/tmp/schema1", schemas.get(0).getSchemaLocation());

		List<Namespace> namespaces = s.getNamespace();
		assertEquals(1, namespaces.size());
		assertEquals("t1", namespaces.get(0).getPrefix());
		assertEquals("test1", namespaces.get(0).getNamespaceUri());
	}
}
