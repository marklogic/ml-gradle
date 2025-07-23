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
