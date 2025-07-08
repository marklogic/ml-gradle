/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
