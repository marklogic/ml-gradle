/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
