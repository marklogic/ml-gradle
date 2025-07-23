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
import org.junit.jupiter.api.Test;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExportDocumentsToSingleFileTest extends AbstractDataMovementTest {

    @Test
    public void test() throws Exception {
        File exportDir = new File("build/export-test/" + System.currentTimeMillis());
        File exportFile = new File(exportDir, "exportToFileTest.xml");
        new ExportToFileJob(exportFile).setWhereUris(FIRST_URI, SECOND_URI).run(client);

        String exportedXml = new String(FileCopyUtils.copyToByteArray(exportFile));
        logger.info("Exported XML: " + exportedXml);
        assertTrue(exportedXml.contains("<test>" + FIRST_URI + "</test>"));
        assertTrue(exportedXml.contains("<test>" + SECOND_URI + "</test>"));
    }

    @Test
    public void configureWithProperties() throws Exception {
        Properties props = new Properties();
        props.setProperty("exportPath", "build/export-test/" + System.currentTimeMillis() + "/file.xml");
        props.setProperty("fileHeader", "<results>");
        props.setProperty("fileFooter", "</results>");
        props.setProperty("recordPrefix", "<record>");
        props.setProperty("recordSuffix", "</record>");
        props.setProperty("batchSize", "52");
        props.setProperty("consistentSnapshot", "true");
        props.setProperty("jobName", "my-job");
        props.setProperty("logBatches", "true");
        props.setProperty("logBatchesWithLogger", "true");
        props.setProperty("threadCount", "7");
        props.setProperty("whereUris", FIRST_URI + "," + SECOND_URI);

        ExportToFileJob job = new ExportToFileJob();
        assertTrue(job.configureJob(props).isEmpty());
        job.run(client);

        String exportedXml = new String(FileCopyUtils.copyToByteArray(job.getExportFile()));
        logger.info("Exported XML: " + exportedXml);
        assertTrue(exportedXml.startsWith("<results>\n<record><test>"));
        assertTrue(exportedXml.endsWith("</test></record></results>"));
    }
}
