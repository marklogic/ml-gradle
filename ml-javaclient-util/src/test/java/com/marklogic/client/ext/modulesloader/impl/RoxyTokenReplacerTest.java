/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
