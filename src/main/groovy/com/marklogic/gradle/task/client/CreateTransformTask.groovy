package com.marklogic.gradle.task.client

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import com.marklogic.gradle.task.MarkLogicTask;

class CreateTransformTask extends MarkLogicTask {

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

    String transformsDir

    @TaskAction
    void createResource() {
        String propName = "transformName"
        if (getProject().hasProperty(propName)) {
            transformsDir = transformsDir ? transformsDir : getAppConfig().getModulePaths().get(0) + "/transforms"

            String name = getProject().getProperties().get(propName)

            String defaultType = "xqy"
            String type = "xqy"
            String propType = "transformType"
            if (getProject().hasProperty(propType)) {
                type = getProject().getProperties().get(propType)
            }

            String template = type == "xqy" ? XQUERY_TEMPLATE : XSL_TEMPLATE
            String suffix = type == "xqy" ? ".xqy" : ".xsl"
            String transform = template.replace("%%TRANSFORM_NAME%%", name)

            new File(transformsDir).mkdirs()
            def transformFile = new File(transformsDir, name + suffix)
            println "Creating new transform at " + transformFile.getAbsolutePath()
            transformFile.write(transform)

			def metadataDir = new File(transformsDir, "metadata")
			metadataDir.mkdirs()
			String metadata = METADATA_TEMPLATE.replace("%%TRANSFORM_NAME%%", name)
			def metadataFile = new File(metadataDir, name + ".xml")
			println "Creating new transform metadata file at " + metadataFile.getAbsolutePath()
			metadataFile.write(metadata)
        } else {
            println "Use -PtransformName=your-transform-name [-PtransformType=(xqy|xslt)] when invoking Gradle to specify a transform name"
        }
    }
}
