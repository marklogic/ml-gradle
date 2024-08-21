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
package com.marklogic.mgmt.api.forest;

import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class UnmarshalForestTest  {

	@Test
	public void xmlSmokeTest() {
		String xml = "<forest-properties xmlns=\"http://marklogic.com/manage\">\n" +
			"  <forest-name>Documents</forest-name>\n" +
			"  <host>localhost</host>\n" +
			"  <enabled>true</enabled>\n" +
			"  <data-directory/>\n" +
			"  <large-data-directory/>\n" +
			"  <fast-data-directory/>\n" +
			"  <fast-data-max-size>0</fast-data-max-size>\n" +
			"  <updates-allowed>all</updates-allowed>\n" +
			"  <availability>online</availability>\n" +
			"  <rebalancer-enable>true</rebalancer-enable>\n" +
			"  <range/>\n" +
			"  <failover-enable>true</failover-enable>\n" +
			"  <failover-hosts/>\n" +
			"  <forest-backups/>\n" +
			"  <forest-replicas/>\n" +
			"  <database-replication/>\n" +
			"  <database>Documents</database>\n" +
			"</forest-properties>";

		Forest f = new DefaultResourceMapper().readResource(xml, Forest.class);
		assertEquals("Documents", f.getForestName());
		assertEquals("localhost", f.getHost());
	}
}
