package com.rjrudin.marklogic.appdeployer.command.databases;

import org.junit.Test;

import com.rjrudin.marklogic.appdeployer.AbstractAppDeployerTest;
import com.rjrudin.marklogic.appdeployer.command.forests.CreateContentForestsCommand;
import com.rjrudin.marklogic.appdeployer.command.restapis.CreateRestApiServersCommand;
import com.rjrudin.marklogic.mgmt.databases.DatabaseManager;
import com.rjrudin.marklogic.mgmt.forests.ForestManager;
import com.rjrudin.marklogic.rest.util.Fragment;

/**
 * The REST API command can be used to create a server with a content database, but that doesn't give any control over
 * the details of the forests. CreateContentForestsCommand can be used for that kind of control.
 */
public class CreateDatabaseWithCustomForestsTest extends AbstractAppDeployerTest {

    @Test
    public void test() {
        // We want both main and test databases
        appConfig.setTestRestPort(SAMPLE_APP_TEST_REST_PORT);

        CreateContentForestsCommand command = new CreateContentForestsCommand();
        command.setForestsPerHost(2);
        initializeAppDeployer(new CreateContentDatabasesCommand(), command, new CreateRestApiServersCommand(),
                new CreateSchemasDatabaseCommand(), new CreateTriggersDatabaseCommand());

        ForestManager forestMgr = new ForestManager(manageClient);
        DatabaseManager dbMgr = new DatabaseManager(manageClient);

        try {
            appDeployer.deploy(appConfig);

            Fragment mainDb = dbMgr.getAsXml(appConfig.getContentDatabaseName());
            Fragment testDb = dbMgr.getAsXml(appConfig.getTestContentDatabaseName());

            // Assert that the content forests and test content forests were all created
            for (int i = 1; i <= 2; i++) {
                String mainForestName = appConfig.getContentDatabaseName() + "-" + i;
                assertTrue(forestMgr.exists(mainForestName));
                assertTrue(mainDb.elementExists(format("//db:relation[db:nameref = '%s']", mainForestName)));

                String testForestName = appConfig.getTestContentDatabaseName() + "-" + i;
                assertTrue(forestMgr.exists(testForestName));
                assertTrue(testDb.elementExists(format("//db:relation[db:nameref = '%s']", testForestName)));
            }

        } finally {
            undeploySampleApp();

            for (int i = 1; i <= 2; i++) {
                assertFalse(forestMgr.exists(appConfig.getContentDatabaseName() + "-1"));
                assertFalse(forestMgr.exists(appConfig.getTestContentDatabaseName() + "-1"));
            }
        }
    }
}
