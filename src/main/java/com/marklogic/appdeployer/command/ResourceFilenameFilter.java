package com.marklogic.appdeployer.command;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;

import com.marklogic.client.helper.LoggingObject;

/**
 * Simple filter implementation that returns true for .json and .xml files.
 */
public class ResourceFilenameFilter extends LoggingObject implements FilenameFilter {

    private Set<String> filenamesToIgnore;

    public ResourceFilenameFilter() {
    }

    public ResourceFilenameFilter(String... filenamesToIgnore) {
        this.filenamesToIgnore = new HashSet<>();
        for (String f : filenamesToIgnore) {
            this.filenamesToIgnore.add(f);
        }
    }

    public ResourceFilenameFilter(Set<String> filenamesToIgnore) {
        this.filenamesToIgnore = filenamesToIgnore;
    }

    @Override
    public boolean accept(File dir, String name) {
        if (filenamesToIgnore != null && filenamesToIgnore.contains(name)) {
            if (logger.isInfoEnabled()) {
                logger.info("Ignoring filename: " + name);
            }
            return false;
        }
        return name.endsWith(".json") || name.endsWith(".xml");
    }

    public void setFilenamesToIgnore(Set<String> ignoreFilenames) {
        this.filenamesToIgnore = ignoreFilenames;
    }

    public Set<String> getFilenamesToIgnore() {
        return filenamesToIgnore;
    }

}
