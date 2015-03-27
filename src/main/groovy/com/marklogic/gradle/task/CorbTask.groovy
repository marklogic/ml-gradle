package com.marklogic.gradle.task

import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.AppConfig

class CorbTask extends JavaExec {

    String xccUrl
    String collectionName = '""'

    // It's common practice for the uris/transform modules to have the same prefix, so just set this if that's the
    // case - e.g. convert-uris.xqy and convert-transform.xqy
    String modulePrefix

    // Otherwise, set transformModule and urisModule
    String transformModule
    String urisModule
    
    // corb defaults to 1, but 8 seems like a more common default
    int threadCount = 8
    
    String moduleRoot = "/"
    
    String modulesDatabase
    
    String install = "false"
    
    @TaskAction
    @Override
    public void exec() {
        setMain("com.marklogic.developer.corb.Manager")

        AppConfig config = getProject().property("mlAppConfig")

        List<String> newArgs = new ArrayList<>()

        if (xccUrl) {
            newArgs.add(xccUrl)
        } else {
            newArgs.add(config.getXccUrl())
        }

        newArgs.add(collectionName)

        if (modulePrefix) {
            newArgs.add(modulePrefix + "-transform.xqy")
        } else {
            newArgs.add(transformModule)
        }
        
        newArgs.add(threadCount + "")
        
        if (modulePrefix) {
            newArgs.add(modulePrefix + "-uris.xqy")
        } else {
            newArgs.add(urisModule)
        }
        
        newArgs.add(moduleRoot)
        
        if (modulesDatabase) {
            newArgs.add(modulesDatabase)
        } else {
            newArgs.add(config.getModulesDatabaseName())
        }
        
        newArgs.add(install)
        
        setArgs(newArgs)
        super.exec()
    }
}
