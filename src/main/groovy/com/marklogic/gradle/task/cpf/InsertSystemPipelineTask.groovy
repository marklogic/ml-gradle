package com.marklogic.gradle.task.cpf

import org.gradle.api.tasks.TaskAction

class InsertSystemPipelineTask extends CpfTask {

    String filename
    
    @TaskAction
    void installMarkLogicPipeline() {
        newCpfHelper().installSystemPipeline(filename)
        println "Installed system pipeline: " + filename + "\n"
    }
}
