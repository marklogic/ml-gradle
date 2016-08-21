package com.marklogic.gradle.task

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

    @Delegate MlcpBean mlcpBean = new MlcpBean();

    public Logger getLogger() {
        return Logging.getLogger(MlcpTask.class)
    }

    @TaskAction
    @Override
    public void exec() {
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
                    default:
                        propVal = val
                        break
                }

                newArgs.add("-" + prop);
                newArgs.add(String.valueOf(propVal));
            }
        }

		// Ensure connection arguments are present
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

		println "mlcp arguments, excluding password: " + newArgs

        newArgs.add("-password")
        newArgs.add(password ? password : config.getRestAdminPassword())

        setArgs(newArgs)

        super.exec()
    }
}
