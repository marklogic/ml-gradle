package com.rjrudin.marklogic.modulesloader.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.rjrudin.marklogic.modulesloader.Asset;
import com.rjrudin.marklogic.modulesloader.Modules;
import com.rjrudin.marklogic.modulesloader.ModulesFinder;
import com.rjrudin.marklogic.modulesloader.impl.DefaultModulesFinder;

public class DefaultModulesFinderTest extends Assert {

    private ModulesFinder sut = new DefaultModulesFinder();

    @Test
    public void baseDirWithExtensionsOfEachKind() throws IOException {
        Modules files = sut.findModules(getBaseDir("sample-base-dir"));
        assertEquals(1, files.getOptions().size());
        assertEquals("Only recognized XQuery files should be included; the XML file should be ignored", 2, files
                .getServices().size());
        assertEquals("Only recognized XSL files should be included; the XML file should be ignored", 4, files
                .getTransforms().size());

        List<Asset> assets = files.getAssets();
        assertEquals(2, assets.size());
        assertEquals("/lib/module2.xqy", assets.get(0).getPath());
        assertEquals("/module1.xqy", assets.get(1).getPath());

        assertEquals(
                "Namespace files don't have to fit any filename format; the body of the file should be the namespace URI",
                1, files.getNamespaces().size());
    }

    @Test
    public void emptyBaseDir() throws IOException {
        Modules files = sut.findModules(getBaseDir("empty-base-dir"));
        assertEquals(0, files.getAssets().size());
        assertEquals(0, files.getOptions().size());
        assertEquals(0, files.getServices().size());
        assertEquals(0, files.getTransforms().size());
    }

    @Test
    public void versionControlFilesInAssetsDirectory() {
        Modules files = sut.findModules(getBaseDir("base-dir-with-version-control-files"));

        List<Asset> assets = files.getAssets();
        assertEquals(
                "The directory and file starting with . should have been ignored, as those are considered to be version control files",
                1, assets.size());
        assertEquals("Confirming that the only asset found was the xquery module", "sample.xqy", assets.get(0)
                .getFile().getName());
    }

    private File getBaseDir(String path) {
        try {
            return new ClassPathResource(path).getFile();
        } catch (IOException e) {
            throw new RuntimeException(path);
        }
    }
}
