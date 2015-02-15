package com.marklogic.gradle.task.cpf

import org.gradle.api.tasks.TaskAction

/**
 * Note that this will add each pipeline to each of the given domain names. You'll need separate instances of this task
 * if that approach does not work for your pipelines. 
 */
class InsertPipelinesTask extends CpfTask {

    String[] filePaths
    String[] domainNames

    @TaskAction
    void insertPipelines() {
        def h = newCpfHelper()
        for (String path : filePaths) {
            def xml = new File(path).text
            // This is hacky; we need to remove double quotes so they don't mess up the xdmp:eval call
            // TODO Perhaps xdmp:invoke-function is the better tool?
            xml = xml.replace('"', "'")
            def pipelineId = h.evaluateAgainstTriggersDatabase("p:insert(" + xml + ")")
            println "Inserted pipeline from path: " + path

            for (String name : domainNames) {
                h.addPipelineToDomain(pipelineId, name)
                println "Added pipeline to domain: " + name
            }
        }
        println "Finished inserting pipelines\n"
    }
}
