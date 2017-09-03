package sample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.marklogic.client.ext.helper.DatabaseClientConfig;
import com.marklogic.junit.spring.BasicTestConfig;

@Configuration
@PropertySource({ "file:gradle.properties" })
public class SampleProjectTestConfig extends BasicTestConfig {

    /**
     * If the REST API server required SSL, we can easily modify our ML DatabaseClient instance by overriding this
     * method and un-commenting the lines commented within it. Because the REST API servers for this project require
     * SSL, we have to modify the DatabaseClientConfig that's created by default in BasicConfig.
     */
    @Bean
    public DatabaseClientConfig databaseClientConfig() {
        DatabaseClientConfig config = new DatabaseClientConfig(getMlHost(), getRestPort(), getMlUsername(),
                getMlPassword());
        // config.setSslContext(SimpleX509TrustManager.newSSLContext());
        // config.setSslHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier.ANY);
        return config;
    }
}
