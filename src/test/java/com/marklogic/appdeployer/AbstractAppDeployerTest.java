package com.marklogic.appdeployer;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.modules.LoadModulesCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.appdeployer.impl.SimpleAppDeployer;
import com.marklogic.client.modulesloader.impl.DefaultModulesLoader;
import com.marklogic.mgmt.AbstractMgmtTest;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.xcc.template.XccTemplate;

/**
 * Base class for tests that depend on an AppDeployer instance. You can extend this directly to write a test for a
 * particular resource, but check out AbstractManageResourceTest (and its subclasses) to see if that will work for you
 * instead, as that saves a lot of work.
 */
public abstract class AbstractAppDeployerTest extends AbstractMgmtTest {

    public final static String SAMPLE_APP_NAME = "sample-app";

    protected final static Integer SAMPLE_APP_REST_PORT = 8540;
    protected final static Integer SAMPLE_APP_TEST_REST_PORT = 8541;

    // Intended to be used by subclasses
    protected AppDeployer appDeployer;
    protected AppConfig appConfig;

    @Before
    public void initialize() {
        initializeAppConfig();
    }

    protected void initializeAppConfig() {
        appConfig = new AppConfig("src/test/resources/sample-app/src/main/ml-modules", "src/test/resources/sample-app/src/main/ml-schemas");
        appConfig.setName(SAMPLE_APP_NAME);
        appConfig.setRestPort(SAMPLE_APP_REST_PORT);
        ConfigDir configDir = new ConfigDir(new File("src/test/resources/sample-app/src/main/ml-config"));
        appConfig.setConfigDir(configDir);

        // Assume that the manager user can also be used as the REST admin user
        appConfig.setRestAdminUsername(manageConfig.getUsername());
        appConfig.setRestAdminPassword(manageConfig.getPassword());
    }

    protected void initializeAppDeployer() {
        initializeAppDeployer(new DeployRestApiServersCommand());
    }

    /**
     * Initialize an AppDeployer with the given set of commands. Avoids having to create a Spring configuration.
     *
     * @param commands
     */
    protected void initializeAppDeployer(Command... commands) {
        appDeployer = new SimpleAppDeployer(manageClient, adminManager, commands);
    }

    protected void deploySampleApp() {
        appDeployer.deploy(appConfig);
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
     * This command is configured to always load modules, ignoring the cache file in the build directory.
     * @return
     */
    protected LoadModulesCommand buildLoadModulesCommand() {
        LoadModulesCommand command = new LoadModulesCommand();
        DefaultModulesLoader loader = new DefaultModulesLoader(appConfig.newXccAssetLoader());
        loader.setModulesManager(null);
        command.setModulesLoader(loader);
        return command;
    }

    protected void setConfigBaseDir(String path) {
        appConfig.getConfigDir().setBaseDir(new File("src/test/resources/" + path));
    }
}
