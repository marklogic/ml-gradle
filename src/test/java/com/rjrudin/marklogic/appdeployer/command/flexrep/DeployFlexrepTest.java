package com.rjrudin.marklogic.appdeployer.command.flexrep;

import java.io.File;

import org.junit.After;
import org.junit.Test;

import com.rjrudin.marklogic.appdeployer.AbstractAppDeployerTest;
import com.rjrudin.marklogic.appdeployer.command.cpf.DeployCpfConfigsCommand;
import com.rjrudin.marklogic.appdeployer.command.cpf.DeployDomainsCommand;
import com.rjrudin.marklogic.appdeployer.command.cpf.DeployPipelinesCommand;
import com.rjrudin.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
import com.rjrudin.marklogic.appdeployer.command.databases.DeployTriggersDatabaseCommand;
import com.rjrudin.marklogic.mgmt.flexrep.ConfigManager;
import com.rjrudin.marklogic.mgmt.flexrep.TargetManager;

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
    }
}
