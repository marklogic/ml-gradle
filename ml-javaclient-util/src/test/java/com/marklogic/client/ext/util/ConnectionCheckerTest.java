/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.util;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConnectionCheckerTest {

	@Mock
	private DatabaseClient mockClient;

	@Mock
	private DatabaseClient.ConnectionResult mockConnectionResult;

	private ConnectionChecker connectionChecker;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		connectionChecker = new ConnectionChecker(() -> mockClient);
	}

	@Test
	void testConstructorWithNullClient() {
		assertThrows(IllegalArgumentException.class, () -> new ConnectionChecker((java.util.function.Supplier<DatabaseClient>) null));
		assertThrows(IllegalArgumentException.class, () -> new ConnectionChecker((java.util.function.Supplier<DatabaseClient>) null, 2000L, 15));
	}

	@Test
	void testConstructorWithCustomSettings() {
		ConnectionChecker checker = new ConnectionChecker(() -> mockClient, 5000L, 30);
		assertEquals(5000L, checker.getWaitInterval());
		assertEquals(30, checker.getMaxAttempts());
	}

	@Test
	void testConstructorWithInvalidSettings() {
		assertThrows(IllegalArgumentException.class, () -> new ConnectionChecker(() -> mockClient, 0L, 15));
		assertThrows(IllegalArgumentException.class, () -> new ConnectionChecker(() -> mockClient, -1L, 15));
		assertThrows(IllegalArgumentException.class, () -> new ConnectionChecker(() -> mockClient, 2000L, 0));
		assertThrows(IllegalArgumentException.class, () -> new ConnectionChecker(() -> mockClient, 2000L, -1));
	}

	@Test
	void testWaitUntilReadySuccessFirstTry() {
		when(mockClient.checkConnection()).thenReturn(mockConnectionResult);
		when(mockConnectionResult.isConnected()).thenReturn(true);

		assertTrue(connectionChecker.waitUntilReady());
		verify(mockClient, times(1)).checkConnection();
	}

	@Test
	void testWaitUntilReadySuccessAfterRetries() {
		ConnectionChecker checker = new ConnectionChecker(() -> mockClient, 100L, 3);

		when(mockClient.checkConnection()).thenReturn(mockConnectionResult);
		when(mockConnectionResult.isConnected())
			.thenReturn(false)  // First attempt fails
			.thenReturn(false)  // Second attempt fails
			.thenReturn(true);  // Third attempt succeeds

		assertTrue(checker.waitUntilReady());
		verify(mockClient, times(3)).checkConnection();
	}

	@Test
	void testWaitUntilReadyFailsAfterMaxAttempts() {
		ConnectionChecker checker = new ConnectionChecker(() -> mockClient, 100L, 2);

		when(mockClient.checkConnection()).thenThrow(new FailedRequestException("Connection failed"));

		RuntimeException exception = assertThrows(RuntimeException.class,
			() -> checker.waitUntilReady());

		assertTrue(exception.getMessage().contains("Failed to connect to MarkLogic after 2 attempts"));
		verify(mockClient, times(2)).checkConnection();
	}

	@Test
	void testDefaultValues() {
		assertEquals(3000L, connectionChecker.getWaitInterval());
		assertEquals(20, connectionChecker.getMaxAttempts());
	}

	@Test
	void testImmutability() {
		// Test that objects are immutable - getters return the values set in constructor
		ConnectionChecker checker1 = new ConnectionChecker(() -> mockClient);
		assertEquals(3000L, checker1.getWaitInterval());
		assertEquals(20, checker1.getMaxAttempts());

		ConnectionChecker checker2 = new ConnectionChecker(() -> mockClient, 5000L, 30);
		assertEquals(5000L, checker2.getWaitInterval());
		assertEquals(30, checker2.getMaxAttempts());

		// Original checker should still have default values
		assertEquals(3000L, checker1.getWaitInterval());
		assertEquals(20, checker1.getMaxAttempts());
	}
}
