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
