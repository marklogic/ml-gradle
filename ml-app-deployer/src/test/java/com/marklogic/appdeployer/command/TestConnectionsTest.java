package com.marklogic.appdeployer.command;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.appdeployer.command.security.DeployCertificateTemplatesCommand;
import com.marklogic.mgmt.ManageClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestConnectionsTest extends AbstractAppDeployerTest {

	private TestConnectionsCommand command = new TestConnectionsCommand();

	@AfterEach
	void afterEach() {
		undeploySampleApp();
	}

	/**
	 * Multiple scenarios are tested here as they can all use the same deployed app.
	 */
	@Test
	void allConnectionsSucceed() {
		appConfig.setTestRestPort(SAMPLE_APP_TEST_REST_PORT);
		initializeAppDeployer(new DeployRestApiServersCommand(), new DeployCertificateTemplatesCommand());
		deploySampleApp();

		verifyAllConnectionsSucceed();
		verifyConnectionsSucceedWhenRestServersRequireSSL();
		verifyResultsWhenManageConnectionFails();
	}

	/**
	 * In this scenario, the 3 REST API app servers don't exist yet - which is fine, no error should be thrown,
	 * we just don't get any test results for them.
	 */
	@Test
	void restServersDontExist() {
		appConfig.setAppServicesPort(SAMPLE_APP_REST_PORT);
		appConfig.setTestRestPort(SAMPLE_APP_TEST_REST_PORT);

		CommandContext context = new CommandContext(appConfig, manageClient, adminManager);
		TestConnectionsCommand command = new TestConnectionsCommand();
		// Smoke test, just expecting logging
		command.execute(context);

		TestConnectionsCommand.TestResults results = command.testConnections(context);
		assertFalse(results.anyTestFailed());

		assertTrue(results.getManageTestResult().isSucceeded());
		assertTrue(results.getAdminTestResult().isSucceeded());
		assertNull(results.getAppServicesTestResult());
		assertNull(results.getRestServerTestResult());
		assertNull(results.getTestRestServerTestResult());
	}

	/**
	 * In this scenario, the Manage test fails, meaning we can't test any of the REST API app servers since we can't
	 * check to see if they exist or not via the Manage app server.
	 */
	private void verifyResultsWhenManageConnectionFails() {
		final String validPassword = manageConfig.getPassword();
		try {
			manageConfig.setPassword("Wrong password");
			TestConnectionsCommand.TestResults results =
				command.testConnections(new CommandContext(appConfig, new ManageClient(manageConfig), adminManager));

			assertTrue(results.anyTestFailed());
			assertFalse(results.getManageTestResult().isSucceeded());
			assertTrue(results.getAdminTestResult().isSucceeded());
			assertNull(results.getAppServicesTestResult());
			assertNull(results.getRestServerTestResult());
			assertNull(results.getTestRestServerTestResult());
		} finally {
			manageConfig.setPassword(validPassword);
		}
	}

	private void verifyConnectionsSucceedWhenRestServersRequireSSL() {
		appConfig.setSimpleSslConfig();
		configureRestServersToRequireSSL();
		try {
			TestConnectionsCommand.TestResults results =
				command.testConnections(new CommandContext(appConfig, manageClient, adminManager));
			assertFalse(results.anyTestFailed());

			TestConnectionsCommand.TestResult restResult = results.getRestServerTestResult();
			assertEquals("https", restResult.getScheme());
			assertTrue(restResult.toString().startsWith(
					"Configured to connect to https://localhost:8004 using 'digest' authentication and username of 'admin'"),
				"Unexpected message: " + restResult);

			TestConnectionsCommand.TestResult testRestResult = results.getTestRestServerTestResult();
			assertEquals("https", testRestResult.getScheme());
			assertTrue(testRestResult.toString().startsWith(
					"Configured to connect to https://localhost:8005 using 'digest' authentication and username of 'admin'"),
				"Unexpected message: " + testRestResult);

			// Disable SSL on the client side and verify we get errors
			appConfig.setRestSslContext(null);
			appConfig.setRestTrustManager(null);
			appConfig.setRestSslHostnameVerifier(null);

			results = command.testConnections(new CommandContext(appConfig, manageClient, adminManager));
			restResult = results.getRestServerTestResult();
			assertFalse(restResult.isSucceeded());
			assertEquals("Received 403: Forbidden", restResult.getMessage(), "Unfortunately, the Java Client receives " +
				"nothing indicating an SSL issue; the request doesn't even show up in the app server's AccessLog. " +
				"All the user gets is a 403 back when SSL is required by the client is not using it.");
			testRestResult = results.getTestRestServerTestResult();
			assertFalse(testRestResult.isSucceeded());
			assertEquals("Received 403: Forbidden", testRestResult.getMessage());
		} finally {
			configureRestServersToNotRequireSSL();
			appConfig.setRestSslContext(null);
			appConfig.setRestTrustManager(null);
			appConfig.setRestSslHostnameVerifier(null);
		}
	}

	private void verifyAllConnectionsSucceed() {
		CommandContext context = new CommandContext(appConfig, manageClient, adminManager);

		// Smoke test - this just logs text on success.
		command.execute(context);
		TestConnectionsCommand.TestResults results = command.testConnections(context);
		assertFalse(results.anyTestFailed());

		final String host = manageConfig.getHost();

		TestConnectionsCommand.TestResult manageResult = results.getManageTestResult();
		assertEquals(host, manageResult.getHost());
		assertEquals(manageConfig.getPort(), manageResult.getPort());
		assertEquals("http", manageResult.getScheme());
		assertEquals(manageConfig.getUsername(), manageResult.getUsername());
		assertEquals(manageConfig.getAuthType(), manageResult.getAuthType());
		assertTrue(manageResult.isSucceeded());
		assertTrue(manageResult.getMessage().startsWith("MarkLogic version:"),
			"Unexpected message; the MarkLogic version should be shown as a quick confirmation of the version of " +
				"MarkLogic that the user is connecting to; actual message: " + manageResult.getMessage());
		assertTrue(
			manageResult.toString().startsWith("Configured to connect to http://localhost:8002 using 'digest' authentication and username of 'admin'"),
			"Unexpected toString content: " + manageResult
		);

		TestConnectionsCommand.TestResult adminResult = results.getAdminTestResult();
		assertEquals(host, adminResult.getHost());
		assertEquals(adminConfig.getPort(), adminResult.getPort());
		assertEquals("http", adminResult.getScheme());
		assertEquals(adminConfig.getUsername(), adminResult.getUsername());
		assertEquals(adminConfig.getAuthType(), adminResult.getAuthType());
		assertTrue(adminResult.isSucceeded());
		assertTrue(adminResult.getMessage().startsWith("MarkLogic server timestamp:"),
			"Unexpected message; the server timestamp should be shown as a quick confirmation of the timezone that " +
				"the MarkLogic instance is running in; actual message: " + adminResult.getMessage());
		assertTrue(
			adminResult.toString().startsWith("Configured to connect to http://localhost:8001 using 'digest' authentication and username of 'admin'"),
			"Unexpected toString content: " + adminResult
		);

		TestConnectionsCommand.TestResult appServicesResult = results.getAppServicesTestResult();
		assertEquals(host, appServicesResult.getHost());
		assertEquals(appConfig.getAppServicesPort(), appServicesResult.getPort());
		assertEquals("http", appServicesResult.getScheme());
		assertEquals(appConfig.getAppServicesUsername(), appServicesResult.getUsername());
		assertEquals(appConfig.getAppServicesSecurityContextType().name().toLowerCase(), appServicesResult.getAuthType());
		assertTrue(appServicesResult.isSucceeded());
		assertNull(appServicesResult.getMessage(), "The Java Client doesn't provide any success message when " +
			"checkConnection succeeds.");
		assertTrue(
			appServicesResult.toString().startsWith("Configured to connect to http://localhost:8000 using 'digest' authentication and username of 'admin'"),
			"Unexpected toString content: " + appServicesResult
		);

		TestConnectionsCommand.TestResult restResult = results.getRestServerTestResult();
		assertEquals(host, restResult.getHost());
		assertEquals(appConfig.getRestPort(), restResult.getPort());
		assertEquals("http", restResult.getScheme());
		assertEquals(appConfig.getRestAdminUsername(), restResult.getUsername());
		assertEquals(appConfig.getRestSecurityContextType().name().toLowerCase(), restResult.getAuthType());
		assertTrue(restResult.isSucceeded());
		assertNull(restResult.getMessage(), "The Java Client doesn't provide any success message when " +
			"checkConnection succeeds.");
		assertTrue(
			restResult.toString().startsWith("Configured to connect to http://localhost:8004 using 'digest' authentication and username of 'admin'"),
			"Unexpected toString content: " + restResult
		);

		TestConnectionsCommand.TestResult testRestResult = results.getTestRestServerTestResult();
		assertEquals(host, testRestResult.getHost());
		assertEquals(appConfig.getTestRestPort(), testRestResult.getPort());
		assertEquals("http", testRestResult.getScheme());
		assertEquals(appConfig.getRestAdminUsername(), testRestResult.getUsername());
		assertEquals(appConfig.getRestSecurityContextType().name().toLowerCase(), testRestResult.getAuthType());
		assertTrue(testRestResult.isSucceeded());
		assertNull(testRestResult.getMessage(), "The Java Client doesn't provide any success message when " +
			"checkConnection succeeds.");
		assertTrue(
			testRestResult.toString().startsWith("Configured to connect to http://localhost:8005 using 'digest' authentication and username of 'admin'"),
			"Unexpected toString content: " + testRestResult
		);
	}
}
