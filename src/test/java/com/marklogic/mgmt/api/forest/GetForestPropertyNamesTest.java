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
