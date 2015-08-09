package com.rjrudin.marklogic.gradle.task.client

import org.gradle.api.tasks.TaskAction

import com.rjrudin.marklogic.gradle.task.MarkLogicTask

class CreateResourceTask extends MarkLogicTask {

    final static String RESOURCE_TEMPLATE = '''xquery version "1.0-ml";

module namespace resource = "http://marklogic.com/rest-api/resource/%%RESOURCE_NAME%%";

declare function get(
  $context as map:map,
  $params  as map:map
  ) as document-node()*
{
  xdmp:log("GET called")
};

declare function put(
  $context as map:map,
  $params  as map:map,
  $input   as document-node()*
  ) as document-node()?
{
  xdmp:log("PUT called")
};

declare function post(
  $context as map:map,
  $params  as map:map,
  $input   as document-node()*
  ) as document-node()*
{
  xdmp:log("POST called")
};

declare function delete(
  $context as map:map,
  $params  as map:map
  ) as document-node()?
{
  xdmp:log("DELETE called")
};
'''

    final static String METADATA_TEMPLATE = '''<metadata>
  <title>%%RESOURCE_NAME%%</title>
  <description>
    <div>
      Use HTML content to provide a description of this resource. The GET method shows an example of how to define the parameters for a method.
    </div>
  </description>
  <method name="GET">
    <param name="id" />
  </method>
  <method name="POST"/>
  <method name="PUT"/>
  <method name="DELETE"/>
</metadata>
'''

    String servicesDir

    @TaskAction
    void createResource() {
        String propName = "resourceName"        
        if (getProject().hasProperty(propName)) {
            servicesDir = servicesDir ? servicesDir : getAppConfig().getModulePaths().get(0) + "/services"
            
            String name = getProject().getProperties().get(propName)

            String resource = RESOURCE_TEMPLATE.replace("%%RESOURCE_NAME%%", name)
            new File(servicesDir).mkdirs()
            def resourceFile = new File(servicesDir, name + ".xqy")
            println "Creating new resource at " + resourceFile.getAbsolutePath()
            resourceFile.write(resource)

            def metadataDir = new File(servicesDir, "metadata")
            metadataDir.mkdirs()
            String metadata = METADATA_TEMPLATE.replace("%%RESOURCE_NAME%%", name)
            def metadataFile = new File(metadataDir, name + ".xml")
            println "Creating new resource metadata file at " + metadataFile.getAbsolutePath()
            metadataFile.write(metadata)
        } else {
            println "Use -PresourceName=your-resource-name when invoking Gradle to specify a resource name"
        }
    }
}
