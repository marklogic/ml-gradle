package com.marklogic.client.modulesloader.impl;

import java.io.File;
import java.io.FileFilter;

/**
 * Simple implementation that accepts every file and ignores anything starting with ".".
 */
public class AssetFileFilter implements FileFilter {

    @Override
    public boolean accept(File f) {
        return f != null && !f.getName().startsWith(".");
    }

}
