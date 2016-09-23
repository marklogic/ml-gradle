package com.marklogic.client.spring.config;

import com.marklogic.client.helper.DatabaseClientConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( classes = {MarkLogicApplicationContext.class} )
public class SpringConfigurationTest implements ApplicationContextAware {

    private ApplicationContext ctx;
    
    @Test
    public void getDatabaseClient() {
        Assert.assertTrue(ctx.containsBeanDefinition("databaseClientConfig"));
        Assert.assertTrue(ctx.containsBeanDefinition("databaseClient"));
        DatabaseClientConfig config = (DatabaseClientConfig) ctx.getBean("databaseClientConfig");
        Assert.assertEquals("localhost", config.getHost());
        Assert.assertEquals(8000, config.getPort());
    }
    
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
