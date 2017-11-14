package com.marklogic.gradle.task

import com.marklogic.client.DatabaseClient
import com.marklogic.client.io.FileHandle
import com.marklogic.contentpump.bean.MlcpBean
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.AppConfig


/**
 * Delegates properties to an instance of MlcpBean, which has properties for all MLCP arguments (and if it's out of date
 * and missing one, you can pass them via this class's args property that's inherited from JavaExec). This task
 * will also default the host/port to the values defined in the mlAppConfig instance populated by the plugin.
 *
 * Note that this defaults to using appConfig.restAdminUsername and appConfig.restAdminPassword. That user may not
 * have permission to perform the mlcp operation you wish to perform. In that case, just set the username/password
 * parameters of this task for the appropriate user.
 */
class MlcpTask extends JavaExec {

	@Delegate
	MlcpBean mlcpBean = new MlcpBean();

	// Set this to define a URI in your content database for mlcp output to be written to as a text document
	String logOutputUri

	Logger getLogger() {
		return Logging.getLogger(MlcpTask.class)
	}

	@TaskAction
	@Override
	void exec() {
		setMain("com.marklogic.contentpump.ContentPump")
		AppConfig config = getProject().property("mlAppConfig")

		List<String> newArgs = new ArrayList<>()
		newArgs.add(command)

		mlcpBean.properties.each { prop, val ->
			def propVal
			if (val) {
				switch (prop) {
					case "host":
						propVal = (val ? val : config.getHost())
						break
					case "port":
						propVal = (val ? val : 8000)
						break
					case "username":
						propVal = (val ? val : config.getRestAdminUsername())
						break
					case ["class", "logger", "command", "password"]:
						// skip for now
						return
					case "additionalOptions":
						// Not supported by this task; use JavaExec's args instead
						return
					default:
						propVal = val
						break
				}

				newArgs.add("-" + prop);
				newArgs.add(String.valueOf(propVal));
			}
		}

		// Ensure connection arguments are present, but not if a COPY
		boolean isCopy = "COPY".equals(command)
		if (!isCopy) {
			if (!newArgs.contains("-host")) {
				newArgs.add("-host")
				newArgs.add(config.getHost())
			}
			if (!newArgs.contains("-port")) {
				newArgs.add("-port")
				newArgs.add("8000")
			}
			if (!newArgs.contains("-username")) {
				newArgs.add("-username")
				newArgs.add(config.getRestAdminUsername())
			}
		}

		// Include any args that a user has configured via the args parameter of the Gradle task
		newArgs.addAll(getArgs())

		println "mlcp arguments, excluding password: " + newArgs

		if (!isCopy) {
			newArgs.add("-password")
			newArgs.add(password ? password : config.getRestAdminPassword())
		}

		setArgs(newArgs)

		File logOutputFile = null
		if (logOutputUri) {
			println "Will write mlcp log output to URI: " + logOutputUri
			logOutputFile = new File(getProject().getBuildDir(), "mlcp-log-output-" + System.currentTimeMillis() + ".txt")
			setStandardOutput(logOutputFile.newOutputStream())
		}

		super.exec()

		if (logOutputFile != null) {
			AppConfig appConfig = project.property("mlAppConfig")
			DatabaseClient client = appConfig.newDatabaseClient()
			client.newDocumentManager().write(logOutputUri, new FileHandle(logOutputFile))
			println "Wrote mlcp log output to URI: " + logOutputUri
		}
	}
}
