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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

public class ExportToZipTest extends AbstractDataMovementTest {

    private File exportFile;
    private ExportToZipJob exportToZipJob;

    @BeforeEach
    public void setupConsumer() {
        exportFile = new File("build/export-test/" + System.currentTimeMillis() + "-write-to-zip-file-test.zip");
        exportToZipJob = new ExportToZipJob(exportFile);
        exportToZipJob.setWhereCollections(COLLECTION);
    }

    @Test
    public void test() {
        exportToZip();
        assertZipFileContainsEntryNames(exportFile, FIRST_URI, SECOND_URI);
    }

    @Test
    public void flattenUri() {
        exportToZipJob.getWriteToZipConsumer().setFlattenUri(true);
        exportToZip();
        assertZipFileContainsEntryNames(exportFile, "dmsdk-test-1.xml", "dmsdk-test-2.xml");
    }

    @Test
    public void uriPrefix() {
        final String prefix = "/example";
        exportToZipJob.getWriteToZipConsumer().setUriPrefix(prefix);
        exportToZip();
        assertZipFileContainsEntryNames(exportFile, prefix + FIRST_URI, prefix + SECOND_URI);
    }

    private void exportToZip() {
        exportToZipJob.run(client);
    }

}
