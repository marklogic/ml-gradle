package com.marklogic.gradle.task

import com.marklogic.contentpump.bean.MlcpBean
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.AppConfig


/**
 * Provides parameters for some, but not all, mlcp arguments. Arguments that aren't supported can be passed in
 * via JavaExec's "args" property. The main benefit of using this class is that it assumes usage of the connection
 * properties found in the mlAppConfig project property.
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

        println "mlcp arguments, excluding password: " + newArgs
        
        newArgs.add("-password")
        newArgs.add(password ? password : config.getRestAdminPassword())
        
        setArgs(newArgs)

        super.exec()
    }
}
