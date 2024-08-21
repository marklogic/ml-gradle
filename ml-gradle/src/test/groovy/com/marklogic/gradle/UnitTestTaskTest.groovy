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
