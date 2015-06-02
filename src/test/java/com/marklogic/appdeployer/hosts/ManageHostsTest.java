package com.marklogic.appdeployer.hosts;

import java.util.List;

import org.junit.Test;

import com.marklogic.appdeployer.AbstractMgmtTest;
import com.marklogic.rest.mgmt.hosts.HostManager;

public class ManageHostsTest extends AbstractMgmtTest {

    @Test
    public void test() {
        HostManager mgr = new HostManager(manageClient);
        List<String> names = mgr.getHostNames();
        List<String> ids = mgr.getHostIds();

        assertFalse("The list of names should not be empty", names.isEmpty());
        assertFalse("The list of ids should not be empty", ids.isEmpty());
        assertEquals("The lists of names and ids should have the same number of items", names.size(), ids.size());
    }
}
