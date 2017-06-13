package com.marklogic.client.ext.spring.config;

import com.marklogic.client.ext.AbstractIntegrationTest;
import com.marklogic.client.ext.helper.DatabaseClientConfig;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringConfigurationTest extends AbstractIntegrationTest implements ApplicationContextAware {

    private ApplicationContext ctx;

    @Test
    public void getDatabaseClient() {
        for (String beanName : ctx.getBeanDefinitionNames()) {
	        System.out.println(beanName);
        }
        Assert.assertTrue(ctx.containsBeanDefinition("com.marklogic.client.ext.spring.SpringDatabaseClientConfig"));
        Assert.assertTrue(ctx.containsBeanDefinition("databaseClientProvider"));
        Assert.assertTrue(ctx.containsBeanDefinition("xccTemplate"));
        DatabaseClientConfig config = (DatabaseClientConfig) ctx.getBean("com.marklogic.client.ext.spring.SpringDatabaseClientConfig");
        Assert.assertEquals("localhost", config.getHost());
        Assert.assertEquals(8000, config.getPort());
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
