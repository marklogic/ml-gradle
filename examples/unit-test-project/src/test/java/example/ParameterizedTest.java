package example;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.test.unit.TestManager;
import com.marklogic.test.unit.TestModule;
import com.marklogic.test.unit.TestResult;
import com.marklogic.test.unit.TestSuiteResult;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Example of running each test module as a separate JUnit test.
 */
@RunWith(Parameterized.class)
public class ParameterizedTest extends Assert {

	private TestModule testModule;

	private static TestManager testManager;
	private static DatabaseClient databaseClient;

	public ParameterizedTest(TestModule testModule) {
		this.testModule = testModule;
	}

	@AfterClass
	public static void releaseDatabaseClient() {
		if (databaseClient != null) {
			databaseClient.release();
		}
	}

	/**
	 * This sets up the parameters for our test by getting a list of the test modules.
	 * <p>
	 * Also creates a DatabaseClient based on the values in gradle.properties. Typically, properties will be retrieved via
	 * a more robust mechanism, like Spring's test framework support.
	 *
	 * @return
	 * @throws Exception
	 */
	@Parameterized.Parameters(name = "{index}: {0}")
	public static List<TestModule> getTestModules() throws IOException {
		Properties props = new Properties();
		props.load(new FileReader("gradle.properties"));
		final String host = props.getProperty("mlHost");
		final int port = Integer.parseInt(props.getProperty("mlTestRestPort"));
		final String username = props.getProperty("mlUsername");
		final String password = props.getProperty("mlPassword");

		databaseClient = DatabaseClientFactory.newClient(host, port,
			new DatabaseClientFactory.DigestAuthContext(username, password));
		testManager = new TestManager(databaseClient);
		return testManager.list();
	}

	@Test
	public void test() {
		TestSuiteResult result = testManager.run(this.testModule);
		for (TestResult testResult : result.getTestResults()) {
			String failureXml = testResult.getFailureXml();
			if (failureXml != null) {
				fail(String.format("Test %s in suite %s failed, cause: %s", testResult.getName(), testModule.getSuite(), failureXml));
			}
		}
	}
}
