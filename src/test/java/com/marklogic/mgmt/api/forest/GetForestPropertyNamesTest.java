package com.marklogic.mgmt.api.forest;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Simple smoke test.
 */
public class GetForestPropertyNamesTest extends Assert {

    @Test
    public void forest() {
        List<String> list = new Forest().getPropertyNames();
        assertEquals("As of ML 9.0-3, expecting 16 forest property names", 16, list.size());
    }

    @Test
    public void forestBackup() {
        List<String> list = new ForestBackup().getPropertyNames();
        assertEquals("As of ML 8.0-4, expecting 10 forest property names", 10, list.size());

    }
}
