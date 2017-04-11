package com.marklogic.gradle.task

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class ExportConfigTask extends MarkLogicTask {

    @TaskAction
    void exportConfig() {
        String configType
        String configName
        String configFormat
        def needAdmin = false

        if (project.hasProperty("configType") && project.hasProperty("configName") && project.hasProperty("configFormat")) {
            configType = project.property("configType").toLowerCase()
            configName = project.property("configName").toLowerCase()
            configFormat = project.property("configFormat").toLowerCase()
        }
        else {
            println "Please specify configuration type, name, and format; e.g. -PconfigType=database -PconfigName=sample-project-content -PconfigFormat=xml"
            return
        }

        if (configType.matches('database|forest|server|user|role')) {
            // determine if admin is needed
            if (configType.matches('user|role')) {
                needAdmin = true
            }

            // construct resource address
            String resource = '/manage/v2/' + configType + 's/' + configName + '/properties'
            if (configType.equals('server')) {
                resource = resource + '?group-id=Default'
            }

            // GET config
            String config
            if (configFormat.equals('xml')) {
                if (needAdmin) {
                    config = getManageClient().getXmlAsAdmin(resource).getPrettyXml()
                }
                else {
                    config = getManageClient().getXml(resource).getPrettyXml()
                }
            }
            else if (configFormat.equals('json')) {
                if (needAdmin) {
                    //config = getManageClient().getJsonAsAdmin(resource)
                    println "Please use -PconfigFormat=xml for user and role configurations. JSON support will be added with ml-app-deployer 2.7.0"
                    return
                }
                else {
                    config = getManageClient().getJson(resource)
                }
            }
            else {
                println "Unsupported configuration format"
                return
            }
            
            // create ml-config subdirectory
            String dir
            if (configType.matches('user|role')) {
                dir = 'src/main/ml-config/security/' + configType + 's/'
            }
            else {
                dir = 'src/main/ml-config/' + configType + 's/'
            }
            new File(dir).mkdirs()

            // construct filename i.e. filename = 'sample-project-content-database.xml'
            String filename = configName + '-' + configType + '.' + configFormat
            
            // write to file
            new File(dir, filename).withWriter('UTF-8') { it << config }
        }
        else {
            println "Unsupported configuration type"
        }
    }
}