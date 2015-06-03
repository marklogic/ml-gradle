package com.marklogic.appdeployer;

import java.io.File;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.marklogic.appdeployer.plugin.restapis.CreateRestApiServersPlugin;
import com.marklogic.appdeployer.spring.SpringAppDeployer;
import com.marklogic.rest.mgmt.AbstractMgmtTest;
import com.marklogic.rest.mgmt.admin.AdminConfig;
import com.marklogic.rest.mgmt.admin.AdminManager;

/**
 * Base class for tests that depend on an AppDeployer instance.
 */
public abstract class AbstractAppDeployerTest extends AbstractMgmtTest {

    public final static String SAMPLE_APP_NAME = "sample-app";

    protected final static Integer SAMPLE_APP_REST_PORT = 8540;
    protected final static Integer SAMPLE_APP_TEST_REST_PORT = 8541;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected AdminConfig adminConfig;

    protected ConfigDir configDir;
    protected AppDeployer appDeployer;
    protected AdminManager adminManager;
    protected ConfigurableApplicationContext appManagerContext;

    protected AppConfig appConfig;

    @Before
    public void initialize() {
        initializeAppConfig();

        configDir = new ConfigDir(new File("src/test/resources/sample-app/src/main/ml-config"));
        adminManager = new AdminManager(adminConfig);
    }

    protected void initializeAppConfig() {
        appConfig = new AppConfig("src/test/resources/sample-app/src/main/ml-modules");
        appConfig.setName(SAMPLE_APP_NAME);
        appConfig.setRestPort(SAMPLE_APP_REST_PORT);
    }

    protected void initializeAppDeployer() {
        initializeAppDeployer(new CreateRestApiServersPlugin());
    }

    /**
     * Initialize an AppDeployer with the given set of plugins. Avoids having to create a Spring configuration.
     * 
     * @param plugins
     */
    protected void initializeAppDeployer(AppPlugin... plugins) {
        SimpleAppDeployer m = new SimpleAppDeployer(manageClient, adminManager);
        m.setAppPlugins(Arrays.asList(plugins));
        appDeployer = m;
    }

    /**
     * Initialize AppDeployer with a Spring Configuration class.
     * 
     * @param configurationClass
     */
    protected void initializeAppDeployer(Class<?> configurationClass) {
        appManagerContext = new AnnotationConfigApplicationContext(configurationClass);
        appDeployer = new SpringAppDeployer(appManagerContext, manageClient, adminManager);
    }

    @After
    public void closeAppContext() {
        if (appManagerContext != null) {
            appManagerContext.close();
        }
    }

    /**
     * Useful for when your test only needs a REST API and not full the sample app created.
     */
    protected void deployRestApi() {
        new CreateRestApiServersPlugin()
                .onDeploy(new AppPluginContext(appConfig, configDir, manageClient, adminManager));
    }

    protected void undeploySampleApp() {
        try {
            appDeployer.undeploy(appConfig, configDir);
        } catch (Exception e) {
            logger.warn("Error while waiting for MarkLogic to restart: " + e.getMessage());
        }
    }
}
