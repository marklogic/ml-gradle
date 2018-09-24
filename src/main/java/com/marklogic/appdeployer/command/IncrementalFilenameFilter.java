package com.marklogic.appdeployer.command;

import java.io.FilenameFilter;

public interface IncrementalFilenameFilter extends FilenameFilter {

	public void addFilenameToIgnoreHash(String filename);

	public void clearFilenamesToIgnoreHash();

	public void setIncrementalMode(boolean incrementalMode);
}
