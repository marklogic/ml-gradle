package com.marklogic.gradle.task

class UndeployAppTask extends AbstractConfirmableTask {

	@Override
	void executeIfConfirmed() {
		getAppDeployer().undeploy(getAppConfig())
	}

}
