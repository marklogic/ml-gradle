package com.marklogic.rest.mgmt;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.marklogic.junit.BaseTestHelper;
import com.marklogic.junit.spring.LoggingTestExecutionListener;

/**
 * Base class for tests that just talk to the Mgmt API and don't depend on an AppDeployer instance.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@TestExecutionListeners({ LoggingTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
public abstract class AbstractMgmtTest extends BaseTestHelper {

    @Autowired
    private ManageConfig manageConfig;

    // Intended to be used by subclasses
    protected ManageClient manageClient;

    protected static HttpStatus.Series[] httpStatus;

    @Before
    public void initializeManageClient() {
        manageClient = new ManageClient(manageConfig);
    }

    protected String format(String s, Object... args) {
        return String.format(s, args);
    }
}
