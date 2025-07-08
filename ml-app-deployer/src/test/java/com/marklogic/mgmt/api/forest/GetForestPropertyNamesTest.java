/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.forest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Simple smoke test.
 */
public class GetForestPropertyNamesTest  {

    @Test
    public void forest() {
        List<String> list = new Forest().getPropertyNames();
        assertEquals(16, list.size(), "As of ML 9.0-3, expecting 16 forest property names");
    }

    @Test
    public void forestBackup() {
        List<String> list = new ForestBackup().getPropertyNames();
        assertEquals(10, list.size(), "As of ML 8.0-4, expecting 10 forest property names");

    }
}
