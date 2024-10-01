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
package com.marklogic.gradle.task.client

import com.marklogic.gradle.task.client.generator.TransformGenerator
import com.marklogic.gradle.task.client.generator.TransformGeneratorFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

class CreateTransformTask extends AbstractModuleCreationTask {

	@Input
	@Optional
    String transformsDir

    @TaskAction
    void createResource() {
        String propName = "transformName"
        if (getProject().hasProperty(propName)) {
	        String transformsPath = transformsDir
	        if (!transformsPath) {
		        transformsPath = selectModulesPath() + "/transforms"
	        }

            String name = getProject().getProperties().get(propName)

			TransformGeneratorFactory.TransformType type = TransformGeneratorFactory.TransformType.XQY;
            String propType = "transformType"
            if (getProject().hasProperty(propType)) {
				type = TransformGeneratorFactory.TransformType.valueOf(getProject().getProperties().get(propType).toString().toUpperCase());
            }

			def rulesetNames = []
			String propRulesets = "rulesets"
			if (getProject().hasProperty(propRulesets)) {
				getProject().getProperties().get(propRulesets).toString().split(",")
					.each(rule -> rulesetNames.add("'" + rule + "'"))
			}

			TransformGenerator generator = TransformGeneratorFactory.getGenerator(type, transformsPath, rulesetNames as String[]);
			generator.generate(name)
        } else {
            println """Use -PtransformName=your-transform-name [-PtransformType=(xqy|xsl|sjs)] when invoking Gradle to specify a transform name.
You may also use -Prulesets=your-redaction-rulesets to specify a comma-delimited list of redaction rulesets."""
        }
    }
}
