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
package com.marklogic.appdeployer.command.flexrep;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.cpf.DeployCpfConfigsCommand;
import com.marklogic.appdeployer.command.cpf.DeployDomainsCommand;
import com.marklogic.appdeployer.command.cpf.DeployPipelinesCommand;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.mgmt.resource.cpf.CpfConfigManager;
import com.marklogic.mgmt.resource.cpf.DomainManager;
import com.marklogic.mgmt.resource.cpf.PipelineManager;
import com.marklogic.mgmt.resource.flexrep.ConfigManager;
import com.marklogic.mgmt.resource.flexrep.PullManager;
import com.marklogic.mgmt.resource.flexrep.TargetManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class DeployFlexrepTest extends AbstractAppDeployerTest {

    @AfterEach
    public void tearDown() {
        undeploySampleApp();
    }

    @Test
    public void configureMaster() {
        appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/flexrep-config"));

        initializeAppDeployer(
                new DeployCpfConfigsCommand(), new DeployDomainsCommand(), new DeployPipelinesCommand(),
                new DeployConfigsCommand(), new DeployTargetsCommand(), new DeployOtherDatabasesCommand(1));

        appDeployer.deploy(appConfig);
        assertConfigAndTargetAreDeployed();

        // Run deploy again to make sure nothing blows up
        appDeployer.deploy(appConfig);
        assertConfigAndTargetAreDeployed();

        ConfigManager mgr = new ConfigManager(manageClient, appConfig.getContentDatabaseName());
        mgr.deleteAllConfigs();
        assertTrue(mgr.getAsXml().getListItemIdRefs().isEmpty(),
			"All of the configs should have been deleted, including their targets");
    }

    @Test
    public void noFlexrepDir() {
        appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/empty-ml-config"));

        initializeAppDeployer(new DeployTargetsCommand());

        // Just making sure we don't get a null-pointer due to the flexrep directory not existing
        appDeployer.deploy(appConfig);
    }

    @Test
    public void masterFlexrep() {
        appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/flexrep-combined"));

        appConfig.setFlexrepPath("master");
        initializeAppDeployer(new DeployOtherDatabasesCommand(1), new DeployFlexrepCommand());

        appDeployer.deploy(appConfig);

        final String domainName = "master-domain";
        final String db = appConfig.getContentDatabaseName();
        final String triggersDb = appConfig.getTriggersDatabaseName();
        assertTrue(new ServerManager(manageClient).exists("master-flexrep-server"));
        assertTrue(new DomainManager(manageClient, triggersDb).exists(domainName));
        assertTrue(new PipelineManager(manageClient, triggersDb).exists("Flexible Replication"));
        assertTrue(new PipelineManager(manageClient, triggersDb).exists("Status Change Handling"));
        assertTrue(new CpfConfigManager(manageClient, triggersDb).exists(domainName));
        assertTrue(new ConfigManager(manageClient, db).exists(domainName));
        assertTrue(new TargetManager(manageClient, db, domainName).exists("master-domain-target"));

        undeploySampleApp();
        assertFalse(new ServerManager(manageClient).exists("master-flexrep-server"));
    }

    @Test
    public void replicaFlexrep() {
        appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/flexrep-combined"));

        appConfig.setFlexrepPath("replica");
        initializeAppDeployer(new DeployOtherDatabasesCommand(1), new DeployFlexrepCommand());

        appDeployer.deploy(appConfig);

        final String domainName = "replica-domain";
        final String db = appConfig.getContentDatabaseName();
        final String triggersDb = appConfig.getTriggersDatabaseName();
        assertTrue(new ServerManager(manageClient).exists("replica-flexrep-server"));
        assertTrue(new DomainManager(manageClient, triggersDb).exists(domainName));
        assertTrue(new PipelineManager(manageClient, triggersDb).exists("Flexible Replication"));
        assertTrue(new PipelineManager(manageClient, triggersDb).exists("Status Change Handling"));
        assertTrue(new CpfConfigManager(manageClient, triggersDb).exists(domainName));
        assertTrue(new ConfigManager(manageClient, db).exists(domainName));
        assertTrue(new TargetManager(manageClient, db, domainName).exists("replica-domain-target"));

        undeploySampleApp();
        assertFalse(new ServerManager(manageClient).exists("master-flexrep-server"));
    }

	@Test
	public void deployPullConfiguration() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/flexrep-config"));

		initializeAppDeployer(
			new DeployCpfConfigsCommand(), new DeployDomainsCommand(), new DeployPipelinesCommand(),
			new DeployConfigsCommand(), new DeployTargetsCommand(), new DeployOtherDatabasesCommand(1), new DeployPullsCommand());

		try {
			appDeployer.deploy(appConfig);

			assertConfigAndTargetAreDeployed();

			final String pullName = "docs2go";
			PullManager pullManager = new PullManager(manageClient, "other-sample-app-content");
			assertTrue(pullManager.exists(pullName));

			// Run deploy again to make sure nothing blows up
			appDeployer.deploy(appConfig);

			ConfigManager mgr = new ConfigManager(manageClient, appConfig.getContentDatabaseName());
			mgr.deleteAllConfigs();
			assertTrue(mgr.getAsXml().getListItemIdRefs().isEmpty(),
				"All of the configs should have been deleted, including their targets");

		} finally {
			undeploySampleApp();
			assertFalse(new ServerManager(manageClient).exists("master-flexrep-server"));
		}
	}

    private void assertConfigAndTargetAreDeployed() {
        final String domainName = "sample-app-domain-1";
        ConfigManager configMgr = new ConfigManager(manageClient, appConfig.getContentDatabaseName());
        configMgr.exists(domainName);

        final String targetName = "sample-app-domain-1-target";
        TargetManager targetMgr = new TargetManager(manageClient, appConfig.getContentDatabaseName(), domainName);
        assertTrue(targetMgr.exists(targetName));

        final String enabledXpath = "/node()/node()[local-name(.) = 'enabled']";
        targetMgr.disableTarget(targetName);
        assertEquals("false", targetMgr.getPropertiesAsXml(targetName).getElementValue(enabledXpath));

        targetMgr.enableTarget(targetName);
        assertEquals("true", targetMgr.getPropertiesAsXml(targetName).getElementValue(enabledXpath));

        targetMgr.disableAllTargets();
        assertEquals("false", targetMgr.getPropertiesAsXml(targetName).getElementValue(enabledXpath));

        targetMgr.enableAllTargets();
        assertEquals("true", targetMgr.getPropertiesAsXml(targetName).getElementValue(enabledXpath));

        configMgr.disableAllFlexrepTargets();
        assertEquals("false", targetMgr.getPropertiesAsXml(targetName).getElementValue(enabledXpath));

        configMgr.enableAllFlexrepTargets();
        assertEquals("true", targetMgr.getPropertiesAsXml(targetName).getElementValue(enabledXpath));
    }
}
