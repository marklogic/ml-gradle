package com.rjrudin.marklogic.appdeployer;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import com.rjrudin.marklogic.appdeployer.command.Command;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.restapis.CreateRestApiServersCommand;
import com.rjrudin.marklogic.appdeployer.impl.SimpleAppDeployer;
import com.rjrudin.marklogic.mgmt.AbstractMgmtTest;
import com.rjrudin.marklogic.mgmt.ManageConfig;
import com.rjrudin.marklogic.xcc.XccTemplate;

/**
 * Base class for tests that depend on an AppDeployer instance. You can extend this directly to write a test for a
 * particular resource, but check out AbstractManageResourceTest (and its subclasses) to see if that will work for you
 * instead, as that saves a lot of work.
 */
public abstract class AbstractAppDeployerTest extends AbstractMgmtTest {

    public final static String SAMPLE_APP_NAME = "sample-app";

    protected final static Integer SAMPLE_APP_REST_PORT = 8540;
    protected final static Integer SAMPLE_APP_TEST_REST_PORT = 8541;

    @Autowired
    private ManageConfig manageConfig;

    private ConfigurableApplicationContext appManagerContext;

    // Intended to be used by subclasses
    protected AppDeployer appDeployer;
    protected AppConfig appConfig;

    @Before
    public void initialize() {
        initializeAppConfig();
    }

    protected void initializeAppConfig() {
        appConfig = new AppConfig("src/test/resources/sample-app/src/main/ml-modules");
        appConfig.setName(SAMPLE_APP_NAME);
        appConfig.setRestPort(SAMPLE_APP_REST_PORT);
        ConfigDir configDir = new ConfigDir(new File("src/test/resources/sample-app/src/main/ml-config"));
        appConfig.setConfigDir(configDir);

        // Assume that the manager user can also be used as the REST admin user
        appConfig.setRestAdminUsername(manageConfig.getUsername());
        appConfig.setRestAdminPassword(manageConfig.getPassword());
    }

    protected void initializeAppDeployer() {
        initializeAppDeployer(new CreateRestApiServersCommand());
    }

    /**
     * Initialize an AppDeployer with the given set of commands. Avoids having to create a Spring configuration.
     * 
     * @param commands
     */
    protected void initializeAppDeployer(Command... commands) {
        appDeployer = new SimpleAppDeployer(manageClient, adminManager, commands);
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
        new CreateRestApiServersCommand().execute(new CommandContext(appConfig, manageClient, adminManager));
    }

    protected void undeploySampleApp() {
        try {
            appDeployer.undeploy(appConfig);
        } catch (Exception e) {
            logger.warn("Error while waiting for MarkLogic to restart: " + e.getMessage());
        }
    }

    protected XccTemplate newModulesXccTemplate() {
        return new XccTemplate(format("xcc://%s:%s@%s:8000/%s", appConfig.getRestAdminUsername(),
                appConfig.getRestAdminPassword(), appConfig.getHost(), appConfig.getModulesDatabaseName()));
    }

    /**
     * This ensures that modules aren't not loaded because of the timestamps file.
     */
    protected void deleteModuleTimestampsFile() {
        File f = new File("build/ml-last-configured-timestamps.properties");
        if (f.exists()) {
            logger.info("Deleting module timestamps file: " + f.getAbsolutePath());
            f.delete();
        }
    }

}
