package com.rjrudin.marklogic.appdeployer.command.databases;

import org.junit.Test;

import com.rjrudin.marklogic.appdeployer.AbstractAppDeployerTest;
import com.rjrudin.marklogic.appdeployer.command.forests.CreateContentForestsCommand;

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
        // initializeAppDeployer(new CreateContentDatabasesCommand(), new CreateSchemasDatabaseCommand(),
        // new CreateTriggersDatabaseCommand(), new CreateContentForestsCommand());

        initializeAppDeployer(new CreateContentDatabasesCommand(), new CreateContentForestsCommand());

        try {
            appDeployer.deploy(appConfig);
        } finally {
            //undeploySampleApp();
        }
    }
}
