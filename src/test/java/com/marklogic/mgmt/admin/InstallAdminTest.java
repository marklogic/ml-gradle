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
package com.marklogic.mgmt.admin;

import org.junit.jupiter.api.Test;

import com.marklogic.mgmt.AbstractMgmtTest;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.*;

public class InstallAdminTest extends AbstractMgmtTest {

    /**
     * Since this test suite assumes that MarkLogic has already been properly initialized, including having an admin
     * user installed, this is just a smoke test to ensure that we don't get an error when trying to install the admin
     * again. Instead, a message should be logged and ML should not be restarted.
     */
    @Test
    void adminAlreadyInstalled() {
		assertDoesNotThrow(() -> adminManager.installAdmin("admin", "admin"));
    }

	@Test
	void withNullUsername() {
		String originalUsername = adminConfig.getUsername();
		try {
			adminConfig.setUsername(null);
			HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> adminManager.installAdmin("admin", "admin"));
			assertTrue(exception.getMessage().contains("Unauthorized"));
			assertEquals(401, exception.getStatusCode().value());
		} finally {
			adminConfig.setUsername(originalUsername);
		}
	}

	@Test
	void withNullPassword() {
		String originalPassword = adminConfig.getPassword();
		try {
			adminConfig.setPassword(null);
			HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> adminManager.installAdmin("admin", "admin"));
			assertTrue(exception.getMessage().contains("Unauthorized"));
			assertEquals(401, exception.getStatusCode().value());
		} finally {
			adminConfig.setPassword(originalPassword);
		}
	}
}
