package com.marklogic.mgmt;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.marklogic.junit.BaseTestHelper;
import com.marklogic.junit.spring.LoggingTestExecutionListener;
import com.marklogic.mgmt.admin.AdminConfig;
import com.marklogic.mgmt.admin.AdminManager;

/**
 * Base class for tests that just talk to the Mgmt API and don't depend on an AppDeployer instance.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@TestExecutionListeners({ LoggingTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
public abstract class AbstractMgmtTest extends BaseTestHelper {

    @Autowired
    protected ManageConfig manageConfig;

    @Autowired
    protected AdminConfig adminConfig;

    // Intended to be used by subclasses
    protected ManageClient manageClient;
    protected AdminManager adminManager;

    @Before
    public void initializeManageClient() {
        manageClient = new ManageClient(manageConfig);
        adminManager = new AdminManager(adminConfig);
    }

    protected String format(String s, Object... args) {
        return String.format(s, args);
    }
}
