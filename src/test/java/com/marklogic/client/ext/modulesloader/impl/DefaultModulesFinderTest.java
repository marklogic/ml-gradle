package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.client.ext.modulesloader.Modules;
import com.marklogic.client.ext.modulesloader.ModulesFinder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DefaultModulesFinderTest extends Assert {

    private ModulesFinder sut = new DefaultModulesFinder();

    @Test
    public void baseDirWithExtensionsOfEachKind() throws IOException {
        Modules modules = sut.findModules(getBaseDir("sample-base-dir"));
        assertEquals(2, modules.getOptions().size());
		List<String> validOptions = Arrays.asList(
			"javascript-options.json", "sample-options.xml"
		);
		modules.getOptions().forEach(resource -> {
			assertTrue(resource.getFilename() + " is not a valid resource", validOptions.contains(resource.getFilename()));
		});

        assertEquals("Only recognized XQuery files should be included; the XML file should be ignored", 3,
                modules.getServices().size());
		List<String> validServices = Arrays.asList(
			"another-sample.xq", "javascript.sjs", "sample.xqy"
		);
		modules.getServices().forEach(resource -> {
			assertTrue(resource.getFilename() + " is not a valid resource", validServices.contains(resource.getFilename()));
		});

        assertEquals("Only recognized XSL files should be included; the XML file should be ignored", 5,
                modules.getTransforms().size());
		List<String> validTransforms = Arrays.asList(
			"another-sample.xslt", "another-sample-xquery-transform.xq", "javascript-transform.sjs", "sample.xsl", "sample-xquery-transform.xqy"
		);
        modules.getTransforms().forEach(resource -> {
			assertTrue(resource.getFilename() + " is not a valid resource", validTransforms.contains(resource.getFilename()));
		});

        List<Resource> dirs = modules.getAssetDirectories();
        assertEquals(3, dirs.size());
        assertEquals("ext", dirs.get(0).getFile().getName());
        assertEquals("include-this-too", dirs.get(1).getFile().getName());
		assertEquals("root", dirs.get(2).getFile().getName());

        assertEquals(
                "Namespace files don't have to fit any filename format; the body of the file should be the namespace URI",
                1, modules.getNamespaces().size());
    }

    @Test
    public void emptyBaseDir() throws IOException {
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
