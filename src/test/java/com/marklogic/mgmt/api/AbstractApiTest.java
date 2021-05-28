package com.marklogic.mgmt.api;

import org.junit.jupiter.api.BeforeEach;

import com.marklogic.mgmt.AbstractMgmtTest;

public abstract class AbstractApiTest extends AbstractMgmtTest {

    protected API api;

    @BeforeEach
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
