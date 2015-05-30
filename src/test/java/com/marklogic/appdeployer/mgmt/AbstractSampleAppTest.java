package com.marklogic.appdeployer.mgmt;

import org.junit.After;
import org.junit.Before;

/**
 * Extend this class for when you want the sample app automatically created and deleted.
 */
public abstract class AbstractSampleAppTest extends AbstractMgmtTest {

    @Before
    public void setupSampleApp() {
        createSampleApp();
    }

    @After
    public void teardownSampleApp() {
        configMgr.setAdminConfig(new AdminConfig());
        //deleteSampleApp();
    }
}
