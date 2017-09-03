package com.marklogic.appdeployer.command;

import com.marklogic.client.ext.helper.LoggingObject;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Simple filter implementation that returns true for .json and .xml files.
 */
public class ResourceFilenameFilter extends LoggingObject implements FilenameFilter {

    private Set<String> filenamesToIgnore;
    private Pattern excludePattern;
    private Pattern includePattern;

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
    public boolean accept(File dir, String filename) {
    	if (excludePattern != null && includePattern != null) {
    		throw new IllegalStateException("Both excludePattern and includePattern cannot be specified");
	    }

	    if (excludePattern != null) {
    		if (excludePattern.matcher(filename).matches()) {
    			if (logger.isInfoEnabled()) {
    				logger.info(format("Filename %s matches excludePattern, so ignoring", filename));
			    }
			    return false;
		    }
	    }

	    if (includePattern != null) {
    		if (!includePattern.matcher(filename).matches()) {
    			if (logger.isInfoEnabled()) {
    				logger.info(format("Filename %s doesn't match includePattern, so ignoring", filename));
			    }
			    return false;
		    }
	    }

        if (filenamesToIgnore != null && filenamesToIgnore.contains(filename)) {
            if (logger.isInfoEnabled()) {
                logger.info("Ignoring filename: " + filename);
            }
            return false;
        }

        return filename.endsWith(".json") || filename.endsWith(".xml");
    }

    public void setFilenamesToIgnore(Set<String> ignoreFilenames) {
        this.filenamesToIgnore = ignoreFilenames;
    }

    public Set<String> getFilenamesToIgnore() {
        return filenamesToIgnore;
    }

	public Pattern getExcludePattern() {
		return excludePattern;
	}

	public void setExcludePattern(Pattern excludePattern) {
		this.excludePattern = excludePattern;
	}

	public Pattern getIncludePattern() {
		return includePattern;
	}

	public void setIncludePattern(Pattern includePattern) {
		this.includePattern = includePattern;
	}
}
