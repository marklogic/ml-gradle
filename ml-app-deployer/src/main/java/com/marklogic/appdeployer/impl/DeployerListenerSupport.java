/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.impl;

import com.marklogic.appdeployer.command.Command;
import com.marklogic.client.ext.helper.LoggingObject;

import java.util.List;

/**
 * Listeners should extend this so that new methods can be added to DeployerListener without breaking implementors.
 */
public class DeployerListenerSupport extends LoggingObject implements DeployerListener {

	@Override
	public void beforeCommandsExecuted(DeploymentContext context) {
	}

	@Override
	public void beforeCommandExecuted(Command command, DeploymentContext context, List<Command> remainingCommands) {
	}

	@Override
	public void afterCommandExecuted(Command command, DeploymentContext context, List<Command> remainingCommands) {
	}
}
