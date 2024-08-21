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
package com.marklogic.mgmt;

import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.security.Role;
import org.junit.jupiter.api.Test;
import org.springframework.util.ClassUtils;

import static org.springframework.test.util.AssertionErrors.fail;

/**
 * This test is used for manual inspection of the errors that are logged. Any resource works here, roles are just easy
 * to test with.
 */
public class InvalidResourcePayloadTest extends AbstractMgmtTest {

	@Test
	public void createNewRole() {
		Role role = new Role(new API(manageClient), ClassUtils.getShortName(getClass()) + "-test");
		role.addRole("INVALID_ROLE_THAT_SHOULD_CAUSE_A_FAILURE");

		try {
			role.save();
			fail("The role should have failed to save, which should have resulted in the request body being logged at the ERROR level");
		} catch (Exception ex) {
			logger.info("Caught expected exception: " + ex.getMessage());
		}
	}

	@Test
	public void updateRole() {
		Role role = new Role(new API(manageClient), "rest-reader");
		role.addRole("INVALID_ROLE_THAT_SHOULD_CAUSE_A_FAILURE");

		try {
			role.save();
			fail("The role should have failed to save, which should have resulted in the request body being logged at the ERROR level");
		} catch (Exception ex) {
			logger.info("Caught expected exception: " + ex.getMessage());
		}
	}
}
