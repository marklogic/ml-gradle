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
package com.marklogic.client.ext.xcc;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.xcc.Content;
import com.marklogic.xcc.ContentCapability;
import com.marklogic.xcc.ContentCreateOptions;
import com.marklogic.xcc.ContentPermission;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileCopyUtils;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultDocumentWriteOperationAdapterTest {

	private DefaultDocumentWriteOperationAdapter sut = new DefaultDocumentWriteOperationAdapter();

	@Test
	public void test() throws Exception {
		String xml = "<hello>World</hello>";
		String uri = "/test.xml";
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		metadata.withCollections("collection1", "collection2");
		metadata.withPermission("role1", DocumentMetadataHandle.Capability.EXECUTE, DocumentMetadataHandle.Capability.READ);
		metadata.withPermission("role2", DocumentMetadataHandle.Capability.UPDATE);
		metadata.withPermission("role3", DocumentMetadataHandle.Capability.INSERT);
		metadata.setFormat(Format.XML);

		DocumentWriteOperation operation = new DocumentWriteOperationImpl(
			DocumentWriteOperation.OperationType.DOCUMENT_WRITE, uri, metadata, new StringHandle(xml));

		Content content = sut.adapt(operation);

		assertEquals(uri, content.getUri());
		String xccXml = new String(FileCopyUtils.copyToByteArray(content.openDataStream()));
		assertEquals(xml, xccXml);

		ContentCreateOptions options = content.getCreateOptions();

		String[] collections = options.getCollections();
		assertEquals("collection1", collections[0]);
		assertEquals("collection2", collections[1]);

		// Permissions are in a set, and thus not ordered, so our assertions a little funky here
		ContentPermission[] permissions = options.getPermissions();
		assertEquals(4, permissions.length);
		for (ContentPermission perm : permissions) {
			String role = perm.getRole();
			ContentCapability capability = perm.getCapability();
			if ("role1".equals(role)) {
				assertTrue(ContentCapability.EXECUTE.equals(capability) || ContentCapability.READ.equals(capability));
			} else if ("role2".equals(role)) {
				assertEquals(ContentCapability.UPDATE, capability);
			} else if ("role3".equals(role)) {
				assertEquals(ContentCapability.INSERT, capability);
			} else {
				fail("Unexpected role: " + role);
			}
		}
	}
}
