package com.rjrudin.marklogic.appdeployer.command.alert;

import java.io.File;

import com.rjrudin.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.rjrudin.marklogic.appdeployer.command.Command;
import com.rjrudin.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
import com.rjrudin.marklogic.appdeployer.command.databases.DeployTriggersDatabaseCommand;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.alert.AlertActionManager;
import com.rjrudin.marklogic.mgmt.alert.AlertRuleManager;
import com.rjrudin.marklogic.rest.util.Fragment;
import com.rjrudin.marklogic.rest.util.ResourcesFragment;

public class ManageAlertActionsAndRulesTest extends AbstractManageResourceTest {

    private final static String CONFIG_URI = "my-alert-config";

    @Override
    protected void initializeAndDeploy() {
        appConfig.getConfigDir().setBaseDir(new File("src/test/resources/sample-app/alert-config"));

        initializeAppDeployer(new DeployTriggersDatabaseCommand(), new DeployContentDatabasesCommand(1),
                new DeployAlertConfigsCommand(), newCommand(), new DeployAlertRulesCommand());
        appDeployer.deploy(appConfig);
    }

    @Override
    protected ResourceManager newResourceManager() {
        return new AlertActionManager(manageClient, appConfig.getContentDatabaseName(), CONFIG_URI);
    }

    @Override
    protected Command newCommand() {
        return new DeployAlertActionsCommand();
    }

    @Override
    protected String[] getResourceNames() {
        return new String[] { "xdmp:log2" };
    }

    /**
     * No need to do anything here, as "undo" isn't yet supported for alert stuff (for now, they're deleted when the
     * content database is deleted).
     */
    @Override
    protected void verifyResourcesWereDeleted(ResourceManager mgr) {
    }

    /**
     * Verify that both our action and our rule are present.
     */
    @Override
    protected void afterResourcesCreated() {
        final String dbName = appConfig.getContentDatabaseName();
        final String actionName = "xdmp:log2";

        AlertActionManager actionMgr = new AlertActionManager(manageClient, dbName, CONFIG_URI);
        ResourcesFragment rf = actionMgr.getAsXml();
        assertEquals("/manage/v2/databases/sample-app-content/alert/actions/xdmp:log2?uri=my-alert-config",
                rf.getElementValue("/node()/db:list-items[db:list-count = '1']/db:list-item/db:uriref"));
        assertNotNull(actionMgr.getAsXml(actionName));
        assertNotNull(actionMgr.getPropertiesAsXml(actionName));

        AlertRuleManager ruleMgr = new AlertRuleManager(manageClient, dbName, CONFIG_URI, actionName);
        rf = ruleMgr.getAsXml();
        assertEquals(
                "/manage/v2/databases/sample-app-content/alert/actions/xdmp:log2/rules/my-rule?uri=my-alert-config",
                rf.getElementValue("/node()/db:list-items[db:list-count = '1']/db:list-item/db:uriref"));

        assertNotNull(ruleMgr.getAsXml("my-rule"));
        Fragment f = ruleMgr.getPropertiesAsXml("my-rule");
        assertEquals("log to ErrorLog.txt", f.getElementValue("/arp:alert-rule-properties/arp:description"));
    }
}
