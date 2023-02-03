/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.gradle.task.roxy

import org.gradle.api.tasks.TaskAction

/**
 * Does some hacky string parsing to try to convert custom build steps in the Roxy app_specific.rb file into custom
 * Gradle tasks.
 */
class RoxyMigrateBuildStepsTask extends RoxyTask {

	@TaskAction
	void copyBuildSteps() {
		if (getRoxyProjectPath()) {
			def appSpecificPath = project.hasProperty("appSpecificPath") ? project.property("appSpecificPath") : "deploy/app_specific.rb"
			def filePath = getRoxyProjectPath() + "/" + appSpecificPath
			if (new File(filePath).exists()) {
				copyBuildStepsToGradleFile(filePath)
			} else {
				println "Not copying build steps, did not find Roxy app-specific build file at: " + filePath
			}
		} else {
			printMissingPathMessage()
		}
	}

	void copyBuildStepsToGradleFile(filePath) {
		println "Copying build steps from: " + filePath

		List<BuildStep> buildSteps = extractBuildSteps(filePath)
		int count = buildSteps.size()

		if (count == 0) {
			println "No custom build steps were found in: " + filePath
		}
		else {
			File file = new File("build.gradle");
			println "Backing up build.gradle to backup-build.gradle"
			new File("backup-build.gradle").write(file.text)

			String newText = file.text + "\n"
			for (BuildStep step : buildSteps) {
				if (!step.bodyLines.isEmpty()) {
					println "Copying Roxy buld step: " + step.name
					newText += "\n" + step.toGradleTask()
				} else {
					println "Did not copy Roxy build step, could not extract method body: " + step.name
				}
			}

			String message = "Adding " + count;
			if (count > 1) {
				message += " tasks"
			} else {
				message += " task"
			}
			println message + " to build.gradle"
			file.write(newText)
		}
	}

	List<BuildStep> extractBuildSteps(String filePath) {
		String text = new File(filePath).text

		String[] lines = text.split("\\n");
		List<BuildStep> buildSteps = new ArrayList<>();

		BuildStep currentBuildStep = null;

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.trim().startsWith("#")) {
				// Comment line, ignore
				continue;
			}
			else if (line.trim().startsWith("def ") && line.contains("()")) {
				// We're starting a new build step
				currentBuildStep = new BuildStep();
				buildSteps.add(currentBuildStep);
				currentBuildStep.name = extractBuildStepName(line);
			}
			else if (currentBuildStep != null) {
				if (line.startsWith("@logger")) {
					String message = extractLogMessage(line);
					if (message != null) {
						currentBuildStep.logMessages.add(message);
					}
				} else if (line.contains("execute_query")) {
					i++;
					for (; i < lines.length; i++) {
						line = lines[i];
						if (line.trim().equals("},") || line.trim().equals("}")) {
							break;
						} else {
							currentBuildStep.bodyLines.add(line);
						}
					}
				}
			}
		}

		return buildSteps
	}

	/**
	 * Assumes e.g. "def my_method-name()".
	 *
	 * @param line
	 * @return
	 */
	String extractBuildStepName(String line) {
		int pos = line.indexOf("def ");
		int end = line.indexOf("(");
		return line.substring(pos + 4, end);
	}

	/**
	 * We ignore the log message if it appears to do string concatenation, as odds are we can't resolve the
	 * variables.
	 *
	 * Assumes e.g. @logger.info "Hello world"
	 *
	 * @param line
	 * @return
	 */
	String extractLogMessage(String line) {
		if (line.contains("+")) {
			return null;
		}
		int start = line.indexOf("\"");
		if (start > -1) {
			int end = start + 1 + line.substring(start + 1).indexOf("\"");
			if (end > start && end < line.length()) {
				return line.substring(start + 1, end);
			}
		}
		return null;
	}

	/**
	 * Used for backing up and then overwriting the Gradle build file.
	 *
	 * @param filename
	 * @param text
	 */
	void writeFile(String filename, String text) {
		File file = new File(filename);
		if (file.exists()) {
			new File("backup-" + filename).write(file.text)
		}
		println "Writing: " + filename
		file.write(text)
	}
}

class BuildStep {

	String name;
	List<String> bodyLines = new ArrayList<>();
	List<String> logMessages = new ArrayList<>();

	String toGradleTask() {
		StringBuilder sb = new StringBuilder("task " + name + "(type: com.marklogic.gradle.task.ServerEvalTask, group: 'Converted Roxy build step') {\n");
		if (!logMessages.isEmpty()) {
			sb.append("\tdoFirst {\n");
			for (String message : logMessages) {
				sb.append("\t\tprintln \"" + message + "\"");
			}
			sb.append("\t}\n");
		}
		sb.append("\txquery = ");
		int lineCount = bodyLines.size();
		for (int i = 0; i < lineCount; i++) {
			String line = bodyLines.get(i).replaceAll("'", "\"");
			sb.append("'" + line + "'");
			if (i < lineCount - 1) {
				sb.append(" +");
			}
			sb.append("\n");
		}
		sb.append("}\n");
		return sb.toString();
	}
}
