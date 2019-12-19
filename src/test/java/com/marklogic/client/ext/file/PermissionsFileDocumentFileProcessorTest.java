package com.marklogic.client.ext.file;

import com.marklogic.client.ext.tokenreplacer.DefaultTokenReplacer;
import com.marklogic.client.io.DocumentMetadataHandle;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Properties;

public class PermissionsFileDocumentFileProcessorTest extends Assert {

	private PermissionsFileDocumentFileProcessor processor = new PermissionsFileDocumentFileProcessor();

	/**
	 * *=manage-user,read
	 * test.json=manage-user,update,manage-admin,update
	 * test.xml=qconsole-user,update
	 */
	@Test
	public void wildcard() {
		File testDir = new File("src/test/resources/process-files/wildcard-test");

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
	public void replaceTokens() {
		File testDir = new File("src/test/resources/process-files/token-test");

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
