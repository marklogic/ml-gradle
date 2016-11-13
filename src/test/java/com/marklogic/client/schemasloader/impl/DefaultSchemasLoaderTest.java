package com.marklogic.client.schemasloader.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.marklogic.client.schemasloader.impl.DefaultSchemasFinder;

public class DefaultSchemasLoaderTest {

	DefaultSchemasFinder finder;

	@Test
	public void testDefaultSchemasLoaderTest() {
		finder = new DefaultSchemasFinder();
		List<File> files = finder.findSchemas(getBaseDir("sample-base-dir/schemas"));

		assertEquals("Found 3 files", 3, files.size());

		List<String> names = new ArrayList<>();
		for (File f : files) {
			names.add(f.getName());
		}
		assertTrue(names.contains("my.ruleset"));
		assertTrue(names.contains("my.tde"));
		assertTrue(names.contains("my.xsd"));
	}

	private File getBaseDir(String path) {
		try {
			return new ClassPathResource(path).getFile();
		} catch (IOException e) {
			throw new RuntimeException(path);
		}
	}
}
