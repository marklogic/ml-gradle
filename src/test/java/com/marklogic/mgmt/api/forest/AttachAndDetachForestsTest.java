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
