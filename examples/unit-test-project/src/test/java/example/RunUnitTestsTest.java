package example;

import com.marklogic.junit5.spring.MarkLogicUnitTestsTest;

/**
 * Example of running each test module as a separate JUnit test. The parent class handles all the work, which includes
 * reading config from gradle.properties and gradle-local.properties. This class is needed though so that JUnit has
 * something it can find and run in the test source tree.
 */
public class RunUnitTestsTest extends MarkLogicUnitTestsTest {

}
