/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.admin;

import com.marklogic.mgmt.AbstractMgmtTest;
import com.marklogic.rest.util.SpringWebUtil;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.*;

public class InitializeMarkLogicTest extends AbstractMgmtTest {

	/**
	 * The only way to really test this is to run it against a freshly installed MarkLogic, but in a test suite, we
	 * always assume that we have a MarkLogic instance that has been initialized already. So this is just a smoke test
	 * to ensure no errors are thrown from bad JSON.
	 */
	@Test
	void initAgainstAnAlreadyInitializedMarkLogic() {
		assertDoesNotThrow(() -> adminManager.init());
	}

	@Test
	void withNullUsername() {
		String originalUsername = adminConfig.getUsername();
		try {
			adminConfig.setUsername(null);
			HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> adminManager.init());
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
			HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> adminManager.init());
			assertTrue(exception.getMessage().contains("Unauthorized"));
			assertEquals(401, SpringWebUtil.getHttpStatusCode(exception));
		} finally {
			adminConfig.setPassword(originalPassword);
		}
	}
}
