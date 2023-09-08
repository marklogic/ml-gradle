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
package com.marklogic.client.ext.file;

import com.marklogic.client.ext.tokenreplacer.DefaultTokenReplacer;
import com.marklogic.client.io.DocumentMetadataHandle;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class PermissionsFileDocumentFileProcessorTest {

	private PermissionsFileDocumentFileProcessor processor = new PermissionsFileDocumentFileProcessor();

	/**
	 * *=manage-user,read
	 * test.json=manage-user,update,manage-admin,update
	 * test.xml=qconsole-user,update
	 */
	@Test
	public void wildcard() throws IOException {
		File testDir = new File("src/test/resources/process-files/wildcard-test");

		File collectionsPropertiesFile = new File(testDir, processor.getPropertiesFilename());
		processor.loadProperties(collectionsPropertiesFile);

		DocumentFile file = new DocumentFile("/test.json", new File(testDir, "test.json"));
		processor.processDocumentFile(file);
		DocumentMetadataHandle.DocumentPermissions permissions = file.getDocumentMetadata().getPermissions();
		assertTrue(permissions.get("manage-user").contains(DocumentMetadataHandle.Capability.READ));
		assertTrue(permissions.get("manage-user").contains(DocumentMetadataHandle.Capability.UPDATE));
		assertTrue(permissions.get("manage-admin").contains(DocumentMetadataHandle.Capability.UPDATE));
		assertNull(permissions.get("qconsole-user"));

		file = new DocumentFile("/test.xml", new File(testDir, "test.xml"));
		processor.processDocumentFile(file);
		permissions = file.getDocumentMetadata().getPermissions();
		assertTrue(permissions.get("manage-user").contains(DocumentMetadataHandle.Capability.READ));
		assertFalse(permissions.get("manage-user").contains(DocumentMetadataHandle.Capability.UPDATE));
		assertNull(permissions.get("manage-admin"));
		assertTrue(permissions.get("qconsole-user").contains(DocumentMetadataHandle.Capability.UPDATE));
	}

	@Test
	public void replaceTokens() throws IOException {
		File testDir = new File("src/test/resources/process-files/token-test");

		File collectionsPropertiesFile = new File(testDir, processor.getPropertiesFilename());
		processor.loadProperties(collectionsPropertiesFile);

		DefaultTokenReplacer tokenReplacer = new DefaultTokenReplacer();
		Properties props = new Properties();
		props.setProperty("%%roleName%%", "rest-admin");
		tokenReplacer.setProperties(props);
		processor.setTokenReplacer(tokenReplacer);

		DocumentFile file = new DocumentFile("/test.json", new File(testDir, "test.json"));
		processor.processDocumentFile(file);
		DocumentMetadataHandle.DocumentPermissions permissions = file.getDocumentMetadata().getPermissions();
		assertTrue(permissions.get("rest-admin").contains(DocumentMetadataHandle.Capability.UPDATE));
	}
}
