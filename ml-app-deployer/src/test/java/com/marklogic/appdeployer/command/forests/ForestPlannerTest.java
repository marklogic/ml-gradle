/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.api.forest.Forest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ForestPlannerTest extends AbstractAppDeployerTest {

	private static final String DB_NAME = "sample-app-content";

	@Test
	void test() {
		List<Forest> forests = new ForestPlanner(manageClient).previewForestPlan(DB_NAME, appConfig);
		assertEquals(3, forests.size());
		assertEquals("sample-app-content-1", forests.get(0).getForestName());
		assertEquals("sample-app-content-2", forests.get(1).getForestName());
		assertEquals("sample-app-content-3", forests.get(2).getForestName());
	}
}
