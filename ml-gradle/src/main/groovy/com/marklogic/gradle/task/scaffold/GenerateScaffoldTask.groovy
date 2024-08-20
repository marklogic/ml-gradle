/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.gradle.task.scaffold

import com.marklogic.appdeployer.scaffold.ScaffoldGenerator
import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class GenerateScaffoldTask extends MarkLogicTask {

    @TaskAction
    void generateScaffold() {
        ScaffoldGenerator g = new ScaffoldGenerator()
        def propName = "scaffoldPath"
        def path = project.hasProperty(propName) ? project.property(propName) : getProject().getProjectDir().getAbsolutePath()
        println "Generating scaffold for path: " + path
		ScaffoldGenerator.AppInputs appInputs = new ScaffoldGenerator.AppInputs(getAppConfig().getName())
        g.generateScaffold(path, appInputs)
    }
}
