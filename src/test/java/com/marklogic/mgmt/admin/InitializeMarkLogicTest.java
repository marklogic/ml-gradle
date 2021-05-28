package com.marklogic.mgmt.admin;

import org.junit.jupiter.api.Test;

import com.marklogic.mgmt.AbstractMgmtTest;

public class InitializeMarkLogicTest extends AbstractMgmtTest {

    /**
     * The only way to really test this is to run it against a freshly installed MarkLogic, but in a test suite, we
     * always assume that we have a MarkLogic instance that has been initialized already. So this is just a smoke test
     * to ensure no errors are thrown from bad JSON.
     */
    @Test
    public void initAgainstAnAlreadyInitializedMarkLogic() {
        adminManager.init();
    }
}
