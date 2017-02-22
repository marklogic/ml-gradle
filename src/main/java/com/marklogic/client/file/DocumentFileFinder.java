package com.marklogic.client.file;

import java.util.List;

public interface DocumentFileFinder {

	List<DocumentFile> findDocumentFiles(String... paths);
}
