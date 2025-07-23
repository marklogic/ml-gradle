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
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExportBatchesToDirectoryTest extends AbstractDataMovementTest {

    private File exportDir;
    private ExportBatchesToDirectoryJob job;

    @BeforeEach
    public void moreSetup() {
        writeDocuments("/test/three.xml", "/test/four.xml");

        exportDir = new File("build/export-test/" + System.currentTimeMillis());
        job = new ExportBatchesToDirectoryJob(exportDir);
        job.setWhereCollections(COLLECTION);
        job.setBatchSize(2);
    }

    @Test
    public void test() throws Exception {
        Properties props = new Properties();
        props.setProperty("fileHeader", "<results>");
        props.setProperty("fileFooter", "</results>");
        props.setProperty("recordPrefix", "<wrapper>");
        props.setProperty("recordSuffix", "</wrapper>");
        props.setProperty("consistentSnapshot", "true");
        job.configureJob(props);

        job.run(client);

        String text = new String(FileCopyUtils.copyToByteArray(new File(exportDir, "batch-1.xml")));
        String otherText = new String(FileCopyUtils.copyToByteArray(new File(exportDir, "batch-2.xml")));
        logger.info(text);
        logger.info(otherText);
        if (text.contains(FIRST_URI)) {
            verifyFileWithDocumentsOneAndTwo(text);
            verifyFileWithDocumentsThreeAndFour(otherText);
        } else {
            verifyFileWithDocumentsOneAndTwo(otherText);
            verifyFileWithDocumentsThreeAndFour(text);
        }
    }

    @Test
    public void customFileExtension() {
        job.getExportListener()
                .withFilenameExtension(".txt")
                .withFilenamePrefix("my-batch-");
        job.run(client);

        assertTrue(new File(exportDir, "my-batch-1.txt").exists());
        assertTrue(new File(exportDir, "my-batch-2.txt").exists());
        assertFalse(new File(exportDir, "batch-1.xml").exists());
        assertFalse(new File(exportDir, "batch-2.xml").exists());
    }

    private void verifyFileWithDocumentsOneAndTwo(String text) {
        assertTrue(text.contains("<wrapper><test>" + FIRST_URI + "</test></wrapper>"));
        assertTrue(text.contains("<wrapper><test>" + SECOND_URI + "</test></wrapper>"));
        assertTrue(text.startsWith("<results><wrapper>"));
        assertTrue(text.endsWith("</wrapper></results>"));
    }

    private void verifyFileWithDocumentsThreeAndFour(String text) {
        assertTrue(text.contains("<wrapper><test>/test/three.xml</test></wrapper>"));
        assertTrue(text.contains("<wrapper><test>/test/four.xml</test></wrapper>"));
        assertTrue(text.startsWith("<results><wrapper>"));
        assertTrue(text.endsWith("</wrapper></results>"));
    }
}
