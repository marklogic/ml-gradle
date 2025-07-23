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

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.ext.datamovement.AbstractDataMovementTest;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExportJsonToJsonFileTest extends AbstractDataMovementTest {

    private ExportToFileJob job;
    private File exportFile;

    @BeforeEach
    public void setup() {
        File exportDir = new File("build/export-test/" + System.currentTimeMillis());
        exportFile = new File(exportDir, "exportToFileTest.xml");
        job = new ExportToFileJob(exportFile);
        job.setWhereUris("test1.json", "test2.json");
    }

    @Test
    public void omitLastRecordSuffix() {
        job.setFileHeader("[");
        job.setFileFooter("]");
        job.setRecordSuffix(",");
        job.setOmitLastRecordSuffix(true);

        String exportedJson = runJobAndGetJson();
        assertTrue(exportedJson.contains("{\"uri\":\"test1.json\"},{\"uri\":\"test2.json\"}]"));
    }

    @Test
    public void dontOmitLastRecordSuffix() {
        job.setFileHeader("[");
        job.setFileFooter("]");
        job.setRecordSuffix(",");
        job.setOmitLastRecordSuffix(false);

        String exportedJson = runJobAndGetJson();
        assertTrue(exportedJson.contains("{\"uri\":\"test1.json\"},{\"uri\":\"test2.json\"},]"));
    }

    @Test
    public void useDefaultConstructorAndOmitLastRecordSuffix() {
        job = new ExportToFileJob();
        job.setWhereUris("test1.json", "test2.json");
        job.setFileHeader("[");
        job.setFileFooter("]");
        job.setRecordSuffix(",");
        job.setOmitLastRecordSuffix(true);
        job.setExportFile(exportFile);

        String exportedJson = runJobAndGetJson();
        assertTrue(exportedJson.contains("{\"uri\":\"test1.json\"},{\"uri\":\"test2.json\"}]"));
    }

    @Test
    public void omitLastRecordSuffixWithNoFileFooter() {
        job.setRecordSuffix(",");
        job.setOmitLastRecordSuffix(true);

        String exportedJson = runJobAndGetJson();
        assertTrue(
                exportedJson.contains("{\"uri\":\"test1.json\"},{\"uri\":\"test2.json\"} "),
                "Since no file footer was set, whitespace should be used to overwrite the last record suffix"
        );
    }

    @Override
    protected void writeDocuments(String... uris) {
        List<DocumentWriteOperation> list = new ArrayList<>();
        uris = new String[]{"test1.json", "test2.json"};
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        metadata.getCollections().add(COLLECTION);
        for (String uri : uris) {
            list.add(new DocumentWriteOperationImpl(DocumentWriteOperation.OperationType.DOCUMENT_WRITE, uri, metadata,
                    new StringHandle("{\"uri\":\"" + uri + "\"}").withFormat(Format.XML)));
        }
        writeDocuments(list);
    }

    protected String runJobAndGetJson() {
        job.run(client);
        String exportedJson;
        try {
            exportedJson = new String(FileCopyUtils.copyToByteArray(exportFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.info("Exported JSON: " + exportedJson);
        return exportedJson;
    }
}
