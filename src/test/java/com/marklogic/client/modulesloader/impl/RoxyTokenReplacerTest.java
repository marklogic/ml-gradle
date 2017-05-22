package com.marklogic.client.modulesloader.impl;

import com.marklogic.client.ext.tokenreplacer.RoxyTokenReplacer;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.FileCopyUtils;

import java.io.File;

public class RoxyTokenReplacerTest extends Assert {

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
