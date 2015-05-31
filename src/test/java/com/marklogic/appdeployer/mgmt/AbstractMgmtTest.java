package com.marklogic.appdeployer.mgmt;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.project.ConfigDir;
import com.marklogic.appdeployer.project.DefaultConfiguration;
import com.marklogic.appdeployer.project.ProjectManager;

/**
 * Base class for tests that run against the new management API in ML8. Main purpose is to provide convenience methods
 * for quickly creating and deleting a sample application.
 */
public abstract class AbstractMgmtTest extends Assert {

    public final static String SAMPLE_APP_NAME = "sample-app";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected ManageConfig manageConfig;
    protected ManageClient manageClient;

    protected ConfigDir configDir;
    protected ProjectManager configMgr;
    protected ConfigurableApplicationContext projectAppContext;

    protected AppConfig appConfig;

    @Before
    public void initialize() {
        manageClient = new ManageClient(new ManageConfig());
        configDir = new ConfigDir(new File("src/test/resources/sample-app/src/main/ml-config"));
        projectAppContext = new AnnotationConfigApplicationContext(DefaultConfiguration.class);
        configMgr = new ProjectManager(projectAppContext, manageClient);
        initializeAppConfig();
    }

    @After
    public void closeProjectAppContext() {
        if (projectAppContext != null) {
            projectAppContext.close();
        }
    }

    protected void createSampleApp() {
        configMgr.createRestApi(configDir, appConfig);
    }

    protected void initializeAppConfig() {
        appConfig = new AppConfig();
        appConfig.setName("sample-app");
        appConfig.setRestPort(8540);
    }

    protected void deleteSampleApp() {
        try {
            configMgr.deleteRestApiAndWaitForRestart(appConfig, true, true);
        } catch (Exception e) {
            logger.warn("Error while waiting for MarkLogic to restart: " + e.getMessage());
        }
    }
}
