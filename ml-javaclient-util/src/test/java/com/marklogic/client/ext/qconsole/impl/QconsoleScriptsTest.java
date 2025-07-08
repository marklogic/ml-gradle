/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.qconsole.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QconsoleScriptsTest {

	@Test
	public void testImport() {
		String importModule = QconsoleScripts.IMPORT;
		assertNotNull(importModule);
		assertTrue(importModule.startsWith("xquery version"));
	}

	@Test
	public void testExport() {
		String exportModule = QconsoleScripts.EXPORT;
		assertNotNull(exportModule);
		assertTrue(exportModule.startsWith("xquery version"));
	}
}
