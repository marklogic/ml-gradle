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
package com.marklogic.mgmt.resource.security;

import com.marklogic.mgmt.AbstractMgmtTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class QueryRolesetManagerTest extends AbstractMgmtTest {

	@Test
	public void checkForRolesetThatDoesntExist() {
		QueryRolesetManager mgr = new QueryRolesetManager(manageClient);
		assertFalse(mgr.exists("[\"doesnt-exist\"]"), "Smoke test that this method won't submit a string to the Manage API, which will cause " +
			"a 500 error because it can't be cast to an unsignedLong");
	}
}
