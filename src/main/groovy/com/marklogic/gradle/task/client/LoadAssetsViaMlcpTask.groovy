package com.marklogic.gradle.task.client

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.AppConfig
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
    
    /**
     * The directory that contains all assets modules consolidated from the application and any of its libraries. Consolidating
     * everything into one directory means we can easily load it via a single mlcp import call.
     */
    String consolidatedAssetsPath = "build/ml-gradle/consolidatedAssets"
    
    /**
     * The subdirectory in the consolidated assets directory that contains the modules to be loaded. This can be blank.
     * It defaults to /ext under the assumption that a developer wants all of an application's asset modules to be 
     * accessible via the REST API, even though they're being loaded via mlcp. 
     */
    String inputFilePathPrefix = "/ext"
    
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
        setInput_file_path(consolidatedAssetsPath + inputFilePathPrefix)

        setOutput_uri_replace(LoadAssetsViaMlcpTask.generateOutputUriReplace(consolidatedAssetsPath))
    }

    /**
     * This pattern removes everything ending with the consolidated assets directory.
     */
    public static String generateOutputUriReplace(String consolidatedAssetsPath) {
        return "\"" + new File(consolidatedAssetsPath).getAbsolutePath().replace("\\", "/").replaceFirst("([a-zA-Z]:)", {"/"+it[0].toUpperCase()}) + ", ''\""
    }
}
