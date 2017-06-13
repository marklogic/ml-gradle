package com.marklogic.client.ext.file;

import java.util.List;

/**
 * Interface for an object that can load files as documents from many paths. These files could be modules, or schemas, or
 * content documents - it doesn't matter. The expected implementation of this is GenericFileLoader or a subclass of it,
 * where the subclass may have knowledge of a specific type of document to load.
 */
public interface FileLoader {

	List<DocumentFile> loadFiles(String... paths);
}
