package com.marklogic.client.spring;

import com.marklogic.client.helper.DatabaseClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SpringDatabaseClientConfig extends DatabaseClientConfig {
    
    @Autowired
    public SpringDatabaseClientConfig(
            @Value("${marklogic.host}") String host,
            @Value("${marklogic.port}") int port,
            @Value("${marklogic.username}") String username,
            @Value("${marklogic.password}") String password) {
        super(host, port, username, password);
    }
    
    
}
