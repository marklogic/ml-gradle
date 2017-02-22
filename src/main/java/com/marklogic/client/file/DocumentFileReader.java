package com.marklogic.client.file;

import java.util.List;

/**
 * Strategy interface for determining which files to load into MarkLogic, with those files being captured as a List of
 * DocumentFile objects.
 */
public interface DocumentFileReader {

	List<DocumentFile> readDocumentFiles(String... paths);
}
