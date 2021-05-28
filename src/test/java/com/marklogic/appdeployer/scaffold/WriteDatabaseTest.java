package com.marklogic.appdeployer.scaffold;

import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.mgmt.api.database.Database;
import com.marklogic.mgmt.api.database.ElementIndex;
import com.marklogic.mgmt.template.database.DatabaseTemplateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WriteDatabaseTest extends AbstractResourceWriterTest {

	@Test
	public void defaultValues() {
		initializeAppDeployer(new DeployOtherDatabasesCommand(1));

		buildResourceAndDeploy(new DatabaseTemplateBuilder());

		Database db = api.db("CHANGEME-name-of-database");
		assertTrue(db.getEnabled());

		ElementIndex index = db.getRangeElementIndex().get(0);
		assertEquals("CHANGEME-name-of-element", index.getLocalname());
		assertEquals("CHANGEME-namespace-of-element", index.getNamespaceUri());
		assertEquals("http://marklogic.com/collation/", index.getCollation());
		assertFalse(index.getRangeValuePositions());
	}
}
