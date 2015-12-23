package com.marklogic.client.modulesloader.impl;

import java.io.File;
import java.io.FileFilter;

/**
 * Simple implementation that accepts every file and ignores any directory starting with ".".
 */
public class AssetFileFilter implements FileFilter {

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return !f.getName().startsWith(".");
        }
        return true;
    }

}
