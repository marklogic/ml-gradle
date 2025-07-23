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
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExportBatchesToZipsTest extends AbstractDataMovementTest {

    private File exportDir;
    private ExportBatchesToZipsJob job;
    private Properties props;

    @BeforeEach
    public void moreSetup() {
        writeDocuments("/test/three.xml", "/test/four.xml");

        final String exportPath = "build/export-test/" + System.currentTimeMillis();
        props = new Properties();
        props.setProperty("exportPath", exportPath);
        props.setProperty("whereCollections", COLLECTION);
        props.setProperty("batchSize", "2");
        exportDir = new File(exportPath);
        job = new ExportBatchesToZipsJob();
    }

    @Test
    public void defaultSettings() {
        assertTrue(job.configureJob(props).isEmpty());
        job.run(client);

        assertZipFileContainsEntryNames(new File(exportDir, "batch-1.zip"), FIRST_URI, SECOND_URI);
        assertZipFileContainsEntryNames(new File(exportDir, "batch-2.zip"), "/test/three.xml", "/test/four.xml");
    }

    @Test
    public void flattenUriAndAddPrefix() {
        props.setProperty("flattenUri", "true");
        props.setProperty("uriPrefix", "/example/");
        props.setProperty("filenamePrefix", "my-batch-");
        props.setProperty("filenameExtension", ".jar");
        assertTrue(job.configureJob(props).isEmpty());
        job.run(client);

        assertZipFileContainsEntryNames(new File(exportDir, "my-batch-1.jar"),
                "/example/dmsdk-test-1.xml", "/example/dmsdk-test-2.xml");
        assertZipFileContainsEntryNames(new File(exportDir, "my-batch-2.jar"),
                "/example/three.xml", "/example/four.xml");
    }
}
