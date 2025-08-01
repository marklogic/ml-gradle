/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
