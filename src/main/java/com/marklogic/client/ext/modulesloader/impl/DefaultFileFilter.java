package com.marklogic.client.ext.modulesloader.impl;

import java.io.File;
import java.io.FileFilter;

/**
 * Simple implementation that accepts every file and ignores anything starting with ".".
 */
public class DefaultFileFilter implements FileFilter {

    @Override
    public boolean accept(File f) {
        return f != null && !f.getName().startsWith(".");
    }

}
