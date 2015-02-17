package com.marklogic.gradle.task.client

import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction

import com.marklogic.clientutil.modulesloader.impl.DefaultModulesLoader
import com.marklogic.gradle.AppConfig

class WatchTask extends JavaExec {

    String modulesLoaderClassName = DefaultModulesLoader.class.getName()

    @TaskAction
    @Override
    public void exec() {
        setMain("com.marklogic.clientutil.modulesloader.ModulesWatcher")
        setClasspath(getProject().sourceSets.main.runtimeClasspath)

        AppConfig config = getProject().property("mlAppConfig")
        def username = config.getUsername() ? config.getUsername() : getProject().property("mlUsername")
        def password = config.getPassword() ? config.getPassword() : getProject().property("mlPassword")
        setArgs([
            config.getModulePaths().join(","),
            config.getHost(),
            config.getRestPort(),
            username,
            password,
            modulesLoaderClassName
        ])

        super.exec()
    }
}
