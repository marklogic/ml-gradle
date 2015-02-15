package com.marklogic.gradle.task.client

import org.gradle.api.tasks.TaskAction

import com.marklogic.client.DatabaseClient
import com.marklogic.clientutil.modulesloader.ModulesLoader
import com.marklogic.clientutil.modulesloader.impl.DefaultExtensionLibraryDescriptorBuilder
import com.marklogic.clientutil.modulesloader.impl.DefaultModulesLoader

class LoadModulesTask extends ClientTask {

    List<String> modulePaths
    String modulesLoaderClassName
    String assetRolesAndCapabilities
    
    @TaskAction
    void loadModules() {
        DatabaseClient client = newClient()
        
        ModulesLoader loader = null
        if (modulesLoaderClassName) {
            loader = Class.forName(modulesLoaderClassName).newInstance()
        } else {
            loader = new DefaultModulesLoader()
            if (assetRolesAndCapabilities) {
                println "Will load assets with roles and capabilities: " + assetRolesAndCapabilities
                ((DefaultModulesLoader)loader).setExtensionLibraryDescriptorBuilder(new DefaultExtensionLibraryDescriptorBuilder(assetRolesAndCapabilities))
            }
        }
        
        List<String> directories = modulePaths != null ? modulePaths : getAppConfig().modulePaths
        println "Module paths: " + directories
        try {
            for (int i = 0; i < directories.size(); i++) {
                String dir = directories.get(i);
                println "Loading modules found at ${dir}"
                loader.loadModules(new File(dir), client)
            }

            println "Finished loading modules\n"
        } finally {
            client.release()
        }
    }
}
