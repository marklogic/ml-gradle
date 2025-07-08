/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.forest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.marklogic.mgmt.api.AbstractApiTest;
import com.marklogic.mgmt.api.database.Database;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AttachAndDetachForestsTest extends AbstractApiTest {

    private Database db;
    private Forest f1, f2;

    private final static String DB_NAME = "api-db";
    private final static String FOREST1_NAME = "api-forest-1";
    private final static String FOREST2_NAME = "api-forest-2";

    @AfterEach
    public void teardown() {
        deleteIfExists(db, f1, f2);
    }

    @Test
    public void test() {
        // Create a database
        db = api.db(DB_NAME);
        assertFalse(db.exists());
        db.save();
        assertTrue(db.exists());

        // Attach a forest using one-liner
        f1 = db.attachNewForest(FOREST1_NAME);
        assertTrue(f1.exists());
        db = api.db(DB_NAME);
        assertTrue(db.getForest().contains(FOREST1_NAME));

        // Attach a forest using multiple steps
        f2 = api.forest("api-forest-2");
        assertFalse(f2.exists());
        f2.save();
        assertTrue(f2.exists());
        db.attach(f2);
        db = api.db(DB_NAME);
        assertTrue(db.getForest().contains(FOREST2_NAME));

        // Detach the 2nd forest
        db.detach(f2);
        assertTrue(f2.exists());
        db = api.db(DB_NAME);
        assertTrue(db.getForest().contains(FOREST1_NAME));
        assertFalse(db.getForest().contains(FOREST2_NAME));

        // Delete the database
        db.delete();
        assertFalse(db.exists());
        assertFalse(f1.exists());
        assertTrue(f2.exists());

        // Delete the detached forest
        f2.delete();
        assertFalse(f2.exists());
    }
}
