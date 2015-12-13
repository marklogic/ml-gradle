package com.rjrudin.marklogic.mgmt.api.forest;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Simple smoke test.
 */
public class GetForestPropertyNamesTest extends Assert {

    @Test
    public void forest() {
        List<String> list = new Forest().getPropertyNames();
        assertEquals("As of ML 8.0-4, expecting 14 forest property names", 14, list.size());
    }

    @Test
    public void forestBackup() {
        List<String> list = new ForestBackup().getPropertyNames();
        assertEquals("As of ML 8.0-4, expecting 10 forest property names", 10, list.size());

    }
}
