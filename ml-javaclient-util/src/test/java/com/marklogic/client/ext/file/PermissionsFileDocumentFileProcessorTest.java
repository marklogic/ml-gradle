/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
		assertEquals(3, permissions.size(), "test.json should have the *, test.json, and *.json rules applied to it");
		assertTrue(permissions.get("manage-user").contains(DocumentMetadataHandle.Capability.READ));
		assertTrue(permissions.get("manage-user").contains(DocumentMetadataHandle.Capability.UPDATE));
		assertTrue(permissions.get("manage-admin").contains(DocumentMetadataHandle.Capability.UPDATE));
		assertTrue(permissions.get("qconsole-user").contains(DocumentMetadataHandle.Capability.READ));

		file = new DocumentFile("/test-1.json", new File(testDir, "test-1.json"));
		processor.processDocumentFile(file);
		permissions = file.getDocumentMetadata().getPermissions();
		assertEquals(2, permissions.size(), "test-1.json should have the * and *.json rules applied to it");
		assertTrue(permissions.get("manage-user").contains(DocumentMetadataHandle.Capability.READ));
		assertFalse(permissions.get("manage-user").contains(DocumentMetadataHandle.Capability.UPDATE));
		assertNull(permissions.get("manage-admin"));
		assertTrue(permissions.get("qconsole-user").contains(DocumentMetadataHandle.Capability.READ));

		file = new DocumentFile("/test.xml", new File(testDir, "test.xml"));
		processor.processDocumentFile(file);
		permissions = file.getDocumentMetadata().getPermissions();
		assertEquals(2, permissions.size(), "test.xml should have the * and test.xml rules applied to it");
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
