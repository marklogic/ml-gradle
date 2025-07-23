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

