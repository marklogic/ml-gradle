package com.marklogic.appdeployer.mgmt;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.app.AppManager;
import com.marklogic.appdeployer.app.ConfigDir;
import com.marklogic.appdeployer.app.DefaultConfiguration;
import com.marklogic.appdeployer.mgmt.admin.AdminConfig;
import com.marklogic.appdeployer.mgmt.admin.AdminManager;
import com.marklogic.appdeployer.project.plugin.RestApiPlugin;
import com.marklogic.junit.spring.LoggingTestExecutionListener;

/**
 * Base class for tests that run against the new management API in ML8. Main purpose is to provide convenience methods
 * for quickly creating and deleting a sample application.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MgmtTestConfig.class })
@TestExecutionListeners({ LoggingTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
public abstract class AbstractMgmtTest extends Assert {

    public final static String SAMPLE_APP_NAME = "sample-app";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected ManageConfig manageConfig;

    @Autowired
    private AdminConfig adminConfig;

    protected ConfigDir configDir;
    protected ManageClient manageClient;
    protected AppManager appManager;
    protected AdminManager adminMgr;
    protected ConfigurableApplicationContext projectAppContext;

    protected AppConfig appConfig;

    @Before
    public void initialize() {
        initializeAppConfig();

        configDir = new ConfigDir(new File("src/test/resources/sample-app/src/main/ml-config"));
        manageClient = new ManageClient(manageConfig);
        adminMgr = new AdminManager(adminConfig);
    }

    protected void initializeAppManager() {
        initializeAppManager(DefaultConfiguration.class);
    }

    protected void initializeAppManager(Class<?> configurationClass) {
        projectAppContext = new AnnotationConfigApplicationContext(configurationClass);
        appManager = new AppManager(projectAppContext, manageClient);
        appManager.setAdminManager(adminMgr);
    }

    @After
    public void closeProjectAppContext() {
        if (projectAppContext != null) {
            projectAppContext.close();
        }
    }

    protected void initializeAppConfig() {
        appConfig = new AppConfig();
        appConfig.setName("sample-app");
        appConfig.setRestPort(8540);
    }

    /**
     * Useful for when your test only needs a REST API and not full the sample app created.
     */
    protected void createSampleAppRestApi() {
        new RestApiPlugin().onCreate(appConfig, configDir, manageClient);
    }

    protected void deleteSampleApp() {
        try {
            appManager.deleteApp(appConfig, configDir);
        } catch (Exception e) {
            logger.warn("Error while waiting for MarkLogic to restart: " + e.getMessage());
        }
    }
}
