package com.marklogic.appdeployer;

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
}
