/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.datamovement.DeleteListener;
import com.marklogic.client.ext.datamovement.AbstractDataMovementTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteCollectionsTest extends AbstractDataMovementTest {

    @Test
    public void test() {
        Properties props = new Properties();
        props.setProperty("collections", COLLECTION);

        DeleteCollectionsJob job = new DeleteCollectionsJob();
        List<String> messages = job.configureJob(props);
        assertTrue(messages.isEmpty(), "This job doesn't require where* properties to be set");

        assertNotNull(client.newDocumentManager().exists(FIRST_URI));
        assertNotNull(client.newDocumentManager().exists(SECOND_URI));

        job.run(client);

        assertNull(client.newDocumentManager().exists(FIRST_URI));
        assertNull(client.newDocumentManager().exists(SECOND_URI));
    }

    @Test
    public void collectionsSetViaConstructor() {
        DeleteCollectionsJob job = new DeleteCollectionsJob(COLLECTION);

        List<String> messages = job.configureJob(new Properties());
        assertTrue(messages.isEmpty(), "This job doesn't require where* properties to be set, and 'collections' shouldn't be required " +
                "since the collections were specified via the constructor");

        assertNotNull(client.newDocumentManager().exists(FIRST_URI));
        assertNotNull(client.newDocumentManager().exists(SECOND_URI));

        job.run(client);

        assertNull(client.newDocumentManager().exists(FIRST_URI));
        assertNull(client.newDocumentManager().exists(SECOND_URI));
    }

    @Test
    public void usingSimpleQueryBatcherJob() {
        SimpleQueryBatcherJob job = new SimpleQueryBatcherJob(new DeleteListener());
        job.setWhereCollections(COLLECTION);

        assertNotNull(client.newDocumentManager().exists(FIRST_URI));
        assertNotNull(client.newDocumentManager().exists(SECOND_URI));

        job.run(client);

        assertNull(client.newDocumentManager().exists(FIRST_URI));
        assertNull(client.newDocumentManager().exists(SECOND_URI));
    }

    /**
     * Any job that extends BatcherConfig can be used here for this test, which is just to verify that the jobId and
     * jobName properties are applied correctly.
     */
    @Test
    public void configureJobIdAndName() {
        Properties props = new Properties();
        props.setProperty("jobId", "my-job-id");
        props.setProperty("jobName", "My Job");

        DeleteCollectionsJob job = new DeleteCollectionsJob();
        job.configureJob(props);
        assertEquals("my-job-id", job.getJobId());
        assertEquals("My Job", job.getJobName());
    }
}
