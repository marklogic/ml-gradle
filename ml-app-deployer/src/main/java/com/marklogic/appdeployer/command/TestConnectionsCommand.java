package com.marklogic.appdeployer.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.appdeployer.AppConfig;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.SecurityContextType;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.admin.AdminManager;
import com.marklogic.mgmt.cma.ConfigurationManager;
import com.marklogic.mgmt.resource.clusters.ClusterManager;
import com.marklogic.rest.util.RestConfig;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;

import javax.net.ssl.SSLContext;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Command for testing each of the connections that can be made to MarkLogic based on the configuration in a
 * {@code CommandContext}.
 *
 * @since 4.6.0
 */
public class TestConnectionsCommand extends AbstractCommand {

	/**
	 * If run in a deployment process, this should run immediately so as to fail fast.
	 */
	public TestConnectionsCommand() {
		setExecuteSortOrder(0);
	}

	/**
	 * Can be included in a deployment process so that the deployment fails if any of the connections fail.
	 *
	 * @param context
	 */
	@Override
	public void execute(CommandContext context) {
		TestResults results = testConnections(context);
		if (results.anyTestFailed()) {
			throw new RuntimeException(results.toString());
		}
		logger.info(results.toString());
	}

	/**
	 * Intended for execution outside a deployment process, where the client wants access to the test results and
	 * will choose how to present those to a user.
	 *
	 * @param context
	 * @return
	 */
	public TestResults testConnections(CommandContext context) {
		try {
			TestResult manageResult = testManageAppServer(context.getManageClient());
			TestResult adminResult = testAdminAppServer(context.getAdminManager());

			TestResult appServicesResult = null;
			TestResult restResult = null;
			TestResult testRestResult = null;
			if (manageResult.isSucceeded()) {
				List<Integer> serverPorts = getAppServerPorts(context.getManageClient());
				appServicesResult = testAppServicesAppServer(context.getAppConfig(), serverPorts);
				restResult = testRestAppServer(context.getAppConfig(), serverPorts);
				testRestResult = testTestRestAppServer(context.getAppConfig(), serverPorts);
			}

			return new TestResults(manageResult, adminResult, appServicesResult, restResult, testRestResult);
		} catch (Exception ex) {
			// We don't expect any exceptions above, as each connection test has its own try/catch block.
			// This is simply to pretty up the error a bit.
			throw new RuntimeException("Unable to test connections; cause: " + ex.getMessage(), ex);
		}
	}

	private List<Integer> getAppServerPorts(ManageClient manageClient) {
		JsonNode json = new ConfigurationManager(manageClient).getResourcesAsJson("server").getBody();
		ArrayNode servers = (ArrayNode) json.get("config").get(0).get("server");
		List<Integer> ports = new ArrayList<>();
		servers.forEach(server -> {
			if (server.has("port")) {
				ports.add(server.get("port").asInt());
			}
		});
		return ports;
	}

	private TestResult testAppServicesAppServer(AppConfig appConfig, List<Integer> serverPorts) {
		if (appConfig.getAppServicesPort() != null && serverPorts.contains(appConfig.getAppServicesPort())) {
			return testWithDatabaseClient(appConfig.getHost(), appConfig.getAppServicesPort(),
				appConfig.getAppServicesSslContext(), appConfig.getAppServicesSecurityContextType(),
				appConfig.getAppServicesUsername(), () -> appConfig.newAppServicesDatabaseClient(null));
		}
		return null;
	}

	private TestResult testRestAppServer(AppConfig appConfig, List<Integer> serverPorts) {
		if (appConfig.getRestPort() != null && serverPorts.contains(appConfig.getRestPort())) {
			return testWithDatabaseClient(appConfig.getHost(), appConfig.getRestPort(),
				appConfig.getRestSslContext(), appConfig.getRestSecurityContextType(),
				appConfig.getRestAdminUsername(), appConfig::newDatabaseClient);
		}
		return null;
	}

	private TestResult testTestRestAppServer(AppConfig appConfig, List<Integer> serverPorts) {
		if (appConfig.getTestRestPort() != null && serverPorts.contains(appConfig.getTestRestPort())) {
			return testWithDatabaseClient(appConfig.getHost(), appConfig.getTestRestPort(),
				appConfig.getRestSslContext(), appConfig.getRestSecurityContextType(),
				appConfig.getRestAdminUsername(), appConfig::newTestDatabaseClient);
		}
		return null;
	}

	public static class TestResults {
		private TestResult manageTestResult;
		private TestResult adminTestResult;
		private TestResult appServicesTestResult;
		private TestResult restServerTestResult;
		private TestResult testRestServerTestResult;

		public TestResults(TestResult manageTestResult, TestResult adminTestResult,
						   TestResult appServicesTestResult, TestResult restServerTestResult, TestResult testRestServerTestResult) {
			this.manageTestResult = manageTestResult;
			this.adminTestResult = adminTestResult;
			this.appServicesTestResult = appServicesTestResult;
			this.restServerTestResult = restServerTestResult;
			this.testRestServerTestResult = testRestServerTestResult;
		}

		public boolean anyTestFailed() {
			return Stream.of(manageTestResult, adminTestResult, appServicesTestResult, restServerTestResult, testRestServerTestResult)
				.anyMatch(test -> test != null && !test.isSucceeded());
		}

		public TestResult getManageTestResult() {
			return manageTestResult;
		}

		public TestResult getAdminTestResult() {
			return adminTestResult;
		}

		public TestResult getAppServicesTestResult() {
			return appServicesTestResult;
		}

		public TestResult getRestServerTestResult() {
			return restServerTestResult;
		}

		public TestResult getTestRestServerTestResult() {
			return testRestServerTestResult;
		}

		/**
		 * @return a multi-line summary of all the non-null test results. This is intended to provide a simple
		 * rendering of the test result data, suitable for use in ml-gradle.
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Manage App Server\n").append(getManageTestResult())
				.append("\n\nAdmin App Server\n").append(getAdminTestResult());
			if (getManageTestResult().isSucceeded()) {
				if (getAppServicesTestResult() != null) {
					sb.append("\n\nApp-Services App Server\n").append(getAppServicesTestResult());
				} else {
					sb.append("\n\nNo test run for the App-Services App Server as either a port is not configured for it or it has not been deployed yet");
				}
				if (getRestServerTestResult() != null) {
					sb.append("\n\nREST API App Server\n").append(getRestServerTestResult());
				} else {
					sb.append("\n\nNo test run for a REST API App Server as either a port is not configured for it or it has not been deployed yet.");
				}
				if (getTestRestServerTestResult() != null) {
					sb.append("\n\nTest REST API App Server\n").append(getTestRestServerTestResult());
				} else {
					sb.append("\n\nNo test run for a Test REST API App Server as either a port is not configured for it or it has not been deployed yet.");
				}
			} else {
				sb.append("\n\nCould not test connections against the App-Services or REST API App Servers " +
					"due to the Manage App Server connection failing.");
			}
			return sb.toString();
		}
	}

	public static class TestResult {
		private String host;
		private int port;
		private String scheme;
		private String authType;
		private String username;
		private boolean succeeded;
		private String message;

		public TestResult(RestConfig restConfig, boolean succeeded, String message) {
			this(restConfig.getHost(), restConfig.getPort(), restConfig.getScheme(), restConfig.getAuthType(),
				restConfig.getUsername(), succeeded, message);
		}

		public TestResult(RestConfig restConfig, Exception ex) {
			this(restConfig, false, ex.getMessage());
		}

		public TestResult(String host, int port, String scheme, String authType, String username, DatabaseClient.ConnectionResult result) {
			this.host = host;
			this.port = port;
			this.scheme = scheme;
			this.authType = authType;
			this.username = username;
			this.succeeded = result.isConnected();
			if (!result.isConnected()) {
				this.message = String.format("Received %d: %s", result.getStatusCode(), result.getErrorMessage());
			}
		}

		public TestResult(String host, int port, String scheme, String authType, String username, boolean succeeded, String message) {
			this.host = host;
			this.port = port;
			this.scheme = scheme;
			this.authType = authType;
			this.username = username;
			this.succeeded = succeeded;
			this.message = message;
		}

		public String getHost() {
			return host;
		}

		public int getPort() {
			return port;
		}

		public String getScheme() {
			return scheme;
		}

		public boolean isSucceeded() {
			return succeeded;
		}

		public String getMessage() {
			return message;
		}

		public String getAuthType() {
			return authType;
		}

		public String getUsername() {
			return username;
		}

		/**
		 * @return a multi-line representation of the test result. This is intended to provide a simple
		 * rendering of the test result data, suitable for use in ml-gradle.
		 */
		@Override
		public String toString() {
			String result = String.format("Configured to connect to %s://%s:%d using '%s' authentication",
				getScheme(), getHost(), getPort(), getAuthType());
			if (getUsername() != null) {
				result += String.format(" and username of '%s'", getUsername());
			}
			if (isSucceeded()) {
				result += "\nConnected successfully";
				return getMessage() != null ? result + "; " + getMessage() : result;
			}
			return result + "\nFAILED TO CONNECT; cause: " + message;
		}
	}

	private TestResult testManageAppServer(ManageClient client) {
		ResponseErrorHandler originalErrorHandler = client.getRestTemplate().getErrorHandler();
		client.getRestTemplate().setErrorHandler(new DefaultResponseErrorHandler());
		try {
			String version = new ClusterManager(client).getVersion();
			return new TestResult(client.getManageConfig(), true, "MarkLogic version: " + version);
		} catch (Exception ex) {
			if (ex instanceof HttpClientErrorException && ((HttpClientErrorException) ex).getRawStatusCode() == 404) {
				return new TestResult(client.getManageConfig(), false,
					"Unable to access /manage/v2; received 404; unexpected response: " + ex.getMessage());
			} else {
				return new TestResult(client.getManageConfig(), ex);
			}
		} finally {
			client.getRestTemplate().setErrorHandler(originalErrorHandler);
		}
	}

	private TestResult testAdminAppServer(AdminManager adminManager) {
		try {
			String timestamp = adminManager.getServerTimestamp();
			return new TestResult(adminManager.getAdminConfig(), true, "MarkLogic server timestamp: " + timestamp);
		} catch (Exception ex) {
			return new TestResult(adminManager.getAdminConfig(), ex);
		}
	}

	private TestResult testWithDatabaseClient(String host, Integer port, SSLContext sslContext,
											  SecurityContextType securityContextType, String username, Supplier<DatabaseClient> supplier) {
		if (port == null) {
			return null;
		}
		final String scheme = sslContext != null ? "https" : "http";
		final String authType = securityContextType != null ? securityContextType.name().toLowerCase() : "unknown";
		DatabaseClient client = null;
		try {
			client = supplier.get();
			return new TestResult(host, port, scheme, authType, username, client.checkConnection());
		} catch (Exception ex) {
			return new TestResult(host, port, scheme, authType, username, false, ex.getMessage());
		} finally {
			if (client != null) {
				client.release();
			}
		}
	}
}
