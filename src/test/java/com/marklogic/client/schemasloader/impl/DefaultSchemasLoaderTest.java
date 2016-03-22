package com.marklogic.client.schemasloader.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
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
		
		assertEquals("File Item 0", "my.ruleset", files.get(0).getName());
		assertEquals("File Item 1", "my.tde", files.get(1).getName());
		assertEquals("File Item 2", "my.xsd", files.get(2).getName());
	}

	private File getBaseDir(String path) {
		try {
			return new ClassPathResource(path).getFile();
		} catch (IOException e) {
			throw new RuntimeException(path);
		}
	}
}
