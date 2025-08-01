/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.admin;

import com.marklogic.mgmt.AbstractMgmtTest;
import com.marklogic.rest.util.SpringWebUtil;
import org.junit.jupiter.api.Test;
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
			assertEquals(401, SpringWebUtil.getHttpStatusCode(exception));
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
			assertEquals(401, SpringWebUtil.getHttpStatusCode(exception));
		} finally {
			adminConfig.setPassword(originalPassword);
		}
	}
}
