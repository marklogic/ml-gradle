package com.marklogic.client.schemasloader.impl;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.file.DocumentFile;

public interface SchemaFileHandler {

	boolean canHandleSchemaFile(DocumentFile documentFile);

	DocumentWriteOperation handleSchemaFile(DocumentFile documentFile);
}
