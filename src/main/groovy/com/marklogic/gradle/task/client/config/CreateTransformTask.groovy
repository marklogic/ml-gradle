package com.marklogic.gradle.task.client.config

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

class CreateTransformTask extends DefaultTask {

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
    
    String transformsDir = "src/main/xqy/transforms"

    @TaskAction
    void createResource() {
        String propName = "transformName"
        if (getProject().hasProperty(propName)) {
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
        } else {
            println "Use -PtransformName=your-transform-name [-PtransformType=(xqy|xslt)] when invoking Gradle to specify a transform name"
        }
    }
}
