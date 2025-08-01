/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UrisQueryQueryBatcherBuilderTest {

    private UrisQueryQueryBatcherBuilder builder = new UrisQueryQueryBatcherBuilder();

    @Test
    public void javascriptStartingWithFnSubsequence() {
        String query = "fn.subsequence(cts.uris(null, null, cts.collectionQuery('Testing')), 1, 3)";
        String newQuery = builder.wrapJavascriptIfAppropriate(query);
        assertEquals(query, newQuery,
                "The query should not be wrapped with cts.uris since it doesn't start with cts.");
    }

    @Test
    public void javascriptStartingWithCtsUris() {
        String query = "cts.uris(null, null, cts.collectionQuery('Testing'))";
        String newQuery = builder.wrapJavascriptIfAppropriate(query);
        assertEquals(query, newQuery,
                "The query should not be wrapped with cts.uris since it already starts with cts.uris");
    }

    @Test
    public void javascriptStartingWithCtsCollectionQuery() {
        String query = "cts.collectionQuery('Testing')";
        String newQuery = builder.wrapJavascriptIfAppropriate(query);
        assertEquals("cts.uris(\"\", null, cts.collectionQuery('Testing'))", newQuery,
                "The query should not be wrapped with cts.uris since it doesn't start with cts.uris");
    }
}
