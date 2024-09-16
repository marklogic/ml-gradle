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
package com.marklogic.client.ext.schemasloader.impl;

import com.marklogic.client.ext.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractSchemasTest extends AbstractIntegrationTest {

	/**
	 * Wipes out the Schemas database - it's assumed you're not using the Schemas database for
	 * anything besides ad hoc testing like this.
	 */
	@BeforeEach
	public void setup() {
		client = newClient("ml-javaclient-util-test-schemas");
		client.newServerEval().xquery("cts:uris((), (), cts:true-query()) ! xdmp:document-delete(.)").eval();
	}

}
