package com.rjrudin.marklogic.appdeployer.command.databases;

import org.junit.Test;

import com.rjrudin.marklogic.appdeployer.AbstractAppDeployerTest;
import com.rjrudin.marklogic.appdeployer.command.forests.CreateContentForestsCommand;
import com.rjrudin.marklogic.appdeployer.command.restapis.CreateRestApiServersCommand;
import com.rjrudin.marklogic.mgmt.databases.DatabaseManager;
import com.rjrudin.marklogic.mgmt.forests.ForestManager;
import com.rjrudin.marklogic.rest.util.Fragment;

/**
 * So we could process content-database.json first (and any other JSON files that are merged together). We use that to
 * create the content database before creating the REST API instance. A database can specify its forests, so we should
 * create the forests first (which require IDs in the database file).
 * 
 * I think an AppConfig property of # of forests per database name would be the easiest way to configure. The forest
 * command would then create that number of forests. The database command would then either make N attach calls or
 * modify the database payload to specify all the forests (that would be a pain though because it needs forest IDs).
 * 
 * The hosts for a forest is tricky too - we'd need that in the config file so we can do a replacement on ML_HOST. But
 * we'd need to iterate over each host name instead of using ML_HOST, really. That means we'd need tokens specific to
 * the command, not to the whole deployment process.
 * 
 * Actually, we can create the database first, and then specify the "database" param for each forest - then we don't
 * need an "attach" step.
 */
public class CreateDatabaseWithCustomForestsTest extends AbstractAppDeployerTest {

    @Test
    public void test() {
        // We want both a main and a test app server in this test
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
