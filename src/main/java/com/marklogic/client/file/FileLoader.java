package com.marklogic.client.file;

import java.util.List;

/**
 * Interface for loading files from a set of paths, returning a list of the files that were loaded as DocumentFile
 * objects.
 */
public interface FileLoader {

	List<DocumentFile> loadFiles(String... paths);
}
