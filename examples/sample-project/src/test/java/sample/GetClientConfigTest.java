package sample;

import com.marklogic.client.ext.DatabaseClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetClientConfigTest extends AbstractSampleProjectTest {

	@Autowired
	ApplicationContext applicationContext;

	/**
	 * This is a simple test to show how a Spring bean can be manually retrieved from the Spring container via a method
	 * in AbstractSpringTest. We also confirm that certain properties have been read from gradle.properties.
	 */
	@Test
	public void testConfig() {
		DatabaseClientConfig config = applicationContext.getBean(DatabaseClientConfig.class);
		assertEquals("localhost", config.getHost());
		assertEquals(8101, config.getPort());
	}

}
