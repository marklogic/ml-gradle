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
package com.marklogic.mgmt.api.group;

import org.junit.jupiter.api.Test;

import com.marklogic.mgmt.api.AbstractApiTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TraceTest extends AbstractApiTest {

    /**
     * Tests configuring trace events on the default group via trace/untrace.
     */
    @Test
    public void traceAndUntrace() {
        final String event = "api-test";

        Group g = api.group();
        assertTrue(g.getEvent() == null || !g.getEvent().contains(event));

        api.trace(event);

        g = api.group();
        assertTrue(g.getEvent().contains(event));

        api.untrace(event);

        g = api.group();
        assertTrue(g.getEvent() == null || !g.getEvent().contains(event));
    }
}
