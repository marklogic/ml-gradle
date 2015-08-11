package com.rjrudin.marklogic.modulesloader.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.rjrudin.marklogic.modulesloader.Modules;
import com.rjrudin.marklogic.modulesloader.ModulesFinder;

public class DefaultModulesFinderTest extends Assert {

    private ModulesFinder sut = new DefaultModulesFinder();

    @Test
    public void baseDirWithExtensionsOfEachKind() throws IOException {
        Modules modules = sut.findModules(getBaseDir("sample-base-dir"));
        assertEquals(1, modules.getOptions().size());
        assertEquals("Only recognized XQuery files should be included; the XML file should be ignored", 2, modules
                .getServices().size());
        assertEquals("Only recognized XSL files should be included; the XML file should be ignored", 4, modules
                .getTransforms().size());

        List<File> dirs = modules.getAssetDirectories();
        assertEquals(2, dirs.size());
        assertEquals("ext", dirs.get(0).getName());
        assertEquals("root", dirs.get(1).getName());

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

    private File getBaseDir(String path) {
        try {
            return new ClassPathResource(path).getFile();
        } catch (IOException e) {
            throw new RuntimeException(path);
        }
    }
}
