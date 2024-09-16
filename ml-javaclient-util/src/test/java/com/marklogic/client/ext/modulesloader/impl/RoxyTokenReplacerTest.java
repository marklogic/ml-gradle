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
package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.client.ext.tokenreplacer.RoxyTokenReplacer;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileCopyUtils;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RoxyTokenReplacerTest {

	@Test
	public void test() throws Exception {
		RoxyTokenReplacer r = new RoxyTokenReplacer();
		String original = new String(
			FileCopyUtils.copyToByteArray(new File("src/test/resources/token-replace/ext/test.xqy")));

		String modified = r.replaceTokens(original);
		assertTrue(modified.contains("<color>red</color>"));
		assertTrue(modified.contains("<number>20</number>"));
		assertTrue(modified.contains("<vehicle>red wagon</vehicle>"));
		assertTrue(modified.contains("<outfit>red dress</outfit>"));
	}

}
