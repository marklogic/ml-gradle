package sample;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.rjrudin.marklogic.junit.spring.BasicTestConfig;

@Configuration
@PropertySource({ "file:gradle.properties" })
public class TestConfig extends BasicTestConfig {

    /**
     * Overriding this since this project currently doesn't have a test database, just the single content database.
     */
    @Override
    protected String buildContentDatabaseName(String mlAppName) {
        return mlAppName + "-content";
    }

}
