/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle

import org.junit.Test
import com.marklogic.gradle.task.test.UnitTestTask

class UnitTestTaskTest extends GroovyTestCase {


	@Test
	void testEscapingJavascriptFilenames() {
		assertEquals("nestedTest.filename.sjs", UnitTestTask.escapeFilename("nestedTest/filename.sjs"))
		assertEquals("nestedTest.filename.sjs", UnitTestTask.escapeFilename("nestedTest\\filename.sjs"))
		assertEquals("nestedTest.doubleNestedTest.filenameWithDetails.sjs",
			UnitTestTask.escapeFilename("nestedTest/doubleNestedTest/filenameWithDetails.sjs"))
		assertEquals("nestedTest.doubleNestedTest.filenameWithDetails.sjs",
			UnitTestTask.escapeFilename("nestedTest\\doubleNestedTest\\filenameWithDetails.sjs"))
	}

	@Test
	public void testEscapingXqueryFilenames() {
		assertEquals("nested-test.filename.xqy", UnitTestTask.escapeFilename("nested-test/filename.xqy"))
		assertEquals("nested-test.filename.xqy", UnitTestTask.escapeFilename("nested-test\\filename.xqy"))
		assertEquals("nested-test.double-nested-test.filename-with-details.xqy",
			UnitTestTask.escapeFilename("nested-test/double-nested-test/filename-with-details.xqy"))
		assertEquals("nested-test.double-nested-test.filename-with-details.xqy",
			UnitTestTask.escapeFilename("nested-test\\double-nested-test\\filename-with-details.xqy"))
	}
}
