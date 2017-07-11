package com.marklogic.appdeployer.command.alert;

import java.io.File;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.databases.DeployTriggersDatabaseCommand;
import com.marklogic.mgmt.ResourceManager;
import com.marklogic.mgmt.alert.AlertActionManager;
import com.marklogic.mgmt.alert.AlertConfigManager;
import com.marklogic.mgmt.alert.AlertRuleManager;
import com.marklogic.rest.util.Fragment;
import com.marklogic.rest.util.ResourcesFragment;

public class ManageAlertActionsAndRulesTest extends AbstractManageResourceTest {

    private final static String CONFIG_URI = "my-alert-config";

	@Override
	protected void undeployAndVerifyResourcesWereDeleted(ResourceManager mgr) {
		super.undeployAndVerifyResourcesWereDeleted(mgr);
	}

	@Override
    protected void initializeAndDeploy() {
        appConfig.getConfigDir().setBaseDir(new File("src/test/resources/sample-app/alert-config"));

        initializeAppDeployer(new DeployContentDatabasesCommand(1), new DeployOtherDatabasesCommand(),
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
     * Verify that both our action and our rule are present.
     *
     * Also verify that the "other" database is populated with config/action/rule.
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

	    verifyOtherDatabaseHasAlertResources();
    }

    private void verifyOtherDatabaseHasAlertResources() {
	    final String dbName = "other-" + appConfig.getContentDatabaseName();
	    final String otherConfigUri = "other-alert-config";
	    final String actionName = "xdmp:log3";

	    AlertActionManager actionMgr = new AlertActionManager(manageClient, dbName, otherConfigUri);
	    ResourcesFragment rf = actionMgr.getAsXml();
	    assertEquals("/manage/v2/databases/other-sample-app-content/alert/actions/xdmp:log3?uri=other-alert-config",
		    rf.getElementValue("/node()/db:list-items[db:list-count = '1']/db:list-item/db:uriref"));
	    assertNotNull(actionMgr.getAsXml(actionName));
	    assertNotNull(actionMgr.getPropertiesAsXml(actionName));

	    AlertRuleManager ruleMgr = new AlertRuleManager(manageClient, dbName, otherConfigUri, actionName);
	    rf = ruleMgr.getAsXml();
	    assertEquals(
		    "/manage/v2/databases/other-sample-app-content/alert/actions/xdmp:log3/rules/other-rule?uri=other-alert-config",
		    rf.getElementValue("/node()/db:list-items[db:list-count = '1']/db:list-item/db:uriref"));

	    assertNotNull(ruleMgr.getAsXml("other-rule"));
	    Fragment f = ruleMgr.getPropertiesAsXml("other-rule");
	    assertEquals("other log to ErrorLog.txt", f.getElementValue("/arp:alert-rule-properties/arp:description"));
    }

    /**
     * The command doesn't yet support "undo", but we can use this method to test deleting all the alert stuff ourselves
     * from the content database, before the content database is deleted.
     */
    @Override
    protected void afterResourcesCreatedAgain() {
        AlertConfigManager mgr = new AlertConfigManager(manageClient, appConfig.getContentDatabaseName());
        mgr.deleteAllConfigs();
        assertTrue("All of the alert configs should have been deleted", mgr.getAsXml().getListItemIdRefs().isEmpty());
    }

    /**
     * Nothing to do here, since the alert stuff is deleted when the content database is deleted.
     */
    @Override
    protected void verifyResourcesWereDeleted(ResourceManager mgr) {

    }


}
