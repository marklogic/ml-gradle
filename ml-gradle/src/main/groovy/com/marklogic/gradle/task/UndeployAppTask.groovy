/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task

class UndeployAppTask extends AbstractConfirmableTask {

	@Override
	void executeIfConfirmed() {
		getAppDeployer().undeploy(getAppConfig())
	}

}
