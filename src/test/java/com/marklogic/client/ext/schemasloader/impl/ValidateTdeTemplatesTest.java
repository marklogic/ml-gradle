package com.marklogic.client.ext.schemasloader.impl;

import com.marklogic.client.ext.file.DocumentFile;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;

/**
 * This test assumes that the default Documents database has the default Schemas database as its schemas database.
 * That allows the underlying invokeFunction to work.
 */
public class ValidateTdeTemplatesTest extends AbstractSchemasTest {

	private DefaultSchemasLoader loader;

	@Before
	public void setup() {
		super.setup();
		// Assumes that Documents points to Schemas as its schemas database
		loader = new DefaultSchemasLoader(client, "Documents");
	}

	@Test
	public void badJsonFile() {
		try {
			loader.loadSchemas(Paths.get("src", "test", "resources", "bad-schemas", "bad-json").toString());
			fail("The bad-template.json file should have failed processing because it has a duplicate column in it");
		} catch (RuntimeException ex) {
			String message = ex.getCause().getMessage();
			assertTrue(message.startsWith("TDE template failed validation"));
			assertTrue(message.contains("TDE-REPEATEDCOLUMN"));
		}
	}

	@Test
	public void goodTemplateThatsThenUpdatedViaJavascript() {
		List<DocumentFile> files = loader.loadSchemas(Paths.get("src", "test", "resources", "good-schemas", "originals").toString());
		assertEquals(2, files.size());

		files = loader.loadSchemas(Paths.get("src", "test", "resources", "good-schemas", "updated").toString());
		assertEquals("Verifying that the updated schemas were still loaded correctly, which depends on telling the " +
			"TDE validation function to exclude the schema that's currently loaded; otherwise a TDE-INCONSISTENTVIEW " +
			"error will be thrown", 2, files.size());
	}


	@Test
	public void badXmlFile() {
		try {
			loader.loadSchemas(Paths.get("src", "test", "resources", "bad-schemas", "bad-xml").toString());
			fail("The bad-template.xml file should have failed processing because it has a duplicate column in it");
		} catch (RuntimeException ex) {
			String message = ex.getCause().getMessage();
			assertTrue(message.startsWith("TDE template failed validation"));
			assertTrue(message.contains("TDE-REPEATEDCOLUMN"));
		}
	}

	@Test
	public void badJsonFileInNonTdeDirectory() {
		List<DocumentFile> files = loader.loadSchemas(Paths.get("src", "test", "resources", "bad-schemas", "otherpath").toString());
		assertEquals("The file in the otherpath directory is a TDE template, but it's not under /tde/, so it's not " +
			"validated as a TDE template", 1, files.size());
	}

	@Test
	public void validationDisabled() {
		loader = new DefaultSchemasLoader(client, null);
		List<DocumentFile> files = loader.loadSchemas(Paths.get("src", "test", "resources", "bad-schemas", "bad-json").toString());
		assertEquals("TDE validation is disabled, so the bad TDE template should have been loaded", 1, files.size());
	}
}
