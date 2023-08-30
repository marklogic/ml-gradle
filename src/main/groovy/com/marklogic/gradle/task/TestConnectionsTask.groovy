package com.marklogic.gradle.task

import com.marklogic.appdeployer.command.TestConnectionsCommand
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

class TestConnectionsTask extends MarkLogicTask {

	@TaskAction
	void testConnections() {
		TestConnectionsCommand.TestResults results = new TestConnectionsCommand().testConnections(getCommandContext())
		if (results.anyTestFailed()) {
			throw new GradleException("One or more connections failed:\n\n" + results)
		} else {
			println("\nAll connections succeeded, results are shown below\n")
			println(results)
			println()
		}
	}
}
