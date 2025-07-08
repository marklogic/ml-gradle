/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task

import com.marklogic.appdeployer.command.Command
import com.marklogic.appdeployer.command.CommandMapBuilder
import com.marklogic.appdeployer.impl.SimpleAppDeployer
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

import java.util.function.Supplier

class DeployToReplicaTask extends MarkLogicTask {

	@Input
	@Optional
	// Intended to allow for DHF, or even a user, to provide a custom list of commands.
	// A user may wish to do this so that e.g. security commands are not included, in case the Security database is
	// being replicated.
	Supplier<List<Command>> commandListSupplier

	@TaskAction
	void deployToReplica() {
		List<Command> commands = commandListSupplier != null ?
			commandListSupplier.get() :
			new CommandMapBuilder().getCommandsForReplicaCluster()

		newAppDeployer(commands).deploy(getAppConfig())
	}
}
