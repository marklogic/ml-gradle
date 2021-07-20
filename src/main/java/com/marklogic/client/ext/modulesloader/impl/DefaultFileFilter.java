package com.marklogic.client.ext.modulesloader.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

/**
 * Simple implementation that accepts every file and ignores anything starting with ".".
 */
public class DefaultFileFilter implements FileFilter, FilenameFilter {

    @Override
    public boolean accept(File f) {
    	return accept(null, f.getName());
    }

	/**
	 * @param dir
	 * @param name
	 * @return Ignores the directory, returns false if the name starts with ".", otherwise true
	 */
	@Override
	public boolean accept(File dir, String name) {
		return name != null && !name.startsWith(".");
	}
}
