package com.marklogic.appdeployer.command.flexrep;

import java.io.File;

import com.marklogic.mgmt.appservers.ServerManager;
import com.marklogic.mgmt.cpf.CpfConfigManager;
import com.marklogic.mgmt.cpf.DomainManager;
import com.marklogic.mgmt.cpf.PipelineManager;
import org.junit.After;
import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.cpf.DeployCpfConfigsCommand;
import com.marklogic.appdeployer.command.cpf.DeployDomainsCommand;
import com.marklogic.appdeployer.command.cpf.DeployPipelinesCommand;
import com.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
import com.marklogic.appdeployer.command.databases.DeployTriggersDatabaseCommand;
import com.marklogic.mgmt.flexrep.ConfigManager;
import com.marklogic.mgmt.flexrep.TargetManager;

public class DeployFlexrepTest extends AbstractAppDeployerTest {

    @After
    public void tearDown() {
        undeploySampleApp();
    }

    @Test
    public void configureMaster() {
        appConfig.getConfigDir().setBaseDir(new File("src/test/resources/sample-app/flexrep-config"));

        initializeAppDeployer(new DeployContentDatabasesCommand(1), new DeployTriggersDatabaseCommand(),
                new DeployCpfConfigsCommand(), new DeployDomainsCommand(), new DeployPipelinesCommand(),
                new DeployConfigsCommand(), new DeployTargetsCommand());

        appDeployer.deploy(appConfig);
        assertConfigAndTargetAreDeployed();

        // Run deploy again to make sure nothing blows up
        appDeployer.deploy(appConfig);
        assertConfigAndTargetAreDeployed();

        ConfigManager mgr = new ConfigManager(manageClient, appConfig.getContentDatabaseName());
        mgr.deleteAllConfigs();
        assertTrue("All of the configs should have been deleted, including their targets", mgr.getAsXml()
                .getListItemIdRefs().isEmpty());
    }

    @Test
    public void noFlexrepDir() {
        appConfig.getConfigDir().setBaseDir(new File("src/test/resources/sample-app/empty-ml-config"));

        initializeAppDeployer(new DeployTargetsCommand());

        // Just making sure we don't get a null-pointer due to the flexrep directory not existing
        appDeployer.deploy(appConfig);
    }

    @Test
    public void masterFlexrep() {
        appConfig.getConfigDir().setBaseDir(new File("src/test/resources/sample-app/flexrep-combined"));

        initializeAppDeployer(new DeployContentDatabasesCommand(1), new DeployTriggersDatabaseCommand(), new DeployFlexrepCommand("master"));

        appDeployer.deploy(appConfig);

        final String domainName = "master-domain";
        final String db = appConfig.getContentDatabaseName();
        assertTrue(new ServerManager(manageClient).exists("master-flexrep-server"));
        assertTrue(new DomainManager(manageClient).exists(appConfig.getTriggersDatabaseName(), domainName));
        assertTrue(new PipelineManager(manageClient).exists(appConfig.getTriggersDatabaseName(), "Flexible Replication"));
        assertTrue(new PipelineManager(manageClient).exists(appConfig.getTriggersDatabaseName(), "Status Change Handling"));
        assertTrue(new CpfConfigManager(manageClient).exists(appConfig.getTriggersDatabaseName(), domainName));
        assertTrue(new ConfigManager(manageClient, db).exists(domainName));
        assertTrue(new TargetManager(manageClient, db, domainName).exists("master-domain-target"));

        undeploySampleApp();
        assertFalse(new ServerManager(manageClient).exists("master-flexrep-server"));
    }

    @Test
    public void replicaFlexrep() {
        appConfig.getConfigDir().setBaseDir(new File("src/test/resources/sample-app/flexrep-combined"));

        initializeAppDeployer(new DeployContentDatabasesCommand(1), new DeployTriggersDatabaseCommand(), new DeployFlexrepCommand("replica"));

        appDeployer.deploy(appConfig);

        final String domainName = "replica-domain";
        final String db = appConfig.getContentDatabaseName();
        assertTrue(new ServerManager(manageClient).exists("replica-flexrep-server"));
        assertTrue(new DomainManager(manageClient).exists(appConfig.getTriggersDatabaseName(), domainName));
        assertTrue(new PipelineManager(manageClient).exists(appConfig.getTriggersDatabaseName(), "Flexible Replication"));
        assertTrue(new PipelineManager(manageClient).exists(appConfig.getTriggersDatabaseName(), "Status Change Handling"));
        assertTrue(new CpfConfigManager(manageClient).exists(appConfig.getTriggersDatabaseName(), domainName));
        assertTrue(new ConfigManager(manageClient, db).exists(domainName));
        assertTrue(new TargetManager(manageClient, db, domainName).exists("replica-domain-target"));

        undeploySampleApp();
        assertFalse(new ServerManager(manageClient).exists("master-flexrep-server"));
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
