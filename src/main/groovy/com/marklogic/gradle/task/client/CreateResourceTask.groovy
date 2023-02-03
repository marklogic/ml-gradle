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

import com.marklogic.gradle.task.MarkLogicTask

class CreateResourceTask extends AbstractModuleCreationTask {

    final static String SJS_RESOURCE_TEMPLATE =
'''function get(context, params) {
  // return zero or more document nodes
};

function post(context, params, input) {
  // return zero or more document nodes
};

function put(context, params, input) {
  // return at most one document node
};

function deleteFunction(context, params) {
  // return at most one document node
};

exports.GET = get;
exports.POST = post;
exports.PUT = put;
exports.DELETE = deleteFunction;
'''

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

	@Input
	@Optional
    String servicesDir

    @TaskAction
    void createResource() {
        String propName = "resourceName"
        if (getProject().hasProperty(propName)) {
	        String servicesPath = servicesDir
	        if (!servicesPath) {
		        servicesPath = selectModulesPath() + "/services"
	        }

            String name = getProject().getProperties().get(propName)

            String template = RESOURCE_TEMPLATE
            String fileExtension = ".xqy"
            if (getProject().hasProperty("resourceType") && "sjs".equals(getProject().getProperties().get("resourceType"))) {
                template = SJS_RESOURCE_TEMPLATE
                fileExtension = ".sjs"
            }

            String resource = template.replace("%%RESOURCE_NAME%%", name)
            new File(servicesPath).mkdirs()
            def resourceFile = new File(servicesPath, name + fileExtension)
            println "Creating new resource at " + resourceFile.getAbsolutePath()
            resourceFile.write(resource)

            def metadataDir = new File(servicesPath, "metadata")
            metadataDir.mkdirs()
            String metadata = METADATA_TEMPLATE.replace("%%RESOURCE_NAME%%", name)
            def metadataFile = new File(metadataDir, name + ".xml")
            println "Creating new resource metadata file at " + metadataFile.getAbsolutePath()
            metadataFile.write(metadata)
        } else {
            println "Use -PresourceName=your-resource-name -PresourceType=(xqy|sjs) when invoking Gradle to specify a resource name"
        }
    }
}
