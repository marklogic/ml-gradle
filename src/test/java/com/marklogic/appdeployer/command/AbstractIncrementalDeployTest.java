package com.marklogic.appdeployer.command;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.ManageClient;
import org.junit.Before;

public abstract class AbstractIncrementalDeployTest extends AbstractAppDeployerTest {

	protected ManageClient originalManageClient;

	@Before
	public void setupIncrementalDeployTest() {
		appConfig.setIncrementalDeploy(true);
		this.originalManageClient = this.manageClient;
		deleteResourceTimestampsFile();
	}
	
	public void deleteResourceTimestampsFile() {
		new ResourceFileManagerImpl().deletePropertiesFile();
	}
}
