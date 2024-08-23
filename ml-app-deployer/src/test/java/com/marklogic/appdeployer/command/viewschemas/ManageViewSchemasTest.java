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
package com.marklogic.appdeployer.command.viewschemas;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.junit.Fragment;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.clusters.ClusterManager;
import com.marklogic.mgmt.resource.viewschemas.ViewSchemaManager;
import com.marklogic.rest.util.RestConfig;
import com.marklogic.rest.util.RestTemplateUtil;
import org.jdom2.Namespace;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;

public class ManageViewSchemasTest extends AbstractManageResourceTest {

	@Override
	protected void initializeAndDeploy() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/view-schemas"));

		initializeAppDeployer(new DeployRestApiServersCommand(), new DeployOtherDatabasesCommand(1),
			newCommand(), buildLoadModulesCommand());

		appDeployer.deploy(appConfig);
	}

	@Override
	protected ResourceManager newResourceManager() {
		return new ViewSchemaManager(manageClient, appConfig.getContentDatabaseName());
	}

	@Override
	protected Command newCommand() {
		return new DeployViewSchemasCommand();
	}

	@Override
	protected String[] getResourceNames() {
		return new String[]{"main"};
	}

	/**
	 * We don't need to verify anything here, as SQL schemas and views live in the sample-app-schemas database which was
	 * already deleted.
	 */
	@Override
	protected void verifyResourcesWereDeleted(ResourceManager mgr) {
	}

	/**
	 * Let's insert a couple documents and try out our SQL view. We'll use Spring's RestTemplate to connect to the REST
	 * API server to insert documents and then invoke a custom extension that calls xdmp:sql.
	 */
	@Override
	protected void afterResourcesCreated() {
		RestConfig restConfig = new RestConfig(appConfig.getHost(), appConfig.getRestPort(),
			appConfig.getRestAdminUsername(), appConfig.getRestAdminPassword());
		RestTemplate clientTemplate = RestTemplateUtil.newRestTemplate(restConfig);

		String baseUrl = format("http://%s:%d", appConfig.getHost(), appConfig.getRestPort());

		clientTemplate.put(baseUrl + "/v1/documents?uri=doc1.xml&format=xml", "<order><amount>111</amount></order>");
		clientTemplate.put(baseUrl + "/v1/documents?uri=doc2.xml&format=xml", "<order><amount>222</amount></order>");

		ResponseEntity<String> response = clientTemplate
			.getForEntity(baseUrl + "/v1/resources/sql?rs:query=select * from ordertable", String.class);
		String body = response.getBody();
		body = body.substring(body.indexOf("<json:array"));

		String version = new ClusterManager(manageClient).getVersion();
		// This changed between ML8 and ML9
		String expectedAmount = version != null && version.startsWith("8") ? "amount" : "main.ordertable.amount";

		Fragment xml = new Fragment("<results>" + body + "</results>",
			Namespace.getNamespace("json", "http://marklogic.com/xdmp/json"));
		xml.assertElementValue("/results/json:array[1]/json:value", expectedAmount);
		xml.assertElementValue("/results/json:array[2]/json:value", "111");
		xml.assertElementValue("/results/json:array[3]/json:value", "222");
	}

}
