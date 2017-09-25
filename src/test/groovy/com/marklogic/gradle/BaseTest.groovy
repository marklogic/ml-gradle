package com.marklogic.gradle

import com.marklogic.mgmt.ManageClient
import com.marklogic.mgmt.ManageConfig
import com.marklogic.mgmt.admin.AdminConfig
import com.marklogic.mgmt.admin.AdminManager
import com.marklogic.mgmt.resource.appservers.ServerManager
import com.marklogic.mgmt.resource.databases.DatabaseManager
import com.marklogic.rest.util.ResourcesFragment
import org.custommonkey.xmlunit.XMLUnit
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class BaseTest extends Specification {
	static final TemporaryFolder testProjectDir = new TemporaryFolder()
	static File buildFile
	static File propertiesFile

	static BuildResult runTask(String... task) {
		return GradleRunner.create()
			.withProjectDir(testProjectDir.root)
			.withArguments(task)
			.withDebug(true)
			.withPluginClasspath()
			.build()
	}

	BuildResult runFailTask(String... task) {
		return GradleRunner.create()
			.withProjectDir(testProjectDir.root)
			.withArguments(task)
			.withDebug(true)
			.withPluginClasspath().buildAndFail()
	}

	static void createBuildFile() {
		buildFile = testProjectDir.newFile('build.gradle')
		buildFile << """
            plugins {
                id 'com.marklogic.ml-gradle'
            }
        """
	}

	static AdminConfig getAdminConfig() {
		return new AdminConfig("localhost", 8001, "admin", "admin")
	}

	static AdminManager getAdminManager() {
		return new AdminManager(getAdminConfig())
	}

	static ManageClient getManageClient() {
		ManageConfig manageConfig = new ManageConfig("localhost", 8002, "admin", "admin")
		ManageClient manageClient = new ManageClient(manageConfig)
		return manageClient
	}

	static ServerManager getServerManager() {
		return new ServerManager(getManageClient());
	}

	static DatabaseManager getDatabaseManager() {
		return new DatabaseManager(getManageClient());
	}

	static ResourcesFragment getDbConfig() {
		return getDatabaseManager().getAsXml()
	}

	static ResourcesFragment getServerConfig() {
		return getServerManager().getAsXml()
	}

	static void createPropertiesFile(contents) {
		propertiesFile = testProjectDir.newFile('gradle.properties')
		propertiesFile << contents
	}

	def setupSpec() {
		XMLUnit.setIgnoreWhitespace(true)
		testProjectDir.create()
		createBuildFile()
	}
}
