package com.marklogic.appdeployer;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.marklogic.mgmt.util.SimplePropertySource;

public class DefaultAppConfigFactoryTest extends Assert {

    private DefaultAppConfigFactory sut;

    @Test
    public void gradleStyleProperties() {
        sut = new DefaultAppConfigFactory(new SimplePropertySource("mlHost", "somehost", "mlUsername", "someuser"));
        AppConfig config = sut.newAppConfig();
        assertEquals("somehost", config.getHost());
        assertEquals("someuser", config.getRestAdminUsername());
    }

    @Test
    public void springStyleProperties() {
        sut = new DefaultAppConfigFactory(
                new SimplePropertySource("marklogic.mlHost", "springhost", "marklogic.mlUsername", "springuser"));
        AppConfig config = sut.newAppConfig();
        assertEquals("springhost", config.getHost());
        assertEquals("springuser", config.getRestAdminUsername());
    }

    @Test
    public void unrecognizedProperties() {
        sut = new DefaultAppConfigFactory(new SimplePropertySource("foo.mlHost", "host", "foo.mlUsername", "user"));
        AppConfig config = sut.newAppConfig();
        assertEquals("Should use default", "localhost", config.getHost());
        assertEquals("Should use default", "admin", config.getRestAdminUsername());
    }

    /**
     * As of 2.2.0.
     */
    @Test
    public void allProperties() {
        Properties p = new Properties();
        p.setProperty("mlHost", "prophost");
        p.setProperty("mlAppName", "propname");
        p.setProperty("mlRestPort", "4321");
        p.setProperty("mlTestRestPort", "8765");
        p.setProperty("mlUsername", "propuser1");
        p.setProperty("mlPassword", "proppassword");
        p.setProperty("mlRestAdminUsername", "propuser2");
        p.setProperty("mlRestAdminPassword", "proppassword2");
        p.setProperty("mlContentForestsPerHost", "17");
        p.setProperty("mlModulePermissions", "some-perm,read,some-perm,update");
        p.setProperty("mlAdditionalBinaryExtensions", ".gradle,.properties");
        p.setProperty("mlConfigDir", "src/test/resources/sample-app/empty-ml-config");
        p.setProperty("mlSimpleSsl", "anyvalue");
        p.setProperty("mlModulesDatabaseName", "my-modules");
        
        sut = new DefaultAppConfigFactory(new SimplePropertySource(p));
        AppConfig config = sut.newAppConfig();

        assertEquals("prophost", config.getHost());
        assertEquals("propname", config.getName());
        assertEquals((Integer) 4321, config.getRestPort());
        assertEquals((Integer) 8765, config.getTestRestPort());
        assertEquals("propuser2", config.getRestAdminUsername());
        assertEquals("proppassword2", config.getRestAdminPassword());
        assertEquals((Integer) 17, config.getContentForestsPerHost());
        assertEquals("some-perm,read,some-perm,update", config.getModulePermissions());
        String[] extensions = config.getAdditionalBinaryExtensions();
        assertEquals(".gradle", extensions[0]);
        assertEquals(".properties", extensions[1]);
        assertTrue(config.getConfigDir().getBaseDir().getAbsolutePath().contains("empty-ml-config"));
        assertNotNull(config.getRestSslContext());
        assertNotNull(config.getRestSslHostnameVerifier());
        assertEquals("my-modules", config.getModulesDatabaseName());
    }

    @Test
    public void mlUsernameAndPassword() {
        sut = new DefaultAppConfigFactory(
                new SimplePropertySource("mlUsername", "customuser", "mlPassword", "custompassword"));
        AppConfig config = sut.newAppConfig();

        assertEquals("When mlRestAdminUsername is not set, mlUsername should be used", "customuser",
                config.getRestAdminUsername());
        assertEquals("When mlRestAdminPassword is not set, mlPassword should be used", "custompassword",
                config.getRestAdminPassword());

        assertNull("SSL context should be null by default", config.getRestSslContext());
        assertNull("SSL hostname verifier should be null by default", config.getRestSslHostnameVerifier());
    }
}
