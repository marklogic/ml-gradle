/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConnectionCheckerTest {

	@Mock
	private ManageClient mockManageClient;

	private ConnectionChecker connectionChecker;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		connectionChecker = new ConnectionChecker(() -> mockManageClient);
	}

	@Test
	void testConstructorWithNullClient() {
		assertThrows(IllegalArgumentException.class, () -> new ConnectionChecker(null));
		assertThrows(IllegalArgumentException.class, () -> new ConnectionChecker(null, 2000L, 15));
	}

	@Test
	void testConstructorWithCustomSettings() {
		ConnectionChecker checker = new ConnectionChecker(() -> mockManageClient, 5000L, 30);
		assertEquals(5000L, checker.getWaitInterval());
		assertEquals(30, checker.getMaxAttempts());
	}

	@Test
	void testConstructorWithInvalidSettings() {
		assertThrows(IllegalArgumentException.class, () -> new ConnectionChecker(() -> mockManageClient, 0L, 15));
		assertThrows(IllegalArgumentException.class, () -> new ConnectionChecker(() -> mockManageClient, -1L, 15));
		assertThrows(IllegalArgumentException.class, () -> new ConnectionChecker(() -> mockManageClient, 2000L, 0));
		assertThrows(IllegalArgumentException.class, () -> new ConnectionChecker(() -> mockManageClient, 2000L, -1));
	}

	@Test
	void testWaitUntilReadySuccessFirstTry() {
		when(mockManageClient.getJson("/manage/v2")).thenReturn("{}");

		connectionChecker.waitUntilReady();
		
		verify(mockManageClient, times(1)).getJson("/manage/v2");
	}

	@Test
	void testWaitUntilReadySuccessAfterRetries() {
		ConnectionChecker checker = new ConnectionChecker(() -> mockManageClient, 100L, 3);

		when(mockManageClient.getJson("/manage/v2"))
			.thenThrow(new RuntimeException("Not ready yet"))  // First attempt fails
			.thenThrow(new RuntimeException("Still not ready"))  // Second attempt fails
			.thenReturn("{}");  // Third attempt succeeds

		checker.waitUntilReady();
		
		verify(mockManageClient, times(3)).getJson("/manage/v2");
	}

	@Test
	void testWaitUntilReadyFailsAfterMaxAttempts() {
		ConnectionChecker checker = new ConnectionChecker(() -> mockManageClient, 100L, 2);

		when(mockManageClient.getJson("/manage/v2")).thenThrow(new RuntimeException("Connection failed"));

		RuntimeException exception = assertThrows(RuntimeException.class,
			() -> checker.waitUntilReady());

		assertTrue(exception.getMessage().contains("Failed to connect to MarkLogic after 2 attempts"));
		verify(mockManageClient, times(2)).getJson("/manage/v2");
	}

	@Test
	void testDefaultValues() {
		assertEquals(3000L, connectionChecker.getWaitInterval());
		assertEquals(20, connectionChecker.getMaxAttempts());
	}

	@Test
	void testImmutability() {
		// Test that objects are immutable - getters return the values set in constructor
		ConnectionChecker checker1 = new ConnectionChecker(() -> mockManageClient);
		assertEquals(3000L, checker1.getWaitInterval());
		assertEquals(20, checker1.getMaxAttempts());

		ConnectionChecker checker2 = new ConnectionChecker(() -> mockManageClient, 5000L, 30);
		assertEquals(5000L, checker2.getWaitInterval());
		assertEquals(30, checker2.getMaxAttempts());

		// Original checker should still have default values
		assertEquals(3000L, checker1.getWaitInterval());
		assertEquals(20, checker1.getMaxAttempts());
	}
}
