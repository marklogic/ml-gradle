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
		assertThrows(IllegalArgumentException.class, () -> new ConnectionChecker(null, 2000L, 15, 2));
	}

	@Test
	void testConstructorWithCustomSettings() {
		ConnectionChecker checker = new ConnectionChecker(() -> mockManageClient, 5000L, 30, 2);
		assertEquals(5000L, checker.getWaitInterval());
		assertEquals(30, checker.getMaxAttempts());
		assertEquals(2, checker.getMinSuccessfulAttempts());

		ConnectionChecker checker2 = new ConnectionChecker(() -> mockManageClient, 5000L, 30, 3);
		assertEquals(5000L, checker2.getWaitInterval());
		assertEquals(30, checker2.getMaxAttempts());
		assertEquals(3, checker2.getMinSuccessfulAttempts());
	}

	@Test
	void testConstructorWithInvalidSettings() {
		assertThrows(IllegalArgumentException.class, () -> new ConnectionChecker(() -> mockManageClient, 0L, 15, 2));
		assertThrows(IllegalArgumentException.class, () -> new ConnectionChecker(() -> mockManageClient, -1L, 15, 2));
		assertThrows(IllegalArgumentException.class, () -> new ConnectionChecker(() -> mockManageClient, 2000L, 0, 2));
		assertThrows(IllegalArgumentException.class, () -> new ConnectionChecker(() -> mockManageClient, 2000L, -1, 2));
		assertThrows(IllegalArgumentException.class, () -> new ConnectionChecker(() -> mockManageClient, 2000L, 15, 0));
		assertThrows(IllegalArgumentException.class, () -> new ConnectionChecker(() -> mockManageClient, 2000L, 15, -1));
	}

	@Test
	void testWaitUntilReadySuccessFirstTry() {
		when(mockManageClient.getJson("/manage/v2")).thenReturn("{}");

		connectionChecker.waitUntilReady();

		// With default minSuccessfulAttempts=2, we need 2 successful calls
		verify(mockManageClient, times(2)).getJson("/manage/v2");
	}

	@Test
	void testWaitUntilReadySuccessAfterRetries() {
		ConnectionChecker checker = new ConnectionChecker(() -> mockManageClient, 100L, 5, 2);

		when(mockManageClient.getJson("/manage/v2"))
			.thenThrow(new RuntimeException("Not ready yet"))  // First attempt fails
			.thenThrow(new RuntimeException("Still not ready"))  // Second attempt fails
			.thenReturn("{}")  // Third attempt succeeds
			.thenReturn("{}");  // Fourth attempt succeeds (2 consecutive = ready)

		checker.waitUntilReady();

		verify(mockManageClient, times(4)).getJson("/manage/v2");
	}

	@Test
	void testWaitUntilReadyFailsAfterMaxAttempts() {
		ConnectionChecker checker = new ConnectionChecker(() -> mockManageClient, 100L, 2, 2);

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
		assertEquals(2, connectionChecker.getMinSuccessfulAttempts());
	}

	@Test
	void testImmutability() {
		// Test that objects are immutable - getters return the values set in constructor
		ConnectionChecker checker1 = new ConnectionChecker(() -> mockManageClient);
		assertEquals(3000L, checker1.getWaitInterval());
		assertEquals(20, checker1.getMaxAttempts());
		assertEquals(2, checker1.getMinSuccessfulAttempts());

		ConnectionChecker checker2 = new ConnectionChecker(() -> mockManageClient, 5000L, 30, 3);
		assertEquals(5000L, checker2.getWaitInterval());
		assertEquals(30, checker2.getMaxAttempts());
		assertEquals(3, checker2.getMinSuccessfulAttempts());

		// Original checker should still have default values
		assertEquals(3000L, checker1.getWaitInterval());
		assertEquals(20, checker1.getMaxAttempts());
		assertEquals(2, checker1.getMinSuccessfulAttempts());
	}

	@Test
	void testConsecutiveSuccessesRequired() {
		ConnectionChecker checker = new ConnectionChecker(() -> mockManageClient, 100L, 10, 3);

		when(mockManageClient.getJson("/manage/v2"))
			.thenReturn("{}")  // Success 1
			.thenReturn("{}")  // Success 2
			.thenReturn("{}");  // Success 3 (3 consecutive = ready)

		checker.waitUntilReady();

		verify(mockManageClient, times(3)).getJson("/manage/v2");
	}

	@Test
	void testConsecutiveSuccessesResetOnFailure() {
		ConnectionChecker checker = new ConnectionChecker(() -> mockManageClient, 100L, 10, 2);

		when(mockManageClient.getJson("/manage/v2"))
			.thenReturn("{}")  // Success 1
			.thenThrow(new RuntimeException("Connection lost"))  // Failure - resets counter
			.thenReturn("{}")  // Success 1 (after reset)
			.thenReturn("{}");  // Success 2 (2 consecutive = ready)

		checker.waitUntilReady();

		verify(mockManageClient, times(4)).getJson("/manage/v2");
	}

	@Test
	void testMinSuccessfulAttemptsOfOne() {
		ConnectionChecker checker = new ConnectionChecker(() -> mockManageClient, 100L, 5, 1);

		when(mockManageClient.getJson("/manage/v2")).thenReturn("{}");

		checker.waitUntilReady();

		// With minSuccessfulAttempts=1, only 1 success needed
		verify(mockManageClient, times(1)).getJson("/manage/v2");
	}
}
