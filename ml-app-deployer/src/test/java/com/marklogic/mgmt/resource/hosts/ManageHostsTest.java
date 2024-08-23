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
package com.marklogic.mgmt.resource.hosts;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.marklogic.mgmt.AbstractMgmtTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ManageHostsTest extends AbstractMgmtTest {

    @Test
    public void getHostNamesAndIds() {
        HostManager mgr = new HostManager(manageClient);
        List<String> names = mgr.getHostNames();
        List<String> ids = mgr.getHostIds();

        assertFalse(names.isEmpty(), "The list of names should not be empty");
        assertFalse(ids.isEmpty(), "The list of ids should not be empty");
        assertEquals(names.size(), ids.size(), "The lists of names and ids should have the same number of items");
    }
}
