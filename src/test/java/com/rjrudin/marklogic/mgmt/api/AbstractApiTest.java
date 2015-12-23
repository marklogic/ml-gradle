package com.rjrudin.marklogic.mgmt.api;

import org.junit.Before;

import com.rjrudin.marklogic.mgmt.AbstractMgmtTest;

public abstract class AbstractApiTest extends AbstractMgmtTest {

    protected API api;

    @Before
    public void setup() {
        api = new API(manageClient);
    }

    protected void deleteIfExists(Resource... resources) {
        for (Resource r : resources) {
            if (r != null && r.exists()) {
                r.delete();
            }
        }
    }
}
