package com.marklogic.gradle.task.datamovement

import com.marklogic.client.datamovement.ExportToWriterListener
import com.marklogic.client.document.DocumentManager
import com.marklogic.client.document.ServerTransform
import com.marklogic.client.ext.datamovement.QueryBatcherBuilder
import com.marklogic.client.ext.datamovement.listener.XmlOutputListener
import com.marklogic.client.io.Format
import org.gradle.api.tasks.TaskAction

class ExportToFileTask extends DataMovementTask {

	@TaskAction
	void exportToFile() {
		if (!hasWhereSelectorProperty()) {
			println "Invalid inputs; task description: " + getDescription()
			return;
		}

		String exportPath = "build/export.xml"
		if (project.hasProperty("exportPath")) {
			exportPath = project.property("exportPath")
		}

		BuilderAndMessage builderAndMessage = determineBuilderAndMessage()
		QueryBatcherBuilder builder = builderAndMessage.builder
		String message = builderAndMessage.message

		File exportFile = new File(exportPath)
		if (exportFile.getParentFile() != null) {
			exportFile.getParentFile().mkdirs()
		}
		FileWriter fileWriter = new FileWriter(exportFile)

		ExportToWriterListener listener = new ExportToWriterListener(fileWriter)
		listener.onGenerateOutput(new XmlOutputListener())

		try {
			println "Exporting documents " + message + " to file at: " + exportPath

			if (project.hasProperty("filePrefix")) {
				String prefix = project.property("filePrefix")
				println "Writing the following content to the start of the file: " + prefix
				fileWriter.write(prefix)
				fileWriter.write("\n")
			}

			if (project.hasProperty("transform")) {
				String transform = project.property("transform")
				println "Applying server transform: " + transform
				listener.withTransform(new ServerTransform(transform))
			}

			if (project.hasProperty("recordPrefix")) {
				String recordPrefix = project.property("recordPrefix")
				println "Applying record prefix: " + recordPrefix
				listener.withRecordPrefix(recordPrefix)
			}

			if (project.hasProperty("recordSuffix")) {
				String recordSuffix = project.property("recordSuffix")
				println "Applying record suffix: " + recordSuffix
				listener.withRecordSuffix(recordSuffix)
			}

			println "Exporting documents..."
			applyWithQueryBatcherBuilder(listener, builder)
			println "Finished exporting documents " + message
		} finally {

			if (project.hasProperty("fileSuffix")) {
				String suffix = project.property("fileSuffix")
				println "Writing the following content to the end of the file: " + suffix
				fileWriter.write(suffix)
			}

			fileWriter.close()
		}
	}
}
