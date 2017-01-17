package com.marklogic.gradle.task.cluster

import com.marklogic.appdeployer.command.clusters.ModifyLocalClusterCommand
import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class ModifyClusterTask extends MarkLogicTask {

	@TaskAction
	void modifyCluster() {
		invokeDeployerCommandWithClassName(ModifyLocalClusterCommand.class.getSimpleName())
	}
}
