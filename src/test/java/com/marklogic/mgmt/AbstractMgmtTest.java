package com.marklogic.mgmt;

import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.admin.AdminConfig;
import com.marklogic.mgmt.admin.AdminManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Base class for tests that just talk to the Mgmt API and don't depend on an AppDeployer instance.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
public abstract class AbstractMgmtTest extends LoggingObject {

	@Autowired
	protected ManageConfig manageConfig;

	@Autowired
	protected AdminConfig adminConfig;

	// Intended to be used by subclasses
	protected ManageClient manageClient;
	protected AdminManager adminManager;

	@BeforeEach
	public void initializeManageClient() {
		manageClient = new ManageClient(manageConfig);
		adminManager = new AdminManager(adminConfig);
	}
}
