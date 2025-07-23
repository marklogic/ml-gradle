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
package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.datamovement.DeleteListener;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.ext.batch.RestBatchWriter;
import com.marklogic.client.ext.datamovement.AbstractDataMovementTest;
import com.marklogic.client.ext.datamovement.QueryBatcherTemplate;
import com.marklogic.client.ext.helper.ClientHelper;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ManagePermissionsTest extends AbstractDataMovementTest {

    /**
     * Note that with ML9 and previous versions, rest-reader/read and rest-writer/update are always added to documents
     * inserted via /v1/documents.
     */
    @Test
    public void test() {
        final String uri = "/test/manage-permissions-test.xml";

        QueryBatcherTemplate qbt = new QueryBatcherTemplate(newClient("Documents"));

        // Clear out the test documents
        qbt.applyOnDocumentUris(new DeleteListener(), uri);

        // Insert documents
        RestBatchWriter writer = new RestBatchWriter(client, false);
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        metadata.getPermissions().add("app-user", DocumentMetadataHandle.Capability.READ, DocumentMetadataHandle.Capability.UPDATE);
        writer.write(Arrays.asList(
                new DocumentWriteOperationImpl(DocumentWriteOperation.OperationType.DOCUMENT_WRITE, uri, metadata, new StringHandle("<one/>").withFormat(Format.XML))
        ));
        writer.waitForCompletion();

        ClientHelper helper = new ClientHelper(client);
        DocumentMetadataHandle.DocumentPermissions perms = helper.getMetadata(uri).getPermissions();
        assertEquals(2, perms.get("app-user").size());
        assertTrue(perms.get("app-user").contains(DocumentMetadataHandle.Capability.READ));
        assertTrue(perms.get("app-user").contains(DocumentMetadataHandle.Capability.UPDATE));

        // Set permissions
        new SetPermissionsJob("alert-user", "read", "alert-user", "update").setWhereUris(uri).run(client);
        perms = helper.getMetadata(uri).getPermissions();
        assertEquals(1, perms.size());
        assertEquals(2, perms.get("alert-user").size());
        assertTrue(perms.get("alert-user").contains(DocumentMetadataHandle.Capability.READ));
        assertTrue(perms.get("alert-user").contains(DocumentMetadataHandle.Capability.UPDATE));

        // Add permissions
        new AddPermissionsJob("app-user", "read", "app-user", "update").setWhereUris(uri).run(client);
        perms = helper.getMetadata(uri).getPermissions();
        assertEquals(2, perms.size());
        assertEquals(2, perms.get("alert-user").size());
        assertTrue(perms.get("alert-user").contains(DocumentMetadataHandle.Capability.READ));
        assertTrue(perms.get("alert-user").contains(DocumentMetadataHandle.Capability.UPDATE));
        assertEquals(2, perms.get("app-user").size());
        assertTrue(perms.get("app-user").contains(DocumentMetadataHandle.Capability.READ));
        assertTrue(perms.get("app-user").contains(DocumentMetadataHandle.Capability.UPDATE));

        // Remove permissions
        new RemovePermissionsJob("app-user", "read", "app-user", "update").setWhereUris(uri).run(client);
        perms = helper.getMetadata(uri).getPermissions();
        assertEquals(1, perms.size());
        assertEquals(2, perms.get("alert-user").size());
        assertTrue(perms.get("alert-user").contains(DocumentMetadataHandle.Capability.READ));
        assertTrue(perms.get("alert-user").contains(DocumentMetadataHandle.Capability.UPDATE));
    }
}
