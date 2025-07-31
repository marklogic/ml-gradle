/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.AbstractDataMovementTest;
import com.marklogic.client.ext.datamovement.consumer.WriteDocumentToFileConsumer;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExportDocumentsToFilesTest extends AbstractDataMovementTest {

    @Test
    public void test() {
        File exportDir = new File("build/export-test/" + System.currentTimeMillis());
        new SimpleExportJob(new WriteDocumentToFileConsumer(exportDir))
                .setWhereCollections(COLLECTION)
                .run(client);
        assertTrue(new File(exportDir, FIRST_URI).exists());
        assertTrue(new File(exportDir, SECOND_URI).exists());
    }
}
