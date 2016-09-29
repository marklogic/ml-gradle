package com.marklogic.client.spring.config;

import com.marklogic.client.helper.DatabaseClientConfig;
import com.marklogic.client.helper.LoggingObject;
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
public class SpringConfigurationTest extends LoggingObject implements ApplicationContextAware {

    private ApplicationContext ctx;
    
    @Test
    public void getDatabaseClient() {
        for (String beanName : ctx.getBeanDefinitionNames()) {
            logger.info(beanName);
        }
        Assert.assertTrue(ctx.containsBeanDefinition("com.marklogic.client.spring.SpringDatabaseClientConfig"));
        Assert.assertTrue(ctx.containsBeanDefinition("databaseClientProvider"));
        Assert.assertTrue(ctx.containsBeanDefinition("xccTemplate"));
        DatabaseClientConfig config = (DatabaseClientConfig) ctx.getBean("com.marklogic.client.spring.SpringDatabaseClientConfig");
        Assert.assertEquals("localhost", config.getHost());
        Assert.assertEquals(8000, config.getPort());
    }
    
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
