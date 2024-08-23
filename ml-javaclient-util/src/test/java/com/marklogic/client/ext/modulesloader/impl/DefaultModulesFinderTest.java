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

        assertEquals(1, modules.getNamespaces().size(), "Namespace files don't have to fit any filename format; the body of the file should be the namespace URI");
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
