package com.marklogic.appdeployer.command.schemas;

import org.junit.After;
import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
import com.marklogic.appdeployer.command.databases.DeploySchemasDatabaseCommand;
import com.marklogic.appdeployer.command.databases.DeployTriggersDatabaseCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;

public class LoadSchemasTest extends AbstractAppDeployerTest {

    @Test
    public void testSchemaLoading() {
        initializeAppDeployer(new DeploySchemasDatabaseCommand(), new DeployTriggersDatabaseCommand(),
                new DeployContentDatabasesCommand(1), new DeployRestApiServersCommand(), newCommand());
        appDeployer.deploy(appConfig);

        DatabaseClient client = appConfig.newSchemasDatabaseClient();

        GenericDocumentManager docMgr = client.newDocumentManager();

        assertNull("Rules document loaded", docMgr.exists("notExists"));
        assertNotNull("Rules document loaded", docMgr.exists("/my.rules").getUri());
        assertNotNull("XSD document loaded", docMgr.exists("/x.xsd").getUri());
    }

    @Test
    public void testSchemaCustomSchemasPath() {
        initializeAppDeployer(new DeploySchemasDatabaseCommand(), new DeployTriggersDatabaseCommand(),
                new DeployContentDatabasesCommand(1), new DeployRestApiServersCommand(), newCommand());
        appConfig.setSchemasPath("src/test/resources/schemas-marklogic9");
        appDeployer.deploy(appConfig);

        DatabaseClient client = appConfig.newSchemasDatabaseClient();

        GenericDocumentManager docMgr = client.newDocumentManager();

        assertNotNull("TDEXML document loaded", docMgr.exists("/x.tdex").getUri());
        assertNotNull("TDEJSON document loaded", docMgr.exists("/x.tdej").getUri());

        for (String uri : new String[] { "/x.tdex", "/x.tdej" }) {
            DocumentMetadataHandle h = docMgr.readMetadata(uri, new DocumentMetadataHandle());
            assertEquals("Files ending in tdex and tdej go into a special collection", "http://marklogic.com/xdmp/tde",
                    h.getCollections().iterator().next());
        }
    }

    @After
    public void cleanup() {
        undeploySampleApp();
    }

    private Command newCommand() {
        return new LoadSchemasCommand();
    }

}
