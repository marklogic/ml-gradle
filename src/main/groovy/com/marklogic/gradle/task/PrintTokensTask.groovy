package com.marklogic.gradle.task

import org.gradle.api.tasks.TaskAction

class PrintTokensTask extends MarkLogicTask {

	@TaskAction
	void printTokens() {
		Map<String, String> tokens = getAppConfig().getCustomTokens()
		if (tokens == null || tokens.isEmpty()) {
			println "No tokens have been defined; you can set mlPropsAsTokens=true to include all Gradle properties as tokens"
		} else {
			println "Printing " + tokens.size() + " tokens"
			for (String token : tokens) {
				println token + ":" + tokens.get(token)
			}
			println "Finished printing " + tokens.size() + " tokens"
		}
	}
}
