package sample;

import org.junit.Test;

import com.marklogic.clientutil.DatabaseClientConfig;

public class GetClientConfigTest extends AbstractSampleProjectTest {

    /**
     * This is a simple test to show how a Spring bean can be manually retrieved from the Spring container via a method
     * in AbstractSpringTest. We also confirm that certain properties have been read from gradle.properties.
     */
    @Test
    public void testConfig() {
        DatabaseClientConfig config = getApplicationContext().getBean(DatabaseClientConfig.class);
        assertEquals("localhost", config.getHost());
        assertEquals(8101, config.getPort());
    }

}
