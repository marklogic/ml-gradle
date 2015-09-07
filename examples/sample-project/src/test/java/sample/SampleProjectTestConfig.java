package sample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.marklogic.client.DatabaseClientFactory;
import com.rjrudin.marklogic.client.DatabaseClientConfig;
import com.rjrudin.marklogic.junit.spring.BasicTestConfig;
import com.rjrudin.marklogic.modulesloader.ssl.SimpleX509TrustManager;

@Configuration
@PropertySource({ "file:gradle.properties" })
public class SampleProjectTestConfig extends BasicTestConfig {

    /**
     * Because the REST API servers for this project require SSL, we have to modify the DatabaseClientConfig that's
     * created by default in BasicConfig.
     */
    @Bean
    public DatabaseClientConfig databaseClientConfig() {
        DatabaseClientConfig config = new DatabaseClientConfig(getMlHost(), getRestPort(), getMlUsername(),
                getMlPassword());
        config.setSslContext(SimpleX509TrustManager.newSSLContext());
        config.setSslHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier.ANY);
        return config;
    }
}
