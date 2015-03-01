package com.marklogic.gradle.task.client

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.client.DatabaseClientFactory
import com.marklogic.client.DatabaseClientFactory.Authentication
import com.marklogic.clientutil.modulesloader.impl.DefaultModulesLoader
import com.marklogic.gradle.task.MlcpTask

/**
 * Use this task for loading REST API asset modules via MarkLogic Content Pump (MLCP) rather than via the REST API 
 * endpoint for loading asset modules. Once you have dozens or hundreds of modules, MLCP is usually much faster. 
 * <p>
 * This also handles updating for each asset module the timestamp that it was last loaded into the modules database. 
 * This prevents LoadModulesTask from loading each asset module again.
 */
class LoadAssetsViaMlcpTask extends MlcpTask {

    String consolidatedAssetsPath = "build/ml-gradle/consolidatedAssets"
    Authentication auth = Authentication.DIGEST

    @TaskAction
    @Override
    void exec() {
        AppConfig config = getProject().property("mlAppConfig")
        setMlcpParameters(config)
        super.exec()

        def client = DatabaseClientFactory.newClient(config.host, config.restPort, config.username, config.password, auth)
        def modulesLoader = new DefaultModulesLoader()
        try {
            config.modulePaths.each { def modulePath ->
                println "Simulating loading of assets for path: " + modulePath
                modulesLoader.simulateLoadingOfAllAssets(new File(modulePath), client)
            }
        }
        finally {
            client.release()
        }
    }

    protected void setMlcpParameters(AppConfig config) {
        setCommand("IMPORT")
        setPort(config.getModulesXdbcPort())
        setInput_file_path(consolidatedAssetsPath + "/ext")

        // This pattern removes everything before the /ext folder; TODO need some unit tests for this! Should move
        // to a Java class and test it there.
        setOutput_uri_replace("\"" + new File(consolidatedAssetsPath).getAbsolutePath().replace("\\", "/").replaceFirst("([a-zA-Z]:)", {"/"+it[0].toUpperCase()}) + ", ''\"")
    }
}
