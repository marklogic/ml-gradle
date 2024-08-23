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
package com.marklogic.appdeployer.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.appdeployer.AbstractAppDeployerTest;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;

public abstract class AbstractExportTest extends AbstractAppDeployerTest {

	protected File exportDir;
	protected static ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	public void initializeExportDir() throws IOException {
		exportDir = new File("build/export-test");
		exportDir.mkdirs();
		FileUtils.cleanDirectory(exportDir);
	}
}
