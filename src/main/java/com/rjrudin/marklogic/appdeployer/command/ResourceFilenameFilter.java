package com.rjrudin.marklogic.appdeployer.command;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Simple filter implementation that returns true for .json and .xml files.
 */
public class ResourceFilenameFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
        return name.endsWith(".json") || name.endsWith(".xml");
    }

}
