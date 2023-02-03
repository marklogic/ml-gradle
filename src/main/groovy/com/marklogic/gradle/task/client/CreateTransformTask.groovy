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


import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

class CreateTransformTask extends AbstractModuleCreationTask {

    final static String XQUERY_TEMPLATE = '''xquery version "1.0-ml";

module namespace transform = "http://marklogic.com/rest-api/transform/%%TRANSFORM_NAME%%";

declare function transform(
  $context as map:map,
  $params as map:map,
  $content as document-node()
  ) as document-node()
{
  ()
};
'''

	final static String SJS_TEMPLATE =
'''function transform(context, params, content)
{
  // Must return the result of the transform
};
exports.transform = transform;
'''

    final static String XSL_TEMPLATE = '''<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:map="http://marklogic.com/xdmp/map">
  <xsl:param name="context" as="map:map"/>
  <xsl:param name="params"  as="map:map"/>
  <xsl:template match="/*">
    <xsl:copy-of select="."/>
  </xsl:template>
</xsl:stylesheet>
'''

	final static String METADATA_TEMPLATE = '''<metadata>
  <title>%%TRANSFORM_NAME%%</title>
  <description>
    <div>
      Use HTML content to provide a description of this template.
    </div>
  </description>
</metadata>
'''

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

            String type = "xqy"
            String propType = "transformType"
            if (getProject().hasProperty(propType)) {
                type = getProject().getProperties().get(propType)
            }

            String template = XQUERY_TEMPLATE
	        String fileExtension = ".xqy"
	        if ("xsl".equals(type)) {
		        template = XSL_TEMPLATE
		        fileExtension = ".xsl"
	        } else if ("sjs".equals(type)) {
		        template = SJS_TEMPLATE
		        fileExtension  = ".sjs"
	        }

            String transform = template.replace("%%TRANSFORM_NAME%%", name)

            new File(transformsPath).mkdirs()
            def transformFile = new File(transformsPath, name + fileExtension)
            println "Creating new transform at " + transformFile.getAbsolutePath()
            transformFile.write(transform)

			def metadataDir = new File(transformsPath, "metadata")
			metadataDir.mkdirs()
			String metadata = METADATA_TEMPLATE.replace("%%TRANSFORM_NAME%%", name)
			def metadataFile = new File(metadataDir, name + ".xml")
			println "Creating new transform metadata file at " + metadataFile.getAbsolutePath()
			metadataFile.write(metadata)
        } else {
            println "Use -PtransformName=your-transform-name [-PtransformType=(xqy|xsl|sjs)] when invoking Gradle to specify a transform name"
        }
    }
}
