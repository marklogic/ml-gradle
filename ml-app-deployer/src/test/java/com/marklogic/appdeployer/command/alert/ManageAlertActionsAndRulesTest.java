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
package com.marklogic.appdeployer.command.alert;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.alert.AlertActionManager;
import com.marklogic.mgmt.resource.alert.AlertConfigManager;
import com.marklogic.mgmt.resource.alert.AlertRuleManager;
import com.marklogic.rest.util.Fragment;
import com.marklogic.rest.util.ResourcesFragment;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class ManageAlertActionsAndRulesTest extends AbstractManageResourceTest {

    private final static String CONFIG_URI = "my-alert-config";

	@Override
	protected void undeployAndVerifyResourcesWereDeleted(ResourceManager mgr) {
		super.undeployAndVerifyResourcesWereDeleted(mgr);
	}

	@Override
    protected void initializeAndDeploy() {
        appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/alert-config"));

        initializeAppDeployer(new DeployOtherDatabasesCommand(1), new DeployAlertConfigsCommand(), newCommand(), new DeployAlertRulesCommand());
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
        assertTrue(mgr.getAsXml().getListItemIdRefs().isEmpty(), "All of the alert configs should have been deleted");
    }

    /**
     * Nothing to do here, since the alert stuff is deleted when the content database is deleted.
     */
    @Override
    protected void verifyResourcesWereDeleted(ResourceManager mgr) {

    }


}
