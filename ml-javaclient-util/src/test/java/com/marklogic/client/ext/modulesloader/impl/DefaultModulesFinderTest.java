/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.client.ext.modulesloader.Modules;
import com.marklogic.client.ext.modulesloader.ModulesFinder;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefaultModulesFinderTest {

    private ModulesFinder sut = new DefaultModulesFinder();

    @Test
    public void baseDirWithExtensionsOfEachKind() throws IOException {
        Modules modules = sut.findModules(getBaseDir("sample-base-dir"));
        assertEquals(2, modules.getOptions().size());
		List<String> validOptions = Arrays.asList(
			"javascript-options.json", "sample-options.xml"
		);
		modules.getOptions().forEach(resource -> {
			assertTrue(validOptions.contains(resource.getFilename()), resource.getFilename() + " is not a valid resource");
		});

        assertEquals(3, modules.getServices().size(),
			"Only recognized XQuery files should be included; the XML file should be ignored");
		List<String> validServices = Arrays.asList(
			"another-sample.xq", "javascript.sjs", "sample.xqy"
		);
		modules.getServices().forEach(resource -> {
			assertTrue(validServices.contains(resource.getFilename()), resource.getFilename() + " is not a valid resource");
		});

        assertEquals(5, modules.getTransforms().size(),
			"Only recognized XSL files should be included; the XML file should be ignored");
		List<String> validTransforms = Arrays.asList(
			"another-sample.xslt", "another-sample-xquery-transform.xq", "javascript-transform.sjs", "sample.xsl", "sample-xquery-transform.xqy"
		);
        modules.getTransforms().forEach(resource -> {
			assertTrue(validTransforms.contains(resource.getFilename()), resource.getFilename() + " is not a valid resource");
		});

        List<Resource> dirs = modules.getAssetDirectories();
        assertEquals(3, dirs.size());
        assertEquals("ext", dirs.get(0).getFile().getName());
        assertEquals("include-this-too", dirs.get(1).getFile().getName());
		assertEquals("root", dirs.get(2).getFile().getName());
    }

    @Test
    public void emptyBaseDir() {
        Modules files = sut.findModules(getBaseDir("empty-base-dir"));
        assertEquals(0, files.getAssetDirectories().size());
        assertEquals(0, files.getOptions().size());
        assertEquals(0, files.getServices().size());
        assertEquals(0, files.getTransforms().size());
    }

    @Test
    public void missingBaseDir() {
        Modules files = sut.findModules("base-dir-doesnt-exist");
        assertEquals(0, files.getAssetDirectories().size());
        assertEquals(0, files.getOptions().size());
        assertEquals(0, files.getServices().size());
        assertEquals(0, files.getTransforms().size());
    }

    private String getBaseDir(String path) {
        try {
            return new ClassPathResource(path).getFile().toString();
        } catch (IOException e) {
            throw new RuntimeException(path);
        }
    }
}
