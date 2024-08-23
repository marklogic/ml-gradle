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
package com.marklogic.client.ext.schemasloader.impl;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ext.file.DocumentFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test assumes that the default Documents database has the default Schemas database as its schemas database.
 * That allows the underlying invokeFunction to work.
 */
public class ValidateTdeTemplatesTest extends AbstractSchemasTest {

	private DefaultSchemasLoader loader;

	@BeforeEach
	public void setup() {
		super.setup();
		loader = new DefaultSchemasLoader(client, newContentClient());
	}

	@Test
	public void badJsonFile() {
		final String path = Paths.get("src", "test", "resources", "bad-schemas", "bad-json").toString();
		if (TdeUtil.templateBatchInsertSupported(client)) {
			RuntimeException ex = assertThrows(RuntimeException.class, () -> loader.loadSchemas(path));
			assertTrue(ex.getMessage().contains("the following script can be run in Query Console against your content " +
				"database to see the TDE validation error"), "Unexpected message: " + ex.getMessage());
			FailedRequestException fre = (FailedRequestException)ex.getCause();
			assertTrue(fre.getMessage().contains("failed to apply resource at eval: Internal Server Error"),
				"Unfortunately, the FailedRequestException does not capture why the tde.templateBatchInsert failed; " +
					"JAVA-224 has been opened to improve this; unexpected message: " + fre.getMessage());
		} else {
			try {
				loader.loadSchemas(path);
				fail("The bad-template.json file should have failed processing because it has a duplicate column in it");
			} catch (RuntimeException ex) {
				String message = ex.getCause().getMessage();
				assertTrue(message.startsWith("TDE template failed validation"));
				assertTrue(message.contains("TDE-REPEATEDCOLUMN"));
			}
		}
	}

	@Test
	public void goodTemplateThatsThenUpdatedViaJavascript() {
		List<DocumentFile> files = loader.loadSchemas(Paths.get("src", "test", "resources", "good-schemas", "originals").toString());
		assertEquals(2, files.size());

		files = loader.loadSchemas(Paths.get("src", "test", "resources", "good-schemas", "updated").toString());
		assertEquals(2, files.size(), "Verifying that the updated schemas were still loaded correctly, which depends on telling the " +
			"TDE validation function to exclude the schema that's currently loaded; otherwise a TDE-INCONSISTENTVIEW " +
			"error will be thrown");
	}


	@Test
	public void badXmlFile() {
		final String path = Paths.get("src", "test", "resources", "bad-schemas", "bad-xml").toString();
		if (TdeUtil.templateBatchInsertSupported(client)) {
			RuntimeException ex = assertThrows(RuntimeException.class, () -> loader.loadSchemas(path));
			assertTrue(ex.getMessage().contains("the following script can be run in Query Console against your content " +
				"database to see the TDE validation error"), "Unexpected message: " + ex.getMessage());
			FailedRequestException fre = (FailedRequestException)ex.getCause();
			assertTrue(fre.getServerMessage().contains("TDE-REPEATEDCOLUMN"), "As of Java Client 6.1.0, errors from " +
				"the /v1/eval endpoint are expected to be processed correctly by the Java Client so that the user sees " +
				"the actual error; actual error message: " + fre.getServerMessage());
		} else {
			try {
				loader.loadSchemas(path);
				fail("The bad-template.xml file should have failed processing because it has a duplicate column in it");
			} catch (RuntimeException ex) {
				String message = ex.getCause().getMessage();
				assertTrue(message.startsWith("TDE template failed validation"));
				assertTrue(message.contains("TDE-REPEATEDCOLUMN"));
			}
		}
	}

	@Test
	public void badJsonFileInNonTdeDirectory() {
		final String path = Paths.get("src", "test", "resources", "bad-schemas", "otherpath").toString();
		List<DocumentFile> files = loader.loadSchemas(path);
		assertEquals(1, files.size(), "The file in the otherpath directory is a TDE template, but it's not under /tde/, so it's not " +
			"validated as a TDE template");
	}

	@Test
	public void validationDisabled() {
		loader = new DefaultSchemasLoader(client, null);
		List<DocumentFile> files = loader.loadSchemas(Paths.get("src", "test", "resources", "bad-schemas", "bad-json").toString());
		assertEquals(1, files.size(), "TDE validation is disabled, so the bad TDE template should have been loaded");
	}

	@Test
	public void mixedContent() {
		List<DocumentFile> files = loader.loadSchemas(Paths.get("src", "test", "resources", "good-schemas", "xml-schemas").toString());
		assertEquals(1, files.size(), "Verifying that the file still loads correctly even with processing instructions and comments in it");
	}

	@Test
	public void schemasThatArentTdeTemplates() {
		List<DocumentFile> files = loader.loadSchemas(Paths.get("src", "test", "resources", "schemas", "not-tde").toString());
		assertEquals(1, files.size());
		assertEquals("ruleset.txt", files.get(0).getFile().getName(), "The loader should recognize" +
			"that the file is not a TDE, and thus it should not be loaded via templateBatchInsert");
	}
}
