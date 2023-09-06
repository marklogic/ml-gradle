/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.appdeployer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.modules.DefaultModulesLoaderFactory;
import com.marklogic.appdeployer.command.modules.LoadModulesCommand;
import com.marklogic.appdeployer.command.security.GenerateTemporaryCertificateCommand;
import com.marklogic.appdeployer.impl.SimpleAppDeployer;
import com.marklogic.client.ext.modulesloader.impl.DefaultModulesLoader;
import com.marklogic.mgmt.AbstractMgmtTest;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.xcc.template.XccTemplate;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;

/**
 * Base class for tests that depend on an AppDeployer instance. You can extend this directly to write a test for a
 * particular resource, but check out AbstractManageResourceTest (and its subclasses) to see if that will work for you
 * instead, as that saves a lot of work.
 */
public abstract class AbstractAppDeployerTest extends AbstractMgmtTest {

    public final static String SAMPLE_APP_NAME = "sample-app";

    protected final static Integer SAMPLE_APP_REST_PORT = 8004;
    protected final static Integer SAMPLE_APP_TEST_REST_PORT = 8005;

    // Intended to be used by subclasses
    protected AppDeployer appDeployer;
    protected AppConfig appConfig;

    @BeforeEach
    public void initialize() {
        initializeAppConfig();
    }

    protected void initializeAppConfig() {
    	initializeAppConfig(new File("src/test/resources/sample-app"));
    }

    protected void initializeAppConfig(File projectDir) {
	    appConfig = new AppConfig(projectDir);
		appConfig.setHost(this.manageConfig.getHost());
	    appConfig.setName(SAMPLE_APP_NAME);
	    appConfig.setRestPort(SAMPLE_APP_REST_PORT);

	    // Assume that the manager user can also be used as the REST admin user
	    appConfig.setRestAdminUsername(manageConfig.getUsername());
	    appConfig.setRestAdminPassword(manageConfig.getPassword());
		appConfig.setAppServicesUsername(manageConfig.getUsername());
		appConfig.setAppServicesPassword(manageConfig.getPassword());
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
    	if (appDeployer != null) {
		    try {
			    appDeployer.undeploy(appConfig);
		    } catch (Exception e) {
			    throw new RuntimeException("Unexpected error while undeploying sample app: " + e.getMessage(), e);
		    }
	    }
    }

    protected XccTemplate newModulesXccTemplate() {
    	return new XccTemplate(appConfig.getHost(), appConfig.getAppServicesPort(), appConfig.getRestAdminUsername(),
		    appConfig.getRestAdminPassword(), appConfig.getModulesDatabaseName());
    }

    /**
     * This command is configured to always load modules, ignoring the cache file in the build directory.
     * @return
     */
    protected LoadModulesCommand buildLoadModulesCommand() {
        LoadModulesCommand command = new LoadModulesCommand();
        appConfig.setModuleTimestampsPath(null);
        DefaultModulesLoader loader = (DefaultModulesLoader)(new DefaultModulesLoaderFactory().newModulesLoader(appConfig));
        loader.setModulesManager(null);
        command.setModulesLoader(loader);
        return command;
    }

    protected void setConfigBaseDir(String path) {
        appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/" + path));
    }

	/**
	 * Intended to simplify testing app servers that require SSL.
	 */
	protected final void configureRestServersToRequireSSL() {
		GenerateTemporaryCertificateCommand gtcc = new GenerateTemporaryCertificateCommand();
		gtcc.setTemplateIdOrName("sample-app-template");
		gtcc.execute(new CommandContext(appConfig, manageClient, adminManager));

		ObjectNode payload = new ObjectMapper().createObjectNode()
			.put("server-name", SAMPLE_APP_NAME)
			.put("group-name", "Default")
			.put("ssl-certificate-template", "sample-app-template");

		ServerManager mgr = new ServerManager(manageClient);
		mgr.save(payload.toString());
		payload.put("server-name", SAMPLE_APP_NAME + "-test");
		mgr.save(payload.toString());
	}

	protected final void configureRestServersToNotRequireSSL() {
		ObjectNode payload = new ObjectMapper().createObjectNode()
			.put("server-name", SAMPLE_APP_NAME)
			.put("group-name", "Default")
			.put("ssl-certificate-template", "");

		ServerManager mgr = new ServerManager(manageClient);
		mgr.save(payload.toString());
		payload.put("server-name", SAMPLE_APP_NAME + "-test");
		mgr.save(payload.toString());
	}

	protected final CommandContext newCommandContext() {
		return new CommandContext(appConfig, manageClient, adminManager);
	}
}
