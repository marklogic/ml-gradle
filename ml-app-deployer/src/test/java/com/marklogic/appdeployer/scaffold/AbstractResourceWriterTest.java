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
package com.marklogic.appdeployer.scaffold;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.template.TemplateBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractResourceWriterTest extends AbstractAppDeployerTest {

	protected Map<String, Object> propertyMap = new HashMap<>();
	protected API api;

	@AfterEach
	public void teardown() {
		undeploySampleApp();
	}

	@BeforeEach
	public void initializeConfigDir() throws IOException {
		api = new API(manageClient);

		File baseDir = new File("build/generate-test");
		baseDir.mkdirs();
		FileUtils.cleanDirectory(baseDir);
		appConfig.setConfigDir(new ConfigDir(baseDir));
	}

	protected void writeResource(TemplateBuilder templateBuilder) {
		Resource r = templateBuilder.buildTemplate(propertyMap);
		new DefaultResourceWriter().writeResourceAsJson(r, appConfig.getFirstConfigDir());
	}

	protected void buildResourceAndDeploy(TemplateBuilder templateBuilder) {
		writeResource(templateBuilder);
		deploySampleApp();
	}
}
