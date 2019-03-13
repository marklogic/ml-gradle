package com.marklogic.appdeployer.impl;

/**
 * Provides an extension point for introducing behavior right before commands are executed by a subclass of
 * AbstractAppDeployer.
 */
public interface DeployerListener {

	void beforeCommandsExecuted(DeploymentContext context);

}
